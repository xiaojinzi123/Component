package com.ehi.component.bean;

import javax.lang.model.element.Element;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public class InterceptorBean {

    public static final int GLOBAL_INTERCEPTOR = 0;
    public static final int NORMAL_INTERCEPTOR = 1;


    /**
     * 0 全局拦截器
     * 1 普通拦截器
     */
    public int type;

    public Element element;

    public int priority;

    public String name;

    public InterceptorBean(int type, Element element, int priority, String name) {
        this.type = type;
        this.element = element;
        this.priority = priority;
        this.name = name;
    }

}
