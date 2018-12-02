package com.ehi.component.router;

import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiRouterRequest;

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
    void openUri(@NonNull EHiRouterRequest routerRequest) throws Exception;

    /**
     * 是否有匹配的 uri
     *
     * @param uri 要打开的界面 Uri
     * @return 返回的值表示是否路由表中有匹配的目标界面
     */
    @MainThread
    boolean isMatchUri(@NonNull Uri uri);

    /**
     * 是否需要登录,如果返回null表示路由表中根本就没有匹配的目标界面
     * 所以根本就判断不出来,所以返回一个 null
     *
     * @param uri 要打开的界面 Uri
     * @return 返回的值表示目标界面是否需要登录
     */
    @Nullable
    Boolean isNeedLogin(@NonNull Uri uri);

}
