package com.ehi.api.bean;

import javax.lang.model.element.Element;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class RouterBean {

    private String path;
    private String desc;
    private int priority = -1;
    private Element rawType;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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
