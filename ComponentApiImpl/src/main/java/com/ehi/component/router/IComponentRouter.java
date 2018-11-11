package com.ehi.component.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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
     * @param context 上下文,一般是 Activity
     * @param uri     目标界面的 Uri
     * @return
     * @throws android.content.ActivityNotFoundException
     */
    void openUri(@NonNull Context context, @NonNull Uri uri) throws Exception;

    /**
     * 打开一个链接实现跳转
     *
     * @param fragment
     * @param uri
     * @return
     * @throws android.content.ActivityNotFoundException
     */
    void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri) throws Exception;

    /**
     * 打开一个链接实现跳转
     *
     * @param context
     * @param uri
     * @param bundle  额外的信息携带对象,系统的 {@link Bundle} 对象可以被序列化
     * @return
     */
    void openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle) throws Exception;

    /**
     * 打开一个链接实现跳转
     *
     * @param fragment
     * @param uri
     * @param bundle
     * @return
     */
    void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle) throws Exception;

    /**
     * 打开一个链接,并且有请求码
     *
     * @param context
     * @param uri
     * @param bundle
     * @param requestCode 界面的请求码
     * @return
     */
    void openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) throws Exception;

    /**
     * 打开一个链接,并且有请求码
     *
     * @param fragment
     * @param uri
     * @param bundle
     * @param requestCode 界面的请求码
     * @return
     */
    void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) throws Exception;

    /**
     * 是否有匹配的 uri
     *
     * @param uri 要打开的界面 Uri
     * @return 返回的值表示是否路由表中有匹配的目标界面
     */
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
