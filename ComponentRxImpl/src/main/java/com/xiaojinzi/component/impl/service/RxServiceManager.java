package com.xiaojinzi.component.impl.service;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.error.RxJavaException;
import com.xiaojinzi.component.error.ServiceInvokeException;
import com.xiaojinzi.component.error.ServiceNotFoundException;
import com.xiaojinzi.component.support.Utils;

import org.reactivestreams.Publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

/**
 * 关于 Rx版本的增强版本,使用这个类在服务上出现的任何错误,如果您不想处理
 * 这里都能帮您自动过滤掉,如果你写了错误处理,则会回调给您
 * time   : 2019/01/09
 *
 * @author : xiaojinzi 30212
 */
public class RxServiceManager {

    private RxServiceManager() {
    }

    /**
     * 这里最主要的实现就是把出现的错误转化为 {@link RxJavaException} 和 {@link ServiceInvokeException}
     * 然后就可以当用户不想处理RxJava的错误的时候 {@link com.xiaojinzi.component.support.RxErrorConsumer} 进行忽略了
     * 获取实现类,这个方法实现了哪些这里罗列一下：
     * 1. 保证在找不到服务实现类的时候不会有异常,你只管写正确情况的逻辑代码
     * 2. 保证服务那个实现类在主线程上被创建
     * 3. 在保证了第一点的情况下保证不改变 RxJava 的执行线程
     * 4. 保证调用任何一个服务实现类的时候出现的错误用 {@link ServiceInvokeException}
     * 代替,当然了,真实的错误在 {@link Throwable#getCause()} 中可以获取到
     * 5. 如果服务方法返回的是 RxJava 的五种 Observable : [Single,Observable,Flowable,Maybe,Completable]
     * 当错误走了 RxJava 的OnError,这里也会把错误包装成 {@link RxJavaException},真实错误在 {@link Throwable#getCause()} 中
     *
     * @param tClass
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> Single<T> with(@NonNull final Class<T> tClass) {
        return Single.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                T tempImpl = null;
                if (Utils.isMainThread()) {
                    tempImpl = ServiceManager.get(tClass);
                } else {
                    // 这段代码如何为空的话会直接抛出异常
                    tempImpl = blockingGetInChildThread(tClass);
                }
                final T serviceImpl = tempImpl;
                if (serviceImpl == null) {
                    throw new ServiceNotFoundException(tClass.getName());
                }
                // 这个是为了让每一个错误都能被管控,然后如果用户不想处理的话,我这边都自动忽略掉
                return proxy(tClass, serviceImpl);
            }


        });
    }

    /**
     * 创建代理对象包装错误
     *
     * @param tClass
     * @param serviceImpl
     * @param <T>
     * @return
     */
    @NonNull
    private static <T> T proxy(@NonNull final Class<T> tClass, final T serviceImpl) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    Class<?> returnType = method.getReturnType();
                    // 1 : Single
                    // 2 : Observable
                    // 3 : Flowable
                    // 4 : Maybe
                    // 5 : Completable
                    int rxType = -1;
                    // 如果是 RxJava 的五种形式
                    if (returnType == Single.class) {
                        rxType = 1;
                    } else if (returnType == Observable.class) {
                        rxType = 2;
                    } else if (returnType == Flowable.class) {
                        rxType = 3;
                    } else if (returnType == Maybe.class) {
                        rxType = 4;
                    } else if (returnType == Completable.class) {
                        rxType = 5;
                    }
                    // 拿到方法执行的对象,如果对象是 [Observable] 系列中的五个
                    Object result = method.invoke(serviceImpl, args);
                    if (rxType == 1) {
                        result = ((Single) result).onErrorResumeNext(new Function<Throwable, SingleSource>() {
                            @Override
                            public SingleSource apply(Throwable throwable) throws Exception {
                                return Single.error(new RxJavaException(throwable));
                            }
                        });
                    } else if (rxType == 2) {
                        result = ((Observable) result).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                            @Override
                            public ObservableSource apply(Throwable throwable) throws Exception {
                                return Observable.error(new RxJavaException(throwable));
                            }
                        });
                    } else if (rxType == 3) {
                        result = ((Flowable) result).onErrorResumeNext(new Function<Throwable, Publisher>() {
                            @Override
                            public Publisher apply(Throwable throwable) throws Exception {
                                return Flowable.error(new RxJavaException(throwable));
                            }
                        });
                    } else if (rxType == 4) {
                        result = ((Maybe) result).onErrorResumeNext(new Function<Throwable, MaybeSource>() {
                            @Override
                            public MaybeSource apply(Throwable throwable) throws Exception {
                                return Maybe.error(new RxJavaException(throwable));
                            }
                        });
                    } else if (rxType == 5) {
                        result = ((Completable) result).onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                            @Override
                            public CompletableSource apply(Throwable throwable) throws Exception {
                                return Completable.error(new RxJavaException(throwable));
                            }
                        });
                    }
                    return result;
                } catch (Exception e) {
                    throw new ServiceInvokeException(e);
                }
            }
        });
    }

    /**
     * 在主线程中去创建对象,然后在其他线程拿到
     *
     * @param tClass
     * @param <T>
     * @return
     */
    private static <T> T blockingGetInChildThread(@NonNull final Class<T> tClass) {
        return Single.create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(final SingleEmitter<T> emitter) throws Exception {
                // 主线程去获取,因为框架任何一个用户自定义的类创建的时候都会在主线程上被创建
                Utils.postActionToMainThread(new Runnable() {
                    @Override
                    public void run() {
                        T t = ServiceManager.get(tClass);
                        if (emitter.isDisposed()) {
                            return;
                        }
                        if (t == null) {
                            emitter.onError(new ServiceNotFoundException("class:" + tClass.getName()));
                        } else {
                            emitter.onSuccess(t);
                        }
                    }
                });
            }
        }).blockingGet();
    }

}
