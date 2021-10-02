package com.xiaojinzi.component.impl

import com.xiaojinzi.component.impl.fragment.FragmentManager.get
import androidx.fragment.app.Fragment

/**
 * 一个为 [Fragment] 设计的导航器
 */
class FragmentNavigator(private val fragmentFlag: String): IBundleBuilder<FragmentNavigator> by IBundleBuilderImpl() {

    fun navigate(): Fragment? {
        return get(fragmentFlag, bundle)
    }

}