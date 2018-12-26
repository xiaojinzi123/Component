package com.ehi.component.bean;

import javax.lang.model.element.Element;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public class InterceptorBean {

    public Element element;

    public int priority;

    public InterceptorBean(Element element, int priority) {
        this.element = element;
        this.priority = priority;
    }

}
