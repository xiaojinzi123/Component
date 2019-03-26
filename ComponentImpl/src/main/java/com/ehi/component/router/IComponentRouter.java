package com.ehi.component.router;

import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.RouterRequest;

import java.util.List;

/**
 * 组件之间实现跳转的接口
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentRouter {

    /**
     * 打开一个链接实现跳转
     *
     * @param routerRequest
     * @return
     * @throws android.content.ActivityNotFoundException
     */
    @MainThread
    void openUri(@NonNull RouterRequest routerRequest) throws Exception;

    /**
     * 是否有匹配的 uri
     *
     * @param uri 要打开的界面 Uri
     * @return 返回的值表示是否路由表中有匹配的目标界面
     */
    boolean isMatchUri(@NonNull Uri uri);

    /**
     * 获取这个目标要执行的拦截器
     *
     * @return
     */
    @NonNull
    List<EHiRouterInterceptor> interceptors(@NonNull Uri uri) throws Exception;

}
