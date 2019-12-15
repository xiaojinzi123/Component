package com.xiaojinzi.component.router;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.bean.RouterDegradeBean;

import java.util.List;

/**
 * 模块的路由的降级处理
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
public interface IComponentHostRouterDegrade {

    /**
     * 获取当前的 host
     *
     * @return
     */
    @NonNull
    String getHost();

    /**
     * 获取这个模块的降级处理
     *
     * @return
     * @throws Exception
     */
    @NonNull
    List<RouterDegradeBean> listRouterDegrade();

}
