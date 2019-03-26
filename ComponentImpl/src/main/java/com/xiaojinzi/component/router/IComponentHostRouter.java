package com.xiaojinzi.component.router;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.bean.RouterBean;

import java.util.Map;

/**
 * 组件之间实现跳转的接口
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentHostRouter {

    /**
     * 获取host
     *
     * @return
     */
    @NonNull
    String getHost();

    /**
     * 获取路由表,用于检查
     *
     * @return
     */
    @NonNull
    Map<String, RouterBean> getRouterMap();

}
