package com.ehi.component.impl;

import android.support.annotation.CallSuper;

import com.ehi.component.ComponentUtil;
import com.ehi.component.bean.EHiRouterBean;
import com.ehi.component.router.IComponentHostRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果名称更改了,请配置到 {@link ComponentUtil#IMPL_OUTPUT_PKG} 和 {@link ComponentUtil#UIROUTER_IMPL_CLASS_NAME} 上
 * 因为这个类是生成的子路由需要继承的类,所以这个类的包的名字的更改或者类名更改都会引起源码或者配置常量的更改
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
abstract class EHiModuleRouterImpl implements IComponentHostRouter {

    /**
     * component/test
     * 保存映射关系的map集合
     */
    protected final Map<String, EHiRouterBean> routerBeanMap = new HashMap<>();

    /**
     * 是否初始化了map,懒加载
     */
    protected boolean hasInitMap = false;

    /**
     * 必须调用父类的方法
     */
    @CallSuper
    protected void initMap() {
        hasInitMap = true;
    }

    /**
     * 获取路由表
     *
     * @return
     */
    public Map<String, EHiRouterBean> getRouterMap() {
        if (!hasInitMap) {
            initMap();
        }
        return new HashMap<>(routerBeanMap);
    }

}
