package com.xiaojinzi.component.application;

import android.app.Application;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.ComponentConstants;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * 这是生命周期接口
 * 如果这个类被移动了,一定要格外关注 {@link ComponentConstants#APPLICATION_INTERFACE_CLASS_NAME} 的值
 * 每一个自定义的模块 Application 都需要实现次类
 */
@MainThread
@CheckClassNameAnno
public interface IComponentApplication {

    @MainThread
    void onCreate(@NonNull Application app);

    @MainThread
    void onDestroy();

}
