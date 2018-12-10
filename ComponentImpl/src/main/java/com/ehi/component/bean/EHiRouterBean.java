package com.ehi.component.bean;

import com.ehi.component.support.EHiRouterInterceptor;

import java.util.List;

/**
 * 和注解是对应的
 * <p>
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterBean {

    public String host;

    public String path;

    public Class targetClass;

    public List<Class<? extends EHiRouterInterceptor>> interceptors;

    public String desc;

}
