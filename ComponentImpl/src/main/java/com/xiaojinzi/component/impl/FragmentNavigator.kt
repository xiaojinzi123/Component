package com.xiaojinzi.component.impl

import com.xiaojinzi.component.impl.fragment.FragmentManager.get
import android.os.Bundle
import com.xiaojinzi.component.impl.FragmentNavigator
import android.os.Parcelable
import android.util.SparseArray
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.support.IBundle
import com.xiaojinzi.component.support.IBundleImpl
import com.xiaojinzi.component.support.Utils
import java.io.Serializable
import java.util.ArrayList

/**
 * 一个为 [Fragment] 设计的导航器
 */
class FragmentNavigator(private val fragmentFlag: String): IBundle<FragmentNavigator> by IBundleImpl() {

    fun navigate(): Fragment? {
        return get(fragmentFlag, bundle)
    }

}