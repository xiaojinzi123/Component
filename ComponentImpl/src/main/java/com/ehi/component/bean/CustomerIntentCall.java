package com.ehi.component.bean;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ehi.component.impl.EHiRouterRequest;

/**
 * 跳转的 Intent 用户自定义,{@link com.ehi.component.anno.EHiRouterAnno} 注解标记在静态方法上的时候
 * 当返回值是 {@link Intent} 的时候,就会产生一个此接口的实现,实现获取目标界面跳转的 {@link Intent}
 */
public interface CustomerIntentCall {

    /**
     * 获取创建的 Intent
     *
     * @param request 路由请求对象
     * @return
     */
    @NonNull
    Intent get(@NonNull EHiRouterRequest request) throws Exception;

}