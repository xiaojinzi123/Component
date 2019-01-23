package com.ehi.component;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
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

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            final FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
            final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                    super.onFragmentDestroyed(fm, f);
                    EHiRouter.cancel(f);
                }
            };
            fragmentActivity.getLifecycle().addObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void onStateChanged(LifecycleOwner source) {
                    supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
                }
            });
            supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        EHiRouter.cancel(activity);
    }

}