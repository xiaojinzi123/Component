package com.xiaojinzi.component.bean;

import javax.lang.model.element.Element;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
public class RouterDegradeAnnoBean {

    /**
     * 优先级
     */
    private int priority;

    /**
     * 是一个类实现了 RouterDegrade 接口
     */
    private Element rawType;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }

}
