package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.ehi.component.ComponentUtil;
import com.ehi.component.bean.EHiRouterBean;
import com.ehi.component.error.InterceptorNotFoundException;
import com.ehi.component.error.NavigationFailException;
import com.ehi.component.error.TargetActivityNotFoundException;
import com.ehi.component.impl.interceptor.EHiCenterInterceptor;
import com.ehi.component.impl.interceptor.EHiRouterInterceptorUtil;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.support.QueryParameterSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 如果名称更改了,请配置到 {@link ComponentUtil#IMPL_OUTPUT_PKG} 和 {@link ComponentUtil#UIROUTER_IMPL_CLASS_NAME} 上
 * 因为这个类是生成的子路由需要继承的类,所以这个类的包的名字的更改或者类名更改都会引起源码或者配置常量的更改
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
abstract class EHiModuleRouterImpl implements IComponentHostRouter {

    /**
     * component/test
     * 保存映射关系的map集合
     */
    protected Map<String, EHiRouterBean> routerBeanMap = new HashMap<>();

    /**
     * 是否初始化了map,懒加载
     */
    protected boolean hasInitMap = false;

    /**
     * 必须调用父类的方法
     */
    @CallSuper
    protected void initMap() {
        hasInitMap = true;
    }

    /**
     * 获取路由表
     *
     * @return
     */
    public Map<String, EHiRouterBean> getRouterMap() {
        initMap();
        Map<String, EHiRouterBean> map = new HashMap<>(routerBeanMap);
        return map;
    }

    @Override
    @MainThread
    public void openUri(@NonNull final EHiRouterRequest routerRequest) throws Exception {
        doOpenUri(routerRequest);
    }

    /**
     * content 参数和 fragment 参数必须有一个有值的
     *
     * @param routerRequest
     * @return
     */
    @MainThread
    private void doOpenUri(@NonNull final EHiRouterRequest routerRequest) throws Exception {
        if (!hasInitMap) {
            initMap();
        }
        if (EHiRouterUtil.isMainThread() == false) {
            throw new NavigationFailException("EHiRouter must run on main thread");
        }
        if (routerRequest.uri == null) {
            throw new NavigationFailException("target Uri is null");
        }

        // 参数检测完毕

        EHiRouterBean target = getTarget(routerRequest.uri);
        // EHi://component1/test?data=xxxx
        String uriString = routerRequest.uri.toString();

        // 没有找到目标界面
        if (target == null) {
            throw new TargetActivityNotFoundException(uriString);
        }
        if (routerRequest.context == null && routerRequest.fragment == null) {
            throw new NavigationFailException("one of the Context and Fragment must not be null,do you forget call method: \nEHiRouter.with(Context) or EHiRouter.withFragment(Fragment)");
        }

        // do startActivity
        Context context = routerRequest.context;
        if (context == null) {
            context = routerRequest.fragment.getContext();
        }
        // 如果 Context 和 Fragment 中的 Context 都是 null
        if (context == null) {
            throw new NavigationFailException("your fragment attached to Activity?");
        }

        Intent intent = null;
        if (target.customerJump != null) {
            // 用于支持拿到 result 的 Fragment,如果不为空,传这个过去给自定义的地方让写代码的程序员跳转
            // 这个如果不为空,一定要替换原有的传给用户,不然就拿不到 Result 了
            Fragment rxFragment = findFragment(routerRequest);
            if (rxFragment == null) {
                target.customerJump.jump(routerRequest);
            } else {
                target.customerJump.jump(routerRequest
                        .toBuilder()
                        .context(null)
                        .fragment(rxFragment)
                        .build()
                );
            }
        } else {
            if (target.targetClass != null) {
                intent = new Intent(context, target.targetClass);
            } else if (target.customerIntentCall != null) {
                intent = target.customerIntentCall.get(routerRequest);
            }
            if (intent == null) {
                throw new TargetActivityNotFoundException(uriString);
            }
            intent.putExtras(routerRequest.bundle);
            QueryParameterSupport.put(intent, routerRequest.uri);
            if (routerRequest.intentConsumer != null) {
                routerRequest.intentConsumer.accept(intent);
            }
            jump(routerRequest, intent);
        }
        if (routerRequest.afterJumpAction != null) {
            routerRequest.afterJumpAction.run();
        }
    }

    /**
     * 拿到 Intent 之后真正的跳转
     *
     * @param routerRequest
     * @param intent
     */
    private void jump(@NonNull EHiRouterRequest routerRequest, Intent intent) {
        if (routerRequest.requestCode == null) { // 如果是 startActivity
            if (routerRequest.context != null) {
                routerRequest.context.startActivity(intent);
            } else if (routerRequest.fragment != null) {
                routerRequest.fragment.startActivity(intent);
            } else {
                throw new NavigationFailException("the context or fragment both are null");
            }
        } else {
            // 使用 context 跳转 startActivityForResult
            if (routerRequest.context != null) {
                Fragment rxFragment = findFragment(routerRequest.context);
                if (rxFragment != null) {
                    rxFragment.startActivityForResult(intent, routerRequest.requestCode);
                } else if (routerRequest.context instanceof Activity) {
                    ((Activity) routerRequest.context).startActivityForResult(intent, routerRequest.requestCode);
                } else {
                    throw new NavigationFailException("Context is not a Activity,so can't use 'startActivityForResult' method");
                }
            } else if (routerRequest.fragment != null) { // 使用 Fragment 跳转
                Fragment rxFragment = findFragment(routerRequest.fragment);
                if (rxFragment != null) {
                    rxFragment.startActivityForResult(intent, routerRequest.requestCode);
                } else {
                    routerRequest.fragment.startActivityForResult(intent, routerRequest.requestCode);
                }
            } else {
                throw new NavigationFailException("the context or fragment both are null");
            }
        }
    }

    @Override
    public synchronized boolean isMatchUri(@NonNull Uri uri) {
        if (!hasInitMap) {
            initMap();
        }
        return getTarget(uri) == null ? false : true;
    }

    @Nullable
    @Override
    public synchronized List<EHiRouterInterceptor> interceptors(@NonNull Uri uri) {
        if (!hasInitMap) {
            initMap();
        }
        // 获取目标对象
        String targetPath = getTargetPath(uri);
        EHiRouterBean routerBean = routerBeanMap.get(targetPath);
        if (routerBean == null) {
            return null;
        }
        List<Class<? extends EHiRouterInterceptor>> interceptors = routerBean.interceptors;
        List<String> interceptorNames = routerBean.interceptorNames;

        // 如果没有拦截器直接返回 null
        if ((interceptors == null || interceptors.size() == 0) && (interceptorNames == null || interceptorNames.size() == 0)) {
            return null;
        }

        List<EHiRouterInterceptor> result = new ArrayList<>();
        if (interceptors != null) {
            for (Class<? extends EHiRouterInterceptor> interceptorClass : interceptors) {
                EHiRouterInterceptor interceptor = EHiRouterInterceptorUtil.get(interceptorClass);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is" + interceptorClass + ",target url is" + uri.toString());
                }
                result.add(interceptor);
            }
        }
        if (interceptorNames != null) {
            for (String interceptorName : interceptorNames) {
                EHiRouterInterceptor interceptor = EHiCenterInterceptor.getInstance().getByName(interceptorName);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's name is" + interceptorName + ",target url is" + uri.toString());
                }
                result.add(interceptor);
            }
        }
        return result;
    }

    private String getTargetPath(@NonNull Uri uri) {
        // "/component1/test" 不含host
        String targetPath = uri.getEncodedPath();
        if (targetPath == null || "".equals(targetPath)) {
            return null;
        }
        if (targetPath.charAt(0) != '/') {
            targetPath = "/" + targetPath;
        }
        targetPath = uri.getHost() + targetPath;
        return targetPath;
    }

    @Nullable
    private EHiRouterBean getTarget(@NonNull Uri uri) {
        // "/component1/test" 不含host
        String targetPath = uri.getEncodedPath();

        if (targetPath == null || "".equals(targetPath)) {
            return null;
        }
        if (targetPath.charAt(0) != '/') {
            targetPath = "/" + targetPath;
        }
        targetPath = uri.getHost() + targetPath;
        return routerBeanMap.get(targetPath);
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
        if (context != null && context instanceof FragmentActivity) {
            FragmentManager ft = ((FragmentActivity) context).getSupportFragmentManager();
            result = ft.findFragmentByTag(ComponentUtil.FRAGMENT_TAG);
        }
        return result;
    }

    @Nullable
    private Fragment findFragment(@NonNull Fragment fragment) {
        Fragment result = null;
        if (fragment != null) {
            result = fragment.getChildFragmentManager().findFragmentByTag(ComponentUtil.FRAGMENT_TAG);
        }
        return result;
    }

    @Nullable
    private Fragment findFragment(@NonNull EHiRouterRequest request) {
        Fragment fragment = findFragment(request.context);
        if (fragment == null) {
            fragment = findFragment(request.fragment);
        }
        return fragment;
    }

    private String getUrlWithOutQuerys(String url) {
        int index = url.indexOf("?");
        if (index > -1) {
            return url.substring(0, index);
        } else {
            return url;
        }
    }

}
