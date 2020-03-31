package com.xiaojinzi.component.bean;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;

/**
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
public class RouterAnnoBean {

    private String host;
    private String path;
    private String desc;

    private int totalInterceptorSize;
    private int[] interceptorPriorities;
    private int[] interceptorNamePriorities;
    // value是实现类的全类名
    private List<String> interceptors = new ArrayList<>(1);
    // 拦截器的一个别名
    private List<String> interceptorNames = new ArrayList<>(1);
    // 可是是一个Activity 类或者是一个静态方法
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

    public int getTotalInterceptorSize() {
        return totalInterceptorSize;
    }

    public void setTotalInterceptorSize(int totalInterceptorSize) {
        this.totalInterceptorSize = totalInterceptorSize;
    }

    public int[] getInterceptorPriorities() {
        return interceptorPriorities;
    }

    public void setInterceptorPriorities(int[] interceptorPriorities) {
        this.interceptorPriorities = interceptorPriorities;
    }

    public int[] getInterceptorNamePriorities() {
        return interceptorNamePriorities;
    }

    public void setInterceptorNamePriorities(int[] interceptorNamePriorities) {
        this.interceptorNamePriorities = interceptorNamePriorities;
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

    public String hostAndPath() {
        return host + path;
    }

}
