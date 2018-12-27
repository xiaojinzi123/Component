package com.ehi.component;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.ehi.component.impl.EHiRouter;

/**
 * 注册的声明周期回调,用于取消一些调用,这些调用在界面销毁之后
 */
class ComponentLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        System.out.println("ComponentLifecycleCallback.onActivityCreated");
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