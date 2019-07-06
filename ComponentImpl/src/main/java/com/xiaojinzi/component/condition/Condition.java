package com.xiaojinzi.component.condition;

/**
 * 这是一个条件的接口,实现此接口的类必须是无参数的构造函数
 * 如果是一个内部类,请确保是一个 static 的类
 */
public interface Condition {

    /**
     * true 表示条件成立,反之不成立
     *
     * @return true 表示条件成立,反之不成立
     */
    boolean matches();

}
