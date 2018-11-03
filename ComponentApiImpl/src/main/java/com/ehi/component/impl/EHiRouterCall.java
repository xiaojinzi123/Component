package com.ehi.component.impl;

/**
 * 当你发起一个请求跳转的时候,可以选择直接跳转或者生成一个 Call 对象,然后调用
 * time   : 2018/11/02
 *
 * @author : xiaojinzi 30212
 */
public interface EHiRouterCall {

    /**
     * 调用此方法会执行跳转
     *
     * @return
     */
    boolean execute();

}
