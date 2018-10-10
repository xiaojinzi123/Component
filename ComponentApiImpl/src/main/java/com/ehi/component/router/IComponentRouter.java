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
     * @param context
     * @param uri
     * @return
     */
    boolean openUri(@NonNull Context context, @NonNull Uri uri);

    /**
     * 打开一个链接实现跳转
     *
     * @param fragment
     * @param uri
     * @return
     */
    boolean fopenUri(@NonNull Fragment fragment, @NonNull Uri uri);

    /**
     * 打开一个链接实现跳转
     *
     * @param context
     * @param uri
     * @param bundle
     * @return
     */
    boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle);

    /**
     * 打开一个链接实现跳转
     *
     * @param fragment
     * @param uri
     * @param bundle
     * @return
     */
    boolean fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle);

    /**
     * 打开一个链接,并且有请求码
     *
     * @param context
     * @param uri
     * @param bundle
     * @param requestCode
     * @return
     */
    boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode);

    /**
     * 打开一个链接,并且有请求码
     *
     * @param fragment
     * @param uri
     * @param bundle
     * @param requestCode
     * @return
     */
    boolean fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode);

    /**
     * 是否匹配uri
     *
     * @param uri
     * @return
     */
    boolean isMatchUri(@NonNull Uri uri);

    /**
     * 是否需要登录
     *
     * @param uri
     * @return
     */
    boolean isNeedLogin(@NonNull Uri uri);

}
