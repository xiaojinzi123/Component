package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.ehi.component.impl.interceptor.EHiInterceptorCenter;
import com.ehi.component.impl.interceptor.EHiRouterInterceptorCache;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.router.IComponentCenterRouter;
import com.ehi.component.support.QueryParameterSupport;
import com.ehi.component.support.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 中央路由,挂载着多个子路由表,这里有总路由表
 *
 * @hide
 */
public class EHiRouterCenter implements IComponentCenterRouter {

    /**
     * 单例对象
     */
    private static volatile EHiRouterCenter instance;

    private EHiRouterCenter() {
    }

    public static EHiRouterCenter getInstance() {
        if (instance == null) {
            synchronized (EHiRouterCenter.class) {
                if (instance == null) {
                    instance = new EHiRouterCenter();
                }
            }
        }
        return instance;
    }

    /**
     * 子路由表对象
     */
    private static Map<String, IComponentHostRouter> hostRouterMap = new HashMap<>();

    /**
     * 保存映射关系的map集合,是一个总路由表
     */
    protected final Map<String, EHiRouterBean> routerMap = new HashMap<>();

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
        if (!Utils.isMainThread()) {
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
        Context context = routerRequest.getRawContext();
        // 如果 Context 和 Fragment 中的 Context 都是 null
        if (context == null) {
            throw new NavigationFailException("is your fragment or Activity is Destoried?");
        }
        // 转化 query 到 bundle,这句话不能随便放,因为这句话之前是因为拦截器可以修改 routerRequest 对象中的参数或者整个对象
        // 所以直接当所有拦截器都执行完毕的时候,在确定要跳转了,这个 query 参数可以往 bundle 里面存了
        QueryParameterSupport.putQueryBundleToBundle(routerRequest.bundle, routerRequest.uri);
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
            Intent intent = null;
            if (target.targetClass != null) {
                intent = new Intent(context, target.targetClass);
            } else if (target.customerIntentCall != null) {
                intent = target.customerIntentCall.get(routerRequest);
            }
            if (intent == null) {
                throw new TargetActivityNotFoundException(uriString);
            }
            intent.putExtras(routerRequest.bundle);

            if (routerRequest.intentConsumer != null) {
                routerRequest.intentConsumer.accept(intent);
            }
            jump(routerRequest, intent);
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

    @Nullable
    @Override
    public synchronized List<EHiRouterInterceptor> interceptors(@NonNull Uri uri) {
        // 获取目标对象
        final String targetUrl = getTargetUrl(uri);
        final EHiRouterBean routerBean = routerMap.get(targetUrl);
        if (routerBean == null) {
            return null;
        }
        final List<Class<? extends EHiRouterInterceptor>> interceptors = routerBean.interceptors;
        final List<String> interceptorNames = routerBean.interceptorNames;
        // 如果没有拦截器直接返回 null
        if ((interceptors == null || interceptors.isEmpty()) && (interceptorNames == null || interceptorNames.isEmpty())) {
            return null;
        }
        final List<EHiRouterInterceptor> result = new ArrayList<>();
        if (interceptors != null) {
            for (Class<? extends EHiRouterInterceptor> interceptorClass : interceptors) {
                final EHiRouterInterceptor interceptor = EHiRouterInterceptorCache.getInterceptorByClass(interceptorClass);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is " + interceptorClass + ",target url is " + uri.toString());
                }
                result.add(interceptor);
            }
        }
        if (interceptorNames != null) {
            for (String interceptorName : interceptorNames) {
                final EHiRouterInterceptor interceptor = EHiInterceptorCenter.getInstance().getByName(interceptorName);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's name is " + interceptorName + ",target url is " + uri.toString());
                }
                result.add(interceptor);
            }
        }
        return result;
    }

    /**
     * 获取url地址
     *
     * @param uri
     * @return
     */
    private String getTargetUrl(@NonNull Uri uri) {
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
        return routerMap.get(targetPath);
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

    @Override
    public synchronized boolean isMatchUri(@NonNull Uri uri) {
        return getTarget(uri) != null;
    }

    @Override
    public void register(@NonNull IComponentHostRouter router) {
        if (router == null) {
            return;
        }
        hostRouterMap.put(router.getHost(), router);
        routerMap.putAll(router.getRouterMap());
    }

    @Override
    public void register(@NonNull String host) {
        IComponentHostRouter router = findUiRouter(host);
        register(router);
    }

    @Override
    public void unregister(@NonNull IComponentHostRouter router) {
        if (router == null) {
            return;
        }
        hostRouterMap.remove(router.getHost());
        Map<String, EHiRouterBean> childRouterMap = router.getRouterMap();
        if (childRouterMap != null) {
            // key = host/path
            for (String key : childRouterMap.keySet()) {
                routerMap.remove(key);
            }
        }
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostRouter router = hostRouterMap.remove(host);
        unregister(router);
    }

    /**
     * {@hide}
     *
     * @param host
     * @return
     * @hide
     */
    @Nullable
    public IComponentHostRouter findUiRouter(String host) {
        final String className = ComponentUtil.genHostRouterClassName(host);
        try {
            Class<?> clazz = Class.forName(className);
            return (IComponentHostRouter) clazz.newInstance();
        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }
        return null;
    }

    /**
     * 路由表重复的检查工作
     */
    public void check() {
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, IComponentHostRouter> entry : hostRouterMap.entrySet()) {
            IComponentHostRouter childRouter = entry.getValue();
            if (childRouter == null) {
                continue;
            }
            Map<String, EHiRouterBean> childRouterMap = childRouter.getRouterMap();
            if (childRouterMap == null) {
                continue;
            }
            for (String key : childRouterMap.keySet()) {
                if (set.contains(key)) {
                    throw new RuntimeException("the target uri is exist：" + key);
                }
                set.add(key);
            }
        }
    }

}