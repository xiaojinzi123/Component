package com.ehi.component;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.ehi.component.impl.EHiRouter;

/**
 * 注册的声明周期回调,用于取消一些调用,这些调用在界面销毁之后
 */
class ComponentLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
            super.onFragmentDestroyed(fm, f);
            EHiRouter.cancel(f);
        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
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
        EHiRouter.cancel(activity);
    }

}