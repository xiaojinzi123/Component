package com.ehi.component.impl;

import com.ehi.base.interceptor.DialogShowInterceptor;
import com.ehi.component1.view.Component1TestAct;
import com.ehi.component1.view.Component1TestDialogAct;
import com.ehi.component1.view.Component1TestLoginAct;
import com.ehi.component1.view.Component1TestQueryAct;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;

/**
 * component1业务模块的路由表
 */
final class Component1RouterGenerated extends EHiModuleRouterImpl {
    @Override
    public String getHost() {
        return "component1";
    }

    /**
     * 初始化路由表的数据
     */
    @Override
    public void initMap() {
        super.initMap();

        // ---------------------------com.ehi.component1.view.Component1TestAct---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean1 = new com.ehi.component.bean.EHiRouterBean();
        routerBean1.host = "component1";
        routerBean1.path = "test";
        routerBean1.desc = "业务组件1的测试界面";
        routerBean1.targetClass = Component1TestAct.class;
        routerBean1.interceptorNames = new ArrayList();
        routerBean1.interceptorNames.add("component1.test");
        routerBeanMap.put("component1/test",routerBean1);


        // ---------------------------com.ehi.component1.view.Component1TestQueryAct---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean2 = new com.ehi.component.bean.EHiRouterBean();
        routerBean2.host = "component1";
        routerBean2.path = "testQuery";
        routerBean2.desc = "";
        routerBean2.targetClass = Component1TestQueryAct.class;
        routerBeanMap.put("component1/testQuery",routerBean2);


        // ---------------------------com.ehi.component1.view.Component1TestLoginAct---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean3 = new com.ehi.component.bean.EHiRouterBean();
        routerBean3.host = "component1";
        routerBean3.path = "testLogin";
        routerBean3.desc = "";
        routerBean3.targetClass = Component1TestLoginAct.class;
        routerBeanMap.put("component1/testLogin",routerBean3);


        // ---------------------------com.ehi.component1.view.Component1TestDialogAct---------------------------

        com.ehi.component.bean.EHiRouterBean routerBean4 = new com.ehi.component.bean.EHiRouterBean();
        routerBean4.host = "component1";
        routerBean4.path = "testDialog";
        routerBean4.desc = "";
        routerBean4.targetClass = Component1TestDialogAct.class;
        routerBean4.interceptors = new ArrayList();
        routerBean4.interceptors.add(DialogShowInterceptor.class);
        routerBeanMap.put("component1/testDialog",routerBean4);

    }
}
