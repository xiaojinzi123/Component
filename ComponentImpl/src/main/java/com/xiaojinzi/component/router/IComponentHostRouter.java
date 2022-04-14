package com.xiaojinzi.component.router;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.bean.RouterBean;
import com.xiaojinzi.component.support.IHost;

import java.util.Map;

/**
 * 组件之间实现跳转的接口
 * <p>
 * time   : 2018/07/26
 * @author : xiaojinzi
 */
public interface IComponentHostRouter extends IHost {

    /**
     * 获取正则匹配的路由表
     */
    @NonNull
    Map<String, RouterBean> getRegExRouterMap();

    /**
     * 获取路由表
     */
    @NonNull
    Map<String, RouterBean> getRouterMap();

}
