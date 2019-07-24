package com.xiaojinzi.component.anno;

/**
 * 这个注解是标记一个类为降级的处理, 注解标记了降级是针对哪个目标进行的
 * 当对应的目标出现跳转失败的时候, 会自动直接 @DegradeAnno 注解标记的
 */
public @interface DegradeAnno {

    /**
     * 定义host
     *
     * @return
     */
    String host() default "";

    /**
     * 路径, 可以使用正则表达式匹配
     *
     * @return
     */
    String regexPath() default "";

    /**
     * 当你使用了 {@link #hostAndPath()} 那么就会自动忽略 {@link #host()} 和 {@link #path()}
     * 一次性指定 host 和 path,字符串内至少包含一个 /
     *
     * @return
     */
    String hostAndPath() default "";

}
