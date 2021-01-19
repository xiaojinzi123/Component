package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentConstants;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.anno.support.NotAppUseAnno;
import com.xiaojinzi.component.bean.PageInterceptorBean;
import com.xiaojinzi.component.bean.RouterBean;
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.error.ignore.TargetActivityNotFoundException;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
import com.xiaojinzi.component.router.IComponentCenterRouter;
import com.xiaojinzi.component.router.IComponentHostRouter;
import com.xiaojinzi.component.support.ASMUtil;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.RouterInterceptorCache;
import com.xiaojinzi.component.support.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.xiaojinzi.component.ComponentConstants.SEPARATOR;

/**
 * 请注意:
 * 请勿在项目中使用此类, 此类的 Api 不供项目使用, 仅供框架内部使用.
 * 即使你在项目中能引用到此类并且调用到 Api, 也不是你想要的效果. 所以请不要使用.
 * 尤其是方法 {@link #isMatchUri(Uri)}
 * <p>
 * 中央路由,挂载着多个子路由表,这里有总路由表
 * 实际的跳转也是这里实现的,当有模块的注册和反注册发生的时候
 * 总路由表会有响应的变化
 *
 * @author xiaojinzi
 * @hide
 */
@NotAppUseAnno
@CheckClassNameAnno
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
    public synchronized boolean isMatchUri(@NonNull Uri uri) {
        return getTarget(uri) != null;
    }

    @Override
    public boolean isSameTarget(@NonNull Uri uri1, @NonNull Uri uri2) {
        return getTargetRouterKey(uri1).equals(getTargetRouterKey(uri2));
    }

    @Override
    @UiThread
    public void openUri(@NonNull final RouterRequest routerRequest) throws Exception {
        doOpenUri(routerRequest);
    }

    /**
     * @param request             路由请求对象
     * @param routerDegradeIntent 一个降级的 Intent
     */
    @UiThread
    public void routerDegrade(@NonNull RouterRequest request, @NonNull Intent routerDegradeIntent) throws Exception {
        String uriString = request.uri.toString();
        if (routerDegradeIntent == null) {
            throw new TargetActivityNotFoundException(uriString);
        }
        doStartIntent(request, routerDegradeIntent);
    }

    /**
     * content 参数和 fragment 参数必须有一个有值的
     *
     * @param request 路由请求对象
     */
    @UiThread
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
        Context rawContext = request.getRawContext();
        // 如果 Context 和 Fragment 中的 Context 都是 null
        if (rawContext == null) {
            throw new NavigationFailException("is your fragment or Activity is Destoried?\n" +
                    "see " + Component.ROUTER_UES_NOTE);
        }
        Intent intent = null;
        if (target.getTargetClass() != null) {
            intent = new Intent(rawContext, target.getTargetClass());
        } else if (target.getCustomerIntentCall() != null) {
            intent = target.getCustomerIntentCall().get(request);
        }
        if (intent == null) {
            throw new TargetActivityNotFoundException(uriString);
        }
        doStartIntent(request, intent);
    }

    /**
     * 拿到 Intent 之后真正的跳转
     *
     * @param request 请求对象
     * @param intent  Intent
     */
    @UiThread
    private void doStartIntent(@NonNull RouterRequest request,
                               Intent intent) throws Exception {
        // 前置工作

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

        if (request.context instanceof Application &&
                Component.getConfig().isTipWhenUseApplication()) {
            LogUtil.logw(
                    Router.TAG,
                    "you use 'Application' to launch Activity. this is not recommended. you should not use 'Application' as far as possible"
            );
        }

        if (request.beforStartAction != null) {
            request.beforStartAction.run();
        }

        // ------------------------------- 启动界面核心代码 ------------------------------- START

        // 如果是普通的启动界面
        if (request.isForResult) { // 如果是 startActivity
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
        } else { // 不想要框架来获取 activityResult
            // 普通跳转
            if (request.requestCode == null) {
                if (request.context != null) {
                    request.context.startActivity(intent, request.options);
                } else if (request.fragment != null) {
                    request.fragment.startActivity(intent, request.options);
                } else {
                    throw new NavigationFailException("the context or fragment both are null");
                }
            } else { // startActivityForResult
                Activity rawAct = null;
                if ((rawAct = Utils.getActivityFromContext(request.context)) != null) {
                    rawAct.startActivityForResult(intent, request.requestCode, request.options);
                } else if (request.fragment != null) {
                    request.fragment.startActivityForResult(intent, request.requestCode, request.options);
                } else {
                    throw new NavigationFailException("the context or fragment both are null");
                }
            }
        }

        // ------------------------------- 启动界面核心代码 ------------------------------- END

        if (request.afterStartAction != null) {
            request.afterStartAction.run();
        }

    }

    @NonNull
    @Override
    public synchronized List<RouterInterceptor> listPageInterceptors(@NonNull Uri uri) {
        // 获取目标对象
        final String targetUrl = getTargetUrl(uri);
        final RouterBean routerBean = routerMap.get(targetUrl);
        if (routerBean == null) {
            return Collections.emptyList();
        }

        // 如果没有拦截器直接返回 null
        if (routerBean.getPageInterceptors().isEmpty()) {
            return Collections.emptyList();
        }

        final List<RouterInterceptor> result = new ArrayList<>(routerBean.getPageInterceptors().size());

        // 排个序
        List<PageInterceptorBean> pageInterceptors = routerBean.getPageInterceptors();
        Collections.sort(pageInterceptors, new Comparator<PageInterceptorBean>() {
            @Override
            public int compare(PageInterceptorBean o1, PageInterceptorBean o2) {
                return o2.getPriority() - o1.getPriority();
            }
        });

        for (PageInterceptorBean pageInterceptorBean : pageInterceptors) {
            String interceptorName = pageInterceptorBean.getStringInterceptor();
            Class<? extends RouterInterceptor> interceptorClass = pageInterceptorBean.getClassInterceptor();
            if (interceptorName != null && !interceptorName.isEmpty()) {
                final RouterInterceptor interceptor = InterceptorCenter.getInstance().getByName(interceptorName);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's name is " + interceptorName + ",target url is " + uri.toString());
                }
                result.add(interceptor);
            } else if (interceptorClass != null) {
                final RouterInterceptor interceptor = RouterInterceptorCache.getInterceptorByClass(interceptorClass);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is " + interceptorClass + ",target url is " + uri.toString());
                }
                result.add(interceptor);
            } else {
                throw new InterceptorNotFoundException("String interceptor and class interceptor are both null");
            }
        }

        return result;
    }

    @NonNull
    @Override
    public synchronized List<RouterInterceptor> listDegradeInterceptors(@NonNull Uri uri) throws Exception {
        return Collections.emptyList();
    }

    /**
     * 获取url地址
     */
    @Nullable
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
        return routerMap.get(getTargetRouterKey(uri));
    }

    @NonNull
    private String getTargetRouterKey(@NonNull Uri uri) {
        // "/component1/test" 不含host
        String targetPath = uri.getPath();
        if (targetPath == null || targetPath.isEmpty()) {
            return null;
        }
        if (targetPath.charAt(0) != '/') {
            targetPath = SEPARATOR + targetPath;
        }
        return uri.getHost() + targetPath;
    }

    /**
     * 找到那个 Activity 中隐藏的一个 Fragment,如果找的到就会用这个 Fragment 拿来跳转
     */
    @Nullable
    private Fragment findFragment(@NonNull Context context) {
        Fragment result = null;
        Activity act = Utils.getActivityFromContext(context);
        if (act instanceof FragmentActivity) {
            FragmentManager ft = ((FragmentActivity) act).getSupportFragmentManager();
            result = ft.findFragmentByTag(ComponentConstants.ACTIVITY_RESULT_FRAGMENT_TAG);
        }
        return result;
    }

    @Nullable
    private Fragment findFragment(@NonNull Fragment fragment) {
        return fragment.getChildFragmentManager().findFragmentByTag(ComponentConstants.ACTIVITY_RESULT_FRAGMENT_TAG);
    }

    @Override
    public void register(@NonNull IComponentHostRouter router) {
        Utils.checkNullPointer(router);
        if (!hostRouterMap.containsKey(router.getHost())) {
            hostRouterMap.put(router.getHost(), router);
            routerMap.putAll(router.getRouterMap());
        }
    }

    @Override
    public void register(@NonNull String host) {
        Utils.checkStringNullPointer(host, "host");
        if (!hostRouterMap.containsKey(host)) {
            IComponentHostRouter router = findUiRouter(host);
            if (router != null) {
                register(router);
            }
        }
    }

    @Override
    public void unregister(@NonNull IComponentHostRouter hostRouter) {
        Utils.checkNullPointer(hostRouter);
        hostRouterMap.remove(hostRouter.getHost());
        Map<String, RouterBean> childRouterMap = hostRouter.getRouterMap();
        if (childRouterMap != null) {
            // key = host/path
            for (String key : childRouterMap.keySet()) {
                routerMap.remove(key);
            }
        }
    }

    @Override
    public void unregister(@NonNull String host) {
        Utils.checkStringNullPointer(host, "host");
        IComponentHostRouter hostRouter = hostRouterMap.get(host);
        if (hostRouter != null) {
            unregister(hostRouter);
        }
    }

    /**
     * 根据模块名称寻找子路由对象
     */
    @Nullable
    public IComponentHostRouter findUiRouter(String host) {
        try {
            if (Component.getConfig().isOptimizeInit()) {
                return ASMUtil.findModuleRouterAsmImpl(
                        ComponentUtil.transformHostForClass(host)
                );
            } else {
                Class<? extends IComponentHostRouter> clazz = null;
                String className = ComponentUtil.genHostRouterClassName(host);
                clazz = (Class<? extends IComponentHostRouter>) Class.forName(className);
                return clazz.newInstance();
            }
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
            if (childRouter == null) {
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