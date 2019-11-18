package com.xiaojinzi.component.impl;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.anno.support.CheckClassName;

/**
 * 降级的一个接口, 使用 {@link com.xiaojinzi.component.anno.RouterDegradeAnno} 注解标记一个类为降级处理
 */
@CheckClassName
public interface RouterDegrade {

    /**
     * 是否匹配这个路由
     *
     * @param request 路由请求对象
     * @return
     */
    boolean isMatch(@NonNull RouterRequest request);

    /**
     * 当路由失败的时候, 如果路由匹配 {@link RouterDegrade#isMatch(RouterRequest)}
     *
     * @param request 路由请求对象
     * @return
     */
    @NonNull
    Intent onDegrade(@NonNull RouterRequest request);

}
