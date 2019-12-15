package com.xiaojinzi.component.impl.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.error.FragmentNotFoundException;
import com.xiaojinzi.component.error.ServiceInvokeException;
import com.xiaojinzi.component.support.Utils;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * 关于 Rx版本的增强版本,使用这个类在服务上出现的任何错误,如果您不想处理
 * 这里都能帮您自动过滤掉,如果你写了错误处理,则会回调给您
 * time   : 2019/10/12
 *
 * @author : xiaojinzi
 */
public class RxFragmentManager {

    private RxFragmentManager() {
    }

    /**
     * 这里最主要的实现就是把出现的错误转化为 {@link ServiceInvokeException}
     * 然后就可以当用户不想处理RxJava的错误的时候 {@link com.xiaojinzi.component.support.RxErrorConsumer} 进行忽略了
     * 获取实现类,这个方法实现了哪些这里罗列一下：
     * 1. 保证在找不到 Fragment 的时候不会有异常, 你只管写正确情况的逻辑代码
     * 2. 保证 Fragment 在主线程上被创建
     * 3. 在保证了第一点的情况下保证不改变 RxJava 的执行线程
     * 4. 保证调用任何一个服务实现类的时候出现的错误用 {@link ServiceInvokeException}
     * 代替,当然了,真实的错误在 {@link Throwable#getCause()} 中可以获取到
     *
     * @param fragmentFlag
     * @param bundle
     * @return
     */
    @NonNull
    public static Single<Fragment> with(@NonNull final String fragmentFlag, @Nullable final Bundle bundle) {
        return Single.fromCallable(new Callable<Fragment>() {
            @Override
            public Fragment call() throws Exception {
                Fragment tempImpl = null;
                if (Utils.isMainThread()) {
                    tempImpl = FragmentManager.get(fragmentFlag, bundle);
                } else {
                    // 这段代码如何为空的话会直接抛出异常
                    tempImpl = blockingGetInChildThread(fragmentFlag, bundle);
                }
                final Fragment serviceImpl = tempImpl;
                if (serviceImpl == null) {
                    throw new FragmentNotFoundException(fragmentFlag);
                }
                return serviceImpl;
            }


        });
    }

    /**
     * 在主线程中去创建对象,然后在其他线程拿到
     *
     * @param fragmentFlag
     * @param bundle
     * @return
     */
    @NonNull
    private static Fragment blockingGetInChildThread(@NonNull final String fragmentFlag, @Nullable final Bundle bundle) {
        return Single.create(new SingleOnSubscribe<Fragment>() {
            @Override
            public void subscribe(final SingleEmitter<Fragment> emitter) throws Exception {
                // 主线程去获取,因为框架任何一个用户自定义的类创建的时候都会在主线程上被创建
                Utils.postActionToMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        Fragment fragment = FragmentManager.get(fragmentFlag, bundle);
                        if (fragment == null) {
                            emitter.onError(new FragmentNotFoundException("fragmentFlag is '+fragmentFlag+'"));
                        } else {
                            emitter.onSuccess(fragment);
                        }
                    }
                });
            }
        }).blockingGet();
    }

}
