package com.ehi.component.impl;

import android.support.annotation.NonNull;

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
    private final EHiRouterRequest request;

    public EHiRouterResult(@NonNull EHiRouterRequest request) {
        this.request = request;
    }

    @NonNull
    public EHiRouterRequest getRequest() {
        return request;
    }


}
