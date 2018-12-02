package com.ehi.component.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * 这个类表示一次路由的返回结果对象,失败的时候直接跑出异常了,不会生成这个对象
 *
 * time   : 2018/11/10
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterExecuteResult implements Serializable {

    @NonNull
    public final EHiRouterRequest request;

    public EHiRouterExecuteResult(@NonNull EHiRouterRequest request) {
        this.request = request;
    }

}
