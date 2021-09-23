package com.xiaojinzi.component.bean;

import androidx.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;

/**
 * {@link #stringInterceptor} 和 {@link #classInterceptor} 必须有一个是有值的
 */
@CheckClassNameAnno
public class PageInterceptorBean {

    /**
     * 优先级
     */
    private int priority;

    @Nullable
    private String stringInterceptor;

    @Nullable
    private Class<? extends RouterInterceptor> classInterceptor;

    public PageInterceptorBean(int priority, @Nullable String stringInterceptor) {
        this.priority = priority;
        this.stringInterceptor = stringInterceptor;
    }

    public PageInterceptorBean(int priority,
                               @Nullable Class<? extends RouterInterceptor> classInterceptor) {
        this.priority = priority;
        this.classInterceptor = classInterceptor;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Nullable
    public String getStringInterceptor() {
        return stringInterceptor;
    }

    public void setStringInterceptor(@Nullable String stringInterceptor) {
        this.stringInterceptor = stringInterceptor;
    }

    @Nullable
    public Class<? extends RouterInterceptor> getClassInterceptor() {
        return classInterceptor;
    }

    public void setClassInterceptor(@Nullable Class<? extends RouterInterceptor> classInterceptor) {
        this.classInterceptor = classInterceptor;
    }

}
