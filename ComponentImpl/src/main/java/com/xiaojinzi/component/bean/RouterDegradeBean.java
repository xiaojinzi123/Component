package com.xiaojinzi.component.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.impl.RouterDegrade;
import com.xiaojinzi.component.impl.RouterInterceptor;

import java.util.Collections;
import java.util.List;

public class RouterDegradeBean {

    /**
     * 优先级
     */
    private int priority;

    /**
     * 这个目标 Activity Class,可能为空,因为可能标记在静态方法上
     */
    @NonNull
    private Class<? extends RouterDegrade> targetClass;

    /**
     * 这个目标界面要执行的拦截器
     */
    @Nullable
    private List<Class<? extends RouterInterceptor>> interceptors;

    /**
     * 这个目标界面要执行的拦截器
     */
    @Nullable
    private List<String> interceptorNames;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NonNull
    public Class<? extends RouterDegrade> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(@NonNull Class<? extends RouterDegrade> targetClass) {
        this.targetClass = targetClass;
    }

    @NonNull
    public List<Class<? extends RouterInterceptor>> getInterceptors() {
        if (interceptors == null) {
            return Collections.emptyList();
        }
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

}
