package com.ehi.component.bean;

import javax.lang.model.element.Element;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class RouterBean {

    private String host;
    private String path;
    private String desc;
    //private int priority = -1;
    private Element rawType;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }
}
