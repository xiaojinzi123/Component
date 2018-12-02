package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * 当参数刚刚构建出RouterRequest的时候,这时候你可以随意的改值
 * time   : 2018/11/29
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterPreRequest {

    @Nullable
    public Context context;

    @Nullable
    public Fragment fragment;

    @NonNull
    public Uri uri;

    @Nullable
    public Integer requestCode;

    @Nullable
    public Bundle bundle = null;

    public EHiRouterPreRequest() {
    }

    public EHiRouterPreRequest(@NonNull EHiRouterPreRequest routerPreRequest) {
        this.context = routerPreRequest.context;
        this.fragment = routerPreRequest.fragment;
        this.uri = routerPreRequest.uri;
        this.requestCode = routerPreRequest.requestCode;
        this.bundle = routerPreRequest.bundle;
    }

}
