package com.xiaojinzi.component.impl.fragment;

import android.app.Application;

import com.xiaojinzi.component.fragment.IComponentHostFragment;

/**
 * 空实现,每一个模块的 Fragment 生成类需要继承的
 *
 * @author xiaojinzi 30212
 */
abstract class MuduleFragmentImpl implements IComponentHostFragment {

    @Override
    public void onCreate(Application app) {
    }

    @Override
    public void onDestroy() {
    }

}
