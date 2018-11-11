package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.ehi.component.EHiComponentUtil;
import com.ehi.component.error.TargetActivityNotFoundException;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.support.EHiParameterSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果名称更改了,请配置到 {@link EHiComponentUtil#IMPL_OUTPUT_PKG} 和 {@link EHiComponentUtil#UIROUTER_IMPL_CLASS_NAME} 上
 * 因为这个类是生成的子路由需要继承的类,所以这个类的包的名字的更改或者类名更改都会引起源码或者配置常量的更改
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
abstract class EHiModuleRouterImpl implements IComponentHostRouter {

    /**
     * 保存映射关系的map集合
     */
    protected Map<String, Class> routerMap = new HashMap<>();

    /**
     * 记录这个Activity是否需要登录
     */
    protected Map<Class, Boolean> isNeedLoginMap = new HashMap<>();

    /**
     * 是否初始化了map,懒加载
     */
    protected boolean hasInitMap = false;

    /**
     * 上一次跳转的界面的Class
     */
    @Nullable
    private Class preTargetClass;

    /**
     * 记录上一个界面跳转的时间
     */
    private long preTargetTime;

    protected void initMap() {
        hasInitMap = true;
    }

    @Override
    public void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri) throws Exception {
        fopenUri(fragment, uri, null);
    }

    @Override
    public void openUri(@NonNull Context context, @NonNull Uri uri) throws Exception {
        openUri(context, uri, null);
    }

    @Override
    public void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle) throws Exception {
        fopenUri(fragment, uri, null, null);
    }

    @Override
    public void openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle) throws Exception {
        openUri(context, uri, null, null);
    }

    @Override
    public void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) throws Exception {
        doOpenUri(null, fragment, uri, bundle, requestCode);
    }

    @Override
    public void openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) throws Exception {
        doOpenUri(context, null, uri, bundle, requestCode);
    }

    /**
     * content 参数和 fragment 参数必须有一个有值的
     *
     * @param context
     * @param fragment
     * @param uri
     * @param bundle
     * @param requestCode
     * @return
     */
    private void doOpenUri(@Nullable Context context, @Nullable Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) throws Exception {

        if (!hasInitMap) {
            initMap();
        }

        Class targetClass = getTargetClass(uri);

        // 没有找到目标界面
        if (targetClass == null) {
            throw new TargetActivityNotFoundException(uri.toString());
        }

        // 处理拦截的代码

        EHiRouter.EHiUiRouterInterceptor currInterceptor = null;

        for (EHiRouter.EHiUiRouterInterceptor interceptor : EHiRouter.uiRouterInterceptors) {

            if (interceptor.intercept(context, fragment, uri, targetClass, bundle, requestCode, isNeedLoginMap.get(targetClass))) {
                currInterceptor = interceptor;
                break;
            }

        }

        // 如果不为空说明拦截下来了
        if (currInterceptor != null) {
            currInterceptor = null;
            return;
        }

        // 防止重复跳转同一个界面
        if (preTargetClass == targetClass && (System.currentTimeMillis() - preTargetTime) < 1000) { // 如果跳转的是同一个界面
            Log.d("EHiRouter", "you can't launch same Activity '" + preTargetClass.getName() + " in one second");
            throw new Exception("target activity can't launch twice In a second");
        }

        // 保存目前跳转过去的界面
        preTargetClass = targetClass;
        preTargetTime = System.currentTimeMillis();

        if (bundle == null) {
            bundle = new Bundle();
        }

        Intent intent = new Intent(context, targetClass);
        intent.putExtras(bundle);
        EHiParameterSupport.put(intent, uri);

        if (requestCode == null) {
            if (context != null) {
                context.startActivity(intent);
            } else if (fragment != null) {
                fragment.startActivity(intent);
            } else {
                throw new Exception("the context or fragment both are null");
            }
        } else {
            if (context != null) {
                Fragment rxFragment = findFragment(context);
                if (rxFragment == null) {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    }
                } else {
                    rxFragment.startActivityForResult(intent, requestCode);
                }

            } else if (fragment != null) {
                Fragment rxFragment = findFragment(fragment);
                if (rxFragment == null) {
                    fragment.startActivityForResult(intent, requestCode);
                } else {
                    rxFragment.startActivityForResult(intent, requestCode);
                }
            } else {
                throw new Exception("the context or fragment both are null");
            }
        }

    }

    @Override
    public boolean isMatchUri(@NonNull Uri uri) {

        if (!hasInitMap) {
            initMap();
        }

        return getTargetClass(uri) == null ? false : true;

    }

    @Override
    public Boolean isNeedLogin(@NonNull Uri uri) {

        if (!hasInitMap) {
            initMap();
        }

        Class<?> targetClass = getTargetClass(uri);

        return targetClass == null ? null : isNeedLoginMap.get(targetClass);

    }

    @Nullable
    private Class<?> getTargetClass(@NonNull Uri uri) {

        // "/component1/test" 不含host
        String targetPath = uri.getEncodedPath();

        if (targetPath == null || "".equals(targetPath)) {
            return null;
        }

        if (targetPath.charAt(0) != '/') {
            targetPath = "/" + targetPath;
        }

        targetPath = uri.getHost() + targetPath;

        Class targetClass = null;

        for (String key : routerMap.keySet()) {

            if (key == null || "".equals(key)) continue;

            if (key.equals(targetPath)) {
                targetClass = routerMap.get(key);
                break;
            }

        }
        return targetClass;

    }

    /**
     * 找到那个 Activity 中隐藏的一个 Fragment,如果找的到就会用这个 Fragment 拿来跳转
     *
     * @param context
     * @return
     */
    @Nullable
    private Fragment findFragment(@NonNull Context context) {
        Fragment result = null;
        if (context instanceof FragmentActivity) {
            FragmentManager ft = ((FragmentActivity) context).getSupportFragmentManager();
            result = ft.findFragmentByTag(EHiComponentUtil.FRAGMENT_TAG);
        }
        return result;
    }

    @Nullable
    private Fragment findFragment(@NonNull Fragment fragment) {
        Fragment result = fragment.getChildFragmentManager().findFragmentByTag(EHiComponentUtil.FRAGMENT_TAG);
        return result;
    }

}
