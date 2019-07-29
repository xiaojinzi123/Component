package com.xiaojinzi.component.router;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.impl.RouterDegrade;

import java.util.List;

/**
 * 路由的降级处理
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentRouterDegrade {

    /**
     * 获取所有的降级处理, 数据需要排过序的
     *
     * @return
     * @throws Exception
     */
    @NonNull
    List<RouterDegrade> getGlobalRouterDegradeList() throws Exception;

}
