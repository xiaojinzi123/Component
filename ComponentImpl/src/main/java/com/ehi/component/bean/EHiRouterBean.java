package com.ehi.component.bean;

import android.support.annotation.Nullable;

import com.ehi.component.impl.RouterInterceptor;
import com.ehi.component.impl.interceptor.InterceptorCenter;

import java.util.List;

/**
 * 和注解{@link com.ehi.component.anno.EHiRouterAnno}是--对应的
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterBean {

    /**
     * 可能会生成文档之类的
     */
    @Nullable
    private String desc;

    /**
     * uri 的 host
     */
    private String host;

    /**
     * uri 的 path
     */
    private String path;

    /**
     * 这个目标 Activity Class,可能为空,因为可能标记在静态方法上
     */
    @Nullable
    private Class targetClass;

    /**
     * 这个目标界面要执行的拦截器
     */
    @Nullable
    private List<Class<? extends RouterInterceptor>> interceptors;

    /**
     * 这是也是目标界面要执行的拦截器,不过这个是字符串表示的
     * 更加的跨越模块,但是寻找可能就没有上面的方式来的直接了
     *
     * @see InterceptorCenter#getByName(String)
     */
    @Nullable
    private List<String> interceptorNames;

    /**
     * 用户自定义的 {@link android.content.Intent},
     * 当标记在静态方法上并且返回值是 {@link android.content.Intent} 的时候会有值
     */
    @Nullable
    private CustomerIntentCall customerIntentCall;

    /**
     * 用户自定义的 跳转
     * 当标记在静态方法上并且返回值是 void 的时候会有值
     */
    @Nullable
    private CustomerJump customerJump;

    @Nullable
    public String getDesc() {
        return desc;
    }

    public void setDesc(@Nullable String desc) {
        this.desc = desc;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Nullable
    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Nullable
    public List<Class<? extends RouterInterceptor>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(@Nullable List<Class<? extends RouterInterceptor>> interceptors) {
        this.interceptors = interceptors;
    }

    @Nullable
    public List<String> getInterceptorNames() {
        return interceptorNames;
    }

    public void setInterceptorNames(@Nullable List<String> interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    @Nullable
    public CustomerIntentCall getCustomerIntentCall() {
        return customerIntentCall;
    }

    public void setCustomerIntentCall(@Nullable CustomerIntentCall customerIntentCall) {
        this.customerIntentCall = customerIntentCall;
    }

    public void setCustomerJump(@Nullable CustomerJump customerJump) {
        this.customerJump = customerJump;
    }

    @Nullable
    public CustomerJump getCustomerJump() {
        return customerJump;
    }

}
