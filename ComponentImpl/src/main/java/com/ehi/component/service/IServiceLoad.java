package com.ehi.component.service;

/**
 * 服务提供出去的懒加载设计
 * time   : 2018/11/27
 *
 * @author : xiaojinzi 30212
 */
public interface IServiceLoad<T> {

    /**
     * 获取实际的兑现
     *
     * @return
     */
    T get();

}
