package com.ehi.component.impl;

import com.ehi.component2.view.Component2Act;
import com.ehi.component2.view.Component2TestAct;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;

/**
 * component2业务模块的路由表
 */
final class Component2RouterGenerated extends EHiModuleRouterImpl {
    @Override
    public String getHost() {
        return "component2";
    }

    /**
     * 初始化路由表的数据
     */
    @Override
    public void initMap() {
        super.initMap();

        // ---------------------------com.ehi.component2.view.Component2Act---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean1 = new com.ehi.component.bean.EHiRouterBean();
        routerBean1.host = "component2";
        routerBean1.path = "main";
        routerBean1.desc = " 业务组件2的主界面 ";
        routerBean1.targetClass = Component2Act.class;
        routerBeanMap.put("component2/main",routerBean1);


        // ---------------------------com.ehi.component2.view.Component2TestAct---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean2 = new com.ehi.component.bean.EHiRouterBean();
        routerBean2.host = "";
        routerBean2.path = "component2/test";
        routerBean2.desc = "";
        routerBean2.targetClass = Component2TestAct.class;
        routerBean2.interceptorNames = new ArrayList();
        routerBean2.interceptorNames.add("component1.test");
        routerBeanMap.put("component2/component2/test",routerBean2);

    }
}
