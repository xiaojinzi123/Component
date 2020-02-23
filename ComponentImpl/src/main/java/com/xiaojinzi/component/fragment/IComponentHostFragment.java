package com.xiaojinzi.component.fragment;


import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.application.IComponentApplication;
import com.xiaojinzi.component.support.IHost;

import java.util.Set;

/**
 * 每个模块的接口,需要有生命周期的通知
 */
public interface IComponentHostFragment extends IComponentApplication, IHost {

    /**
     * 主要用于检查模块和模块之间的重复
     * 这个 map 不可能有重复的.
     * 第一：因为这是一个 Map, 有重复的也会被覆盖
     * 第二：在注解驱动器中, 做了避免重复的操作
     */
    @NonNull
    @MainThread
    Set<String> getFragmentNameSet();

}
