package com.ehi.component.bean;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.support.EHiRouterInterceptor;

import java.util.List;

/**
 * 和注解是对应的
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterBean {

    /**
     * uri 的 host
     */
    public String host;

    /**
     * uri 的 path
     */
    public String path;

    /**
     * 这个目标 Activity Class
     */
    public Class targetClass;

    /**
     * 这个目标界面要执行的拦截器
     */
    public List<Class<? extends EHiRouterInterceptor>> interceptors;

    /**
     * 可能会生成文档之类的
     */
    @Nullable
    public String desc;

    /**
     * 用户自定义的 Intent
     */
    @Nullable
    public CustomerIntentCall customerIntentCall;


}
