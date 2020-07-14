package com.xiaojinzi.component.impl.fragment;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.fragment.IComponentHostFragment;

import java.util.Collections;
import java.util.Set;

/**
 * 空实现,每一个模块的 Fragment 生成类需要继承的
 *
 * @author xiaojinzi
 */
abstract class ModuleFragmentImpl implements IComponentHostFragment {

    @Override
    public void onModuleCreate(Application app) {
    }

    @Override
    public void onModuleDestroy() {
    }

    @NonNull
    @Override
    public Set<String> getFragmentNameSet() {
        return Collections.EMPTY_SET;
    }

}
