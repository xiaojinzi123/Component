package com.xiaojinzi.component;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.support.LogUtil;

/**
 * 注册的声明周期回调,用于取消一些调用,这些调用在界面销毁之后
 */
class ComponentLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
            super.onFragmentDestroyed(fm, f);
            Router.cancel(f);
        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ComponentActivityStack.getInstance().pushActivity(activity);
        // 目前不支持 Activity,所以写的时候Activity 必须继承 FragmentActivity
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            // 第二个参数是指挂载到这个 Activity 的各个 FragmentManager 都会被注册上
            fragmentActivity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // ignore
    }

    @Override
    public void onActivityResumed(Activity activity) {
        // ignore
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // ignore
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // ignore
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // ignore
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ComponentActivityStack.getInstance().removeActivity(activity);
        Router.cancel(activity);
    }

}