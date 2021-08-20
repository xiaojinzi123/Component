package com.xiaojinzi.component.router;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.bean.RouterDegradeBean;
import com.xiaojinzi.component.support.IHost;

import java.util.List;

/**
 * 模块的路由的降级处理
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
public interface IComponentHostRouterDegrade extends IHost {

    /**
     * 获取这个模块的降级处理
     */
    @NonNull
    List<RouterDegradeBean> listRouterDegrade();

}
