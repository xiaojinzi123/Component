package com.xiaojinzi.component.anno;

/**
 * 这个注解是标记一个类为降级的处理
 * 此注解也是返回一个 Intent, 不支持页面拦截器.
 * 返回的 intent 在匹配的情况下立马用作跳转的 intent 使用
 */
public @interface RouterDegradeAnno {

    /**
     * 优先级
     *
     * @return
     */
    int priority() default 0;

}
