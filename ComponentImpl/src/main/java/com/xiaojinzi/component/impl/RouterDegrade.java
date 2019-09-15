package com.xiaojinzi.component.impl;

import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * 降级的一个接口, 使用 {@link com.xiaojinzi.component.anno.RouterDegradeAnno} 注解标记一个类为降级处理
 */
public interface RouterDegrade {

    /**
     * 是否匹配这个路由
     *
     * @param request
     * @return
     */
    boolean isMatch(@NonNull RouterRequest request);

    /**
     * 当路由失败的时候, 如果路由匹配 {@link RouterDegrade#isMatch(RouterRequest)}
     *
     * @param request
     * @return
     */
    @NonNull
    Intent onDegrade(@NonNull RouterRequest request);

}
