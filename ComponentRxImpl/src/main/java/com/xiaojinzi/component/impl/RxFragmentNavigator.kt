package com.xiaojinzi.component.impl

import androidx.fragment.app.Fragment
import com.xiaojinzi.component.impl.fragment.RxFragmentManager
import com.xiaojinzi.component.support.IBundle
import com.xiaojinzi.component.support.IBundleImpl
import io.reactivex.Single

class RxFragmentNavigator(private val fragmentFlag: String): IBundle<FragmentNavigator> by IBundleImpl() {

    fun call(): Single<Fragment> {
        return RxFragmentManager.with(fragmentFlag, bundle)
    }

}