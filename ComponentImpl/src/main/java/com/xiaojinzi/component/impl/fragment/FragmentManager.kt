package com.xiaojinzi.component.impl.fragment

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import android.os.Bundle
import androidx.annotation.AnyThread
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.support.Function1
import com.xiaojinzi.component.support.Utils
import java.util.*

/**
 * Fragment 的容器
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
object FragmentManager {

    /**
     * Service 的集合, 线程安全
     */
    private val map = Collections.synchronizedMap(HashMap<String, Function1<Bundle?, out Fragment>>())

    /**
     * 你可以注册一个服务,服务的初始化可以是 懒加载的
     *
     * @param flag 用 [com.xiaojinzi.component.anno.FragmentAnno] 标记 [Fragment] 的字符串
     * @param function function
     */
    @AnyThread
    @JvmStatic
    fun register(flag: String,
                 function: Function1<Bundle?, out Fragment>) {
        Utils.checkNullPointer(flag, "flag")
        Utils.checkNullPointer(function, "function")
        map[flag] = function
    }

    @AnyThread
    @JvmStatic
    fun unregister(flag: String) {
        map.remove(flag)
    }

    @AnyThread
    @JvmStatic
    fun get(flag: String): Fragment? {
        return get(flag = flag, bundle = null)
    }

    @AnyThread
    @JvmStatic
    fun get(flag: String, bundle: Bundle?): Fragment? {
        Utils.checkStringNullPointer(flag, "fragment flag")
        val function = map[flag]
        return function?.apply(bundle)
    }

}