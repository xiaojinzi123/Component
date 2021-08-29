package com.xiaojinzi.component.support;

/**
 * 属性注入的模式
 */
public enum AttrAutoWireMode {
    // 未指定. 这个值会采用其他层面配置的值
    Unspecified,
    // 会使用属性的值, 作为默认的值
    Default,
    // 不会使用属性的值, 获取出来的值会直接覆盖属性的值
    Override
}