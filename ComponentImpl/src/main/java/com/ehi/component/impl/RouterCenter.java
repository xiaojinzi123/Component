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
import com.ehi.component.bean.RouterBean;
import com.ehi.component.error.ignore.InterceptorNotFoundException;
import com.ehi.component.error.ignore.NavigationFailException;
import com.ehi.component.error.ignore.TargetActivityNotFoundException;
import com.ehi.component.impl.interceptor.InterceptorCenter;
import com.ehi.component.impl.interceptor.RouterInterceptorCache;
import com.ehi.component.router.IComponentCenterRouter;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.support.ParameterSupport;
import com.ehi.component.support.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ehi.component.ComponentConstants.SEPARATOR;

/**
 * 中央路由,挂载着多个子路由表,这里有总路由表
 *
 * @author xiaojinzi 30212
 * @hide
 */
public class RouterCenter implements IComponentCenterRouter {

    /**
     * 单例对象
     */
    private static volatile RouterCenter instance;

    private RouterCenter() {
    }

    public static RouterCenter getInstance() {
        if (instance == null) {
            synchronized (RouterCenter.class) {
                if (instance == null) {
                    instance = new RouterCenter();
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
    protected final Map<String, RouterBean> routerMap = new HashMap<>();

    @Override
    @MainThread
    public void openUri(@NonNull final RouterRequest routerRequest) throws Exception {
        doOpenUri(routerRequest);
    }

    /**
     * content 参数和 fragment 参数必须有一个有值的
     *
     * @param routerRequest
     * @return
     */
    @MainThread
    private void doOpenUri(@NonNull final RouterRequest routerRequest) throws Exception {
        if (!Utils.isMainThread()) {
            throw new NavigationFailException("Router must run on main thread");
        }
        if (routerRequest.uri == null) {
            throw new NavigationFailException("target Uri is null");
        }
        // 参数检测完毕
        RouterBean target = getTarget(routerRequest.uri);
        // router://component1/test?data=xxxx
        String uriString = routerRequest.uri.toString();
        // 没有找到目标界面
        if (target == null) {
            throw new TargetActivityNotFoundException(uriString);
        }
        if (routerRequest.context == null && routerRequest.fragment == null) {
            throw new NavigationFailException("one of the Context and Fragment must not be null,do you forget call method: \nRouter.with(Context) or Router.withFragment(Fragment)");
        }
        // do startActivity
        Context context = routerRequest.getRawContext();
        // 如果 Context 和 Fragment 中的 Context 都是 null
        if (context == null) {
            throw new NavigationFailException("is your fragment or Activity is Destoried?");
        }
        // 转化 query 到 bundle,这句话不能随便放,因为这句话之前是因为拦截器可以修改 routerRequest 对象中的参数或者整个对象
        // 所以直接当所有拦截器都执行完毕的时候,在确定要跳转了,这个 query 参数可以往 bundle 里面存了
        ParameterSupport.putQueryBundleToBundle(routerRequest.bundle, routerRequest.uri);
        if (target.getCustomerJump() != null) {
            // 用于支持拿到 result 的 Fragment,如果不为空,传这个过去给自定义的地方让写代码的程序员跳转
            // 这个如果不为空,一定要替换原有的传给用户,不然就拿不到 Result 了
            Fragment rxFragment = findFragment(routerRequest);
            if (rxFragment == null) {
                target.getCustomerJump().jump(routerRequest);
            } else {
                target.getCustomerJump().jump(routerRequest
                        .toBuilder()
                        .context(null)
                        .fragment(rxFragment)
                        .build()
                );
            }
            return;
        }
        Intent intent = null;
        if (target.getTargetClass() != null) {
            intent = new Intent(context, target.getTargetClass());
        } else if (target.getCustomerIntentCall() != null) {
            intent = target.getCustomerIntentCall().get(routerRequest);
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

    /**
     * 拿到 Intent 之后真正的跳转
     *
     * @param routerRequest
     * @param intent
     */
    private void jump(@NonNull RouterRequest routerRequest, Intent intent) {
        if (routerRequest.requestCode == null) { // 如果是 startActivity
            if (routerRequest.context != null) {
                routerRequest.context.startActivity(intent);
            } else if (routerRequest.fragment != null) {
                routerRequest.fragment.startActivity(intent);
            } else {
                throw new NavigationFailException("the context or fragment both are null");
            }
            return;
        }
        // 使用 context 跳转 startActivityForResult
        if (routerRequest.context != null) {
            Fragment rxFragment = findFragment(routerRequest.context);
            Activity rawAct = null;
            if (rxFragment != null) {
                rxFragment.startActivityForResult(intent, routerRequest.requestCode);
            } else if ((rawAct = Utils.getActivityFromContext(routerRequest.context)) != null) {
                rawAct.startActivityForResult(intent, routerRequest.requestCode);
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

    @NonNull
    @Override
    public synchronized List<RouterInterceptor> interceptors(@NonNull Uri uri) {
        // 获取目标对象
        final String targetUrl = getTargetUrl(uri);
        final RouterBean routerBean = routerMap.get(targetUrl);
        if (routerBean == null) {
            return Collections.emptyList();
        }
        final List<Class<? extends RouterInterceptor>> targetInterceptors = routerBean.getInterceptors();
        final List<String> targetInterceptorNames = routerBean.getInterceptorNames();
        // 如果没有拦截器直接返回 null
        if ((targetInterceptors == null || targetInterceptors.isEmpty()) && (targetInterceptorNames == null || targetInterceptorNames.isEmpty())) {
            return Collections.emptyList();
        }
        final List<RouterInterceptor> result = new ArrayList<>();
        if (targetInterceptors != null) {
            for (Class<? extends RouterInterceptor> interceptorClass : targetInterceptors) {
                final RouterInterceptor interceptor = RouterInterceptorCache.getInterceptorByClass(interceptorClass);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is " + interceptorClass + ",target url is " + uri.toString());
                }
                result.add(interceptor);
            }
        }
        if (targetInterceptorNames != null) {
            for (String interceptorName : targetInterceptorNames) {
                final RouterInterceptor interceptor = InterceptorCenter.getInstance().getByName(interceptorName);
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
        String targetPath = uri.getPath();
        if (targetPath == null || targetPath.isEmpty()) {
            return null;
        }
        if (targetPath.charAt(0) != '/') {
            targetPath = SEPARATOR + targetPath;
        }
        targetPath = uri.getHost() + targetPath;
        return targetPath;
    }

    @Nullable
    private RouterBean getTarget(@NonNull Uri uri) {
        // "/component1/test" 不含host
        String targetPath = uri.getPath();

        if (targetPath == null || targetPath.isEmpty()) {
            return null;
        }
        if (targetPath.charAt(0) != '/') {
            targetPath = SEPARATOR + targetPath;
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
    private Fragment findFragment(Context context) {
        Fragment result = null;
        Activity act = Utils.getActivityFromContext(context);
        if (act instanceof FragmentActivity) {
            FragmentManager ft = ((FragmentActivity) act).getSupportFragmentManager();
            result = ft.findFragmentByTag(ComponentUtil.FRAGMENT_TAG);
        }
        return result;
    }

    @Nullable
    private Fragment findFragment(Fragment fragment) {
        Fragment result = null;
        if (fragment != null) {
            result = fragment.getChildFragmentManager().findFragmentByTag(ComponentUtil.FRAGMENT_TAG);
        }
        return result;
    }

    @Nullable
    private Fragment findFragment(@NonNull RouterRequest request) {
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
    public void register(IComponentHostRouter router) {
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
    public void unregister(IComponentHostRouter router) {
        if (router == null) {
            return;
        }
        hostRouterMap.remove(router.getHost());
        Map<String, RouterBean> childRouterMap = router.getRouterMap();
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
     * 根据模块名称寻找子路由对象
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
        } catch (Exception ignore) {
            // ignore
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
            if (childRouter == null || childRouter.getRouterMap() == null) {
                continue;
            }
            Map<String, RouterBean> childRouterMap = childRouter.getRouterMap();
            for (String key : childRouterMap.keySet()) {
                if (set.contains(key)) {
                    throw new IllegalStateException("the target uri is exist：" + key);
                }
                set.add(key);
            }
        }
    }
}