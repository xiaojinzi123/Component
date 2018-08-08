package com.ehi.api.router;

/**
 * 组件之间实现跳转的接口
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentHostRouter extends IComponentRouter {

    /**
     * 获取host
     *
     * @return
     */
    String getHost();

}
