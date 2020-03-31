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
     * Note: 不对外公开使用！
     * 打开一个链接实现跳转
     *
     * @throws android.content.ActivityNotFoundException
     */
    @MainThread
    void openUri(@NonNull RouterRequest routerRequest) throws Exception;

    /**
     * Note: 不对外公开使用！
     * <p>
     * 是否有匹配的 uri
     * 此方法仅供框架内部使用, 请勿在自己项目中根据此 Api 来判断一个 {@link Uri} 是否有匹配的路由目标
     * 原因如下：
     * 当发生真正的路由的时候, 一个路由请求对象 {@link RouterRequest} 会经过若干个拦截器 {@link RouterInterceptor}
     * 所以路由请求对象 {@link RouterRequest} 内部的 {@link Uri} 有可能会发生变化. 所以就会有以下的情况发生.
     * 如果你在项目中使用此 Api 进行一个 {@link Uri} 的判断是否有匹配的路由目标.
     * 不管你当时拿到的结果是 true 还是 false, 这都不能代表你最终路由的时候, 是否确定能找到目标. 原因如上已经解释了
     * 总结一句话就是：根据一个 {@link Uri} 无法得知是否一定有路由的目标, 因为路由的过程 {@link RouterRequest}
     * 会发生变化
     *
     * @param uri 要打开的界面 Uri
     * @return 返回的值表示是否路由表中有匹配的目标界面
     */
    boolean isMatchUri(@NonNull Uri uri);

    /**
     * 比较两个 {@link Uri} 是否是同一个目标.
     * 这两个 {@link Uri#equals(Object)} 不一定相等的.
     * 因为这里比较的是内部的信息是否指向了同一个目标
     *
     * @param uri1 第一个 {@link Uri}
     * @param uri2 第二个 {@link Uri}
     * @return 返回两个 {@link Uri} 是否指向同一个目标
     */
    boolean isSameTarget(@NonNull Uri uri1, @NonNull Uri uri2);

    /**
     * Note: 不对外公开使用！
     * 获取这个目标要执行的页面拦截器
     *
     * @return 返回此界面的页面拦截器
     * @throws Exception
     */
    @NonNull
    @MainThread
    List<RouterInterceptor> listPageInterceptors(@NonNull Uri uri) throws Exception;

    /**
     * Note: 不对外公开使用！
     * 获取该 URI 对应的降级需要执行的拦截器
     */
    @NonNull
    @MainThread
    List<RouterInterceptor> listDegradeInterceptors(@NonNull Uri uri) throws Exception;

}
