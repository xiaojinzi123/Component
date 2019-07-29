package com.xiaojinzi.component.anno;

/**
 * 这个注解是标记一个类为降级的处理, 注解标记了降级是针对哪个目标进行的
 * 当对应的目标出现跳转失败的时候, 会自动直接 @RouterDegradeAnno 注解标记的
 * 因为全局的希望他优先级比较低, 所以大家使用的时候尽量用好这个优先级
 */
public @interface RouterDegradeAnno {

    /**
     * 优先级
     *
     * @return
     */
    int priority() default 0;

}
