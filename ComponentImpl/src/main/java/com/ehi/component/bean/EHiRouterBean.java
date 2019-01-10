package com.ehi.component.bean;

import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiRouterInterceptor;

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
     * 这是也是目标界面要执行的拦截器,不过这个是字符串表示的
     * 更加的跨越模块,但是寻找可能就没有上面的方式来的直接了
     */
    public List<String> interceptorNames;

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

    /**
     * 用户自定义的 跳转
     */
    @Nullable
    public CustomerJump customerJump;


}
