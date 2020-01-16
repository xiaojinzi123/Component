package com.xiaojinzi.component.router;

import android.net.Uri;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;

import java.util.List;

/**
 * 组件之间实现跳转的接口
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
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
     * 获取这个目标要执行的页面拦截器
     *
     * @return 返回此界面的页面拦截器
     * @throws Exception
     */
    @NonNull
    @MainThread
    List<RouterInterceptor> listPageInterceptors(@NonNull Uri uri) throws Exception;

    /**
     * 获取该 URI 对应的降级需要执行的拦截器
     *
     * @param uri
     * @return
     * @throws Exception
     */
    @NonNull
    @MainThread
    List<RouterInterceptor> listDegradeInterceptors(@NonNull Uri uri) throws Exception;

}
