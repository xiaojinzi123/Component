package com.xiaojinzi.component.impl.fragment

import android.app.Application
import com.xiaojinzi.component.fragment.IComponentHostFragment
import java.util.*

/**
 * 空实现,每一个模块的 Fragment 生成类需要继承的
 *
 * @author xiaojinzi
 */
internal abstract class ModuleFragmentImpl : IComponentHostFragment {

    override fun onCreate(app: Application) {}
    override fun onDestroy() {}
    override val fragmentNameSet: Set<String> = emptySet()

}