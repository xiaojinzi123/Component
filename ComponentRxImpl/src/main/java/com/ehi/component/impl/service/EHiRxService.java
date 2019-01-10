package com.ehi.component.impl.service;

import android.support.annotation.NonNull;

import com.ehi.component.error.ServiceInvokeException;
import com.ehi.component.error.ServiceNotFoundException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * 关于 Rx版本的增强版本,使用这个类在服务上出现的任何错误,如果您不想处理
 * 这里都能帮您自动过滤掉,如果你写了错误处理,则会回调给您
 * time   : 2019/01/09
 *
 * @author : xiaojinzi 30212
 */
public class EHiRxService extends EHiService {

    /**
     * 获取实现类
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Single<T> with(@NonNull final Class<T> tClass) {
        return Single.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final T serviceImpl = get(tClass);
                if (serviceImpl == null) {
                    throw new ServiceNotFoundException();
                }
                // 这个是为了让每一个错误都能被管控,然后如果用户不想处理的话,我这边都自动忽略掉
                T result = (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try {
                            return method.invoke(serviceImpl, args);
                        } catch (Exception e) {
                            throw new ServiceInvokeException(e);
                        }
                    }
                });
                return result;
            }
        });
    }

}
