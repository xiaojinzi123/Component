package com.ehi.component.bean;

import android.support.annotation.NonNull;

import com.ehi.component.impl.EHiRouterRequest;

/**
 * 自定义跳转的时候,会调用的接口
 * time   : 2019/01/07
 *
 * @author : xiaojinzi 30212
 */
public interface CustomerJump {

    /**
     * 跳转
     *
     * @param request
     */
    void jump(@NonNull EHiRouterRequest request) throws Exception;

}
