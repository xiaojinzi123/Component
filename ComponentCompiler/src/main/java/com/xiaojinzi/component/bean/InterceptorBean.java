package com.xiaojinzi.component.bean;

import com.xiaojinzi.component.anno.support.CheckClassName;

import javax.lang.model.element.Element;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
@CheckClassName
public class InterceptorBean {

    public static final int GLOBAL_INTERCEPTOR = 0;
    public static final int NORMAL_INTERCEPTOR = 1;

    /**
     * 0 全局拦截器
     * 1 普通拦截器
     */
    public final int type;

    public final Element element;

    public final int priority;

    public final String name;

    public InterceptorBean(int type, Element element, int priority, String name) {
        this.type = type;
        this.element = element;
        this.priority = priority;
        this.name = name;
    }

}
