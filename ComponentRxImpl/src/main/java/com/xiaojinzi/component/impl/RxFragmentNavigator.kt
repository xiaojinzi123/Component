package com.xiaojinzi.component.impl

import androidx.fragment.app.Fragment
import com.xiaojinzi.component.impl.fragment.RxFragmentManager
import io.reactivex.Single

class RxFragmentNavigator(private val fragmentFlag: String): IBundleBuilder<RxFragmentNavigator> by IBundleBuilderImpl() {

    fun call(): Single<Fragment> {
        return RxFragmentManager.with(fragmentFlag, bundle)
    }

}