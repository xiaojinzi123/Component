package com.ehi.api.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
     * @param context
     * @param uri
     * @param bundle
     * @return
     */
    boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle);

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
     * 是否匹配uri
     *
     * @param uri
     * @return
     */
    boolean isMatchUri(@NonNull Uri uri);

}
