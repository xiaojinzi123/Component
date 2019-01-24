package com.ehi.component.impl;

import com.ehi.user.view.Component3Act;
import java.lang.Override;
import java.lang.String;

/**
 * user业务模块的路由表
 */
final class UserRouterGenerated extends EHiModuleRouterImpl {
    @Override
    public String getHost() {
        return "user";
    }

    /**
     * 初始化路由表的数据
     */
    @Override
    public void initMap() {
        super.initMap();

        // ---------------------------com.ehi.user.view.Component3Act---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean1 = new com.ehi.component.bean.EHiRouterBean();
        routerBean1.host = "user";
        routerBean1.path = "login";
        routerBean1.desc = "用户模块的登录界面";
        routerBean1.targetClass = Component3Act.class;
        routerBeanMap.put("user/login",routerBean1);

    }
}
