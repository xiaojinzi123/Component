package com.xiaojinzi.component

import android.app.Activity
import com.xiaojinzi.component.ComponentActivityStack.pushActivity
import com.xiaojinzi.component.ComponentActivityStack.removeActivity
import android.app.Application.ActivityLifecycleCallbacks
import com.xiaojinzi.component.impl.Router
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.ComponentActivityStack
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 注册的声明周期回调,用于取消一些调用,这些调用在界面销毁之后
 */
internal class ComponentLifecycleCallback : ActivityLifecycleCallbacks {

    private val fragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks =
        object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                Router.cancel(f)
            }
        }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        pushActivity(activity)
        // 目前不支持 Activity,所以写的时候Activity 必须继承 FragmentActivity
        if (activity is FragmentActivity) {
            // 第二个参数是指挂载到这个 Activity 的各个 FragmentManager 都会被注册上
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                fragmentLifecycleCallbacks,
                true
            )
        }
    }

    override fun onActivityStarted(activity: Activity) {
        // ignore
    }

    override fun onActivityResumed(activity: Activity) {
        // ignore
    }

    override fun onActivityPaused(activity: Activity) {
        // ignore
    }

    override fun onActivityStopped(activity: Activity) {
        // ignore
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // ignore
    }

    override fun onActivityDestroyed(activity: Activity) {
        removeActivity(activity)
        Router.cancel(activity)
    }

}