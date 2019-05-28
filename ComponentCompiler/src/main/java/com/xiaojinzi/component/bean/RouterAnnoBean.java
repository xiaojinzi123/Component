package com.xiaojinzi.component.bean;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class RouterAnnoBean {

    private String desc;
    // value是实现类的全类名
    private List<String> interceptors = new ArrayList<>(1);
    // 拦截器的一个别名
    private List<String> interceptorNames = new ArrayList<>(1);
    // 可是是一个Activity 类或者是一个静态方法
    private Element rawType;

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

    public List<String> getInterceptors() {
        return interceptors;
    }

    public List<String> getInterceptorNames() {
        return interceptorNames;
    }
}
