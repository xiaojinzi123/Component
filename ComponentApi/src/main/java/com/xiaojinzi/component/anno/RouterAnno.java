package com.xiaojinzi.component.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个标记一个 Activity 或者一个 静态方法 是一个路由的注解
 * 1.如果标记在 Activity 上是标准的用法
 * 2.如果标记在静态方法上：
 * 2.1) 你可以让方法返回 Intent, 这样子你可以自定义 Intent, 然后交由系统去跳转
 * 2.1.1) 正因为用户可以完全的控制跳转,所以理论上内部可以做任何一件事情,你可以为第三方或者系统的界面写一个静态方法.
 * 让第三方或者系统的界面也成为了可路由的一个目标.并且可以享受到所有跳转带来的好处
 * <p>
 * 参数的获取问题. RouterRequest 中有 Bundle 对象, 你可以拿到你想要的, 但是因为框架是支持 URI 的跳转方式.所以
 * 避免不了可能用户传递了 query 的参数, 所以强烈建议获取参数的时候使用 ParameterSupport 类去获取.比如要获取 String 参数
 * ParameterSupport.getString("name") 方法会能获取到用户通过 URI 传递的 query 的值,
 * 而你自己通过 RouterRequest.bundle.getStringExtra("name") 则获取不到, 所以获取参数尽量使用
 * ParameterSupport 类, 这样可以支持获取到 query 中的值, 当然你也可以使用自动注入功能
 * Component.inject(this) 即可, 不过这样要配合注解使用:
 * {@link FiledAutowiredAnno} 和 {@link ServiceAutowiredAnno}
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface RouterAnno {

    /**
     * 定义host
     *
     * @return
     */
    String host() default "";

    /**
     * 路径
     *
     * @return
     */
    String path() default "";

    /**
     * 当你使用了 {@link #hostAndPath()} 那么就会自动忽略 {@link #host()} 和 {@link #path()}
     * 一次性指定 host 和 path,字符串内至少包含一个 /
     *
     * @return 返回 {host}/{path} 的组合, 你也可以分开去定义
     */
    String hostAndPath() default "";

    /**
     * 拦截器的Class
     *
     * @return 返回该界面要执行的拦截器
     */
    Class[] interceptors() default {};

    /**
     * 拦截器的名字的列表,可以指定其他模块的拦截器
     * 这点是跨模块的,很棒的
     *
     * @return 返回该界面要执行的拦截器的名称
     */
    String[] interceptorNames() default {};

    /**
     * 描述信息
     *
     * @return 描述
     */
    String desc() default "";

}
