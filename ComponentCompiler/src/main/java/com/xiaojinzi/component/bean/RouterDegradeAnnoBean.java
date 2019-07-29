package com.xiaojinzi.component.bean;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class RouterDegradeAnnoBean {

    /**
     * 优先级
     */
    private int priority;

    /**
     * value是实现类的全类名
     */
    private List<String> interceptors = new ArrayList<>(1);

    /**
     * 拦截器的一个别名
     */
    private List<String> interceptorNames = new ArrayList<>(1);

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

    public List<String> getInterceptors() {
        return interceptors;
    }

    public List<String> getInterceptorNames() {
        return interceptorNames;
    }

}
