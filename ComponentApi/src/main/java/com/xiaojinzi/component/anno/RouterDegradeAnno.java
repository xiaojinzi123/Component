package com.xiaojinzi.component.anno;

/**
 * 这个注解是标记一个类为降级的处理, 注解标记了降级是针对哪个目标进行的
 * 当对应的目标出现跳转失败的时候, 会自动直接 @RouterDegradeAnno 注解标记的
 */
public @interface RouterDegradeAnno {

    /**
     * 可以使用正则表达式匹配整个 URI
     * 比如 router://user/login
     * 你可以用 router://user/* 就可以让标记的降级起作用当用户模块任意一个界面跳转失败的情况下
     *
     * @return
     */
    //String value();

    /**
     * 拦截器的Class
     *
     * @return
     */
    Class[] interceptors() default {};

    /**
     * 拦截器的名字的列表,可以指定其他模块的拦截器
     * 这点是跨模块的,很棒的
     *
     * @return
     */
    String[] interceptorNames() default {};

    /**
     * 优先级
     *
     * @return
     */
    int priority() default 0;

}
