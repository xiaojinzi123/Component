package com.xiaojinzi.component.impl;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.bean.RouterBean;
import com.xiaojinzi.component.router.IComponentHostRouter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 如果名称更改了,请配置到 {@link ComponentUtil#IMPL_OUTPUT_PKG} 和 {@link ComponentUtil#UIROUTER_IMPL_CLASS_NAME} 上
 * 因为这个类是生成的子路由需要继承的类,所以这个类的包的名字的更改或者类名更改都会引起源码或者配置常量的更改
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
abstract class ModuleRouterImpl implements IComponentHostRouter {

    /**
     * component/test
     * 保存映射关系的map集合
     */
    protected final Map<String, RouterBean> routerBeanMap = new HashMap<>();

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
    @NonNull
    public Map<String, RouterBean> getRouterMap() {
        if (!hasInitMap) {
            initMap();
        }
        if (routerBeanMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(routerBeanMap);
    }

}
