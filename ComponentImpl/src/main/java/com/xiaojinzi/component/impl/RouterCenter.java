package com.xiaojinzi.component.impl;

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

import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.bean.RouterBean;
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.error.ignore.TargetActivityNotFoundException;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
import com.xiaojinzi.component.support.RouterInterceptorCache;
import com.xiaojinzi.component.router.IComponentCenterRouter;
import com.xiaojinzi.component.router.IComponentHostRouter;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component.support.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.xiaojinzi.component.ComponentConstants.SEPARATOR;

/**
 * 中央路由,挂载着多个子路由表,这里有总路由表
 * 实际的跳转也是这里实现的,当有模块的注册和反注册发生的时候
 * 总路由表会有响应的变化
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
     * @param request
     * @return
     */
    @MainThread
    private void doOpenUri(@NonNull final RouterRequest request) throws Exception {
        if (!Utils.isMainThread()) {
            throw new NavigationFailException("Router must run on main thread");
        }
        if (request.uri == null) {
            throw new NavigationFailException("target Uri is null");
        }
        // 参数检测完毕
        RouterBean target = getTarget(request.uri);
        // router://component1/test?data=xxxx
        String uriString = request.uri.toString();
        // 没有找到目标界面
        if (target == null) {
            throw new TargetActivityNotFoundException(uriString);
        }
        if (request.context == null && request.fragment == null) {
            throw new NavigationFailException("one of the Context and Fragment must not be null,do you forget call method: \nRouter.with(Context) or Router.with(Fragment)");
        }
        // do startActivity
        Context context = request.getRawContext();
        // 如果 Context 和 Fragment 中的 Context 都是 null
        if (context == null) {
            throw new NavigationFailException("is your fragment or Activity is Destoried?");
        }
        // 转化 query 到 bundle,这句话不能随便放,因为这句话之前是因为拦截器可以修改 routerRequest 对象中的参数或者整个对象
        // 所以直接当所有拦截器都执行完毕的时候,在确定要跳转了,这个 query 参数可以往 bundle 里面存了
        ParameterSupport.putQueryBundleToBundle(request.bundle, request.uri);
        Intent intent = null;
        if (target.getTargetClass() != null) {
            intent = new Intent(context, target.getTargetClass());
        } else if (target.getCustomerIntentCall() != null) {
            intent = target.getCustomerIntentCall().get(request);
        }
        if (intent == null) {
            throw new TargetActivityNotFoundException(uriString);
        }
        // 所有的参数存到 Intent 中
        intent.putExtras(request.bundle);
        // 把用户的 flags 和 categories 都设置进来
        for (String intentCategory : request.intentCategories) {
            intent.addCategory(intentCategory);
        }
        for (Integer intentFlag : request.intentFlags) {
            intent.addFlags(intentFlag);
        }
        if (request.intentConsumer != null) {
            request.intentConsumer.accept(intent);
        }
        jump(request, intent);
    }

    /**
     * 拿到 Intent 之后真正的跳转
     *
     * @param request
     * @param intent
     */
    private void jump(@NonNull RouterRequest request, Intent intent) {
        // 如果是普通的启动界面
        if (request.requestCode == null) { // 如果是 startActivity
            if (request.context != null) {
                request.context.startActivity(intent, request.options);
            } else if (request.fragment != null) {
                request.fragment.startActivity(intent, request.options);
            } else {
                throw new NavigationFailException("the context or fragment both are null");
            }
            return;
        }
        // 使用 context 跳转 startActivityForResult
        if (request.context != null) {
            Fragment rxFragment = findFragment(request.context);
            Activity rawAct = null;
            if (rxFragment != null) {
                rxFragment.startActivityForResult(intent, request.requestCode, request.options);
            } else if ((rawAct = Utils.getActivityFromContext(request.context)) != null) {
                rawAct.startActivityForResult(intent, request.requestCode, request.options);
            } else {
                throw new NavigationFailException("Context is not a Activity,so can't use 'startActivityForResult' method");
            }
        } else if (request.fragment != null) { // 使用 Fragment 跳转
            Fragment rxFragment = findFragment(request.fragment);
            if (rxFragment != null) {
                rxFragment.startActivityForResult(intent, request.requestCode, request.options);
            } else {
                request.fragment.startActivityForResult(intent, request.requestCode, request.options);
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