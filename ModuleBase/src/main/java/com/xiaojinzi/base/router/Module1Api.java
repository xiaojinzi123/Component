package com.xiaojinzi.base.router;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

/**
 * App 模块的路由跳转接口
 */
@RouterApiAnno()
@HostAnno(ModuleConfig.Module1.NAME)
public interface Module1Api {

    @PathAnno(ModuleConfig.Module1.TEST)
    void test(@ParameterAnno("data") String data);

}
