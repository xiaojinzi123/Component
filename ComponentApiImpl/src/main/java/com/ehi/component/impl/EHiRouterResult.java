package com.ehi.component.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * 这个类表示一次路由的返回结果对象
 * time   : 2018/11/10
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterResult implements Serializable {

    /**
     * 如果成功了,这个会有值
     */
    @NonNull
    private EHiRouterRequest request;

    public EHiRouterResult(@NonNull EHiRouterRequest request) {
        this.request = request;
    }

    @NonNull
    public EHiRouterRequest getRequest() {
        return request;
    }

    public void setRequest(@NonNull EHiRouterRequest request) {
        this.request = request;
    }

}
