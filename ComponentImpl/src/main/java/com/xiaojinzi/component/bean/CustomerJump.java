package com.xiaojinzi.component.bean;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 自定义跳转的时候,会调用的接口,{@link RouterAnno} 注解标记在静态方法上的时候
 * 当返回值是 void 的时候,就会产生一个此接口的实现类来实现目标界面的跳转
 * time   : 2019/01/07
 *
 * @author : xiaojinzi
 */
public interface CustomerJump {

    /**
     * 跳转
     *
     * @param request 路由请求对象
     */
    void jump(@NonNull RouterRequest request) throws Exception;

}
