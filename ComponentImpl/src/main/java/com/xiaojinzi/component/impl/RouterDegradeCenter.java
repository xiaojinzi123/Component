package com.xiaojinzi.component.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.bean.RouterDegradeBean;
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
import com.xiaojinzi.component.router.IComponentCenterRouterDegrade;
import com.xiaojinzi.component.router.IComponentHostRouterDegrade;
import com.xiaojinzi.component.support.RouterDegradeCache;
import com.xiaojinzi.component.support.RouterInterceptorCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouterDegradeCenter implements IComponentCenterRouterDegrade {

    private RouterDegradeCenter() {
    }

    /**
     * 单例对象
     */
    private static volatile RouterDegradeCenter instance;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RouterDegradeCenter getInstance() {
        if (instance == null) {
            synchronized (RouterDegradeCenter.class) {
                if (instance == null) {
                    instance = new RouterDegradeCenter();
                }
            }
        }
        return instance;
    }

    /**
     * 子拦截器对象管理 map
     */
    private Map<String, IComponentHostRouterDegrade> moduleRouterDegradeMap = new HashMap<>();

    /**
     * 全局的降级处理, 数据一定是排序过的
     */
    private List<RouterDegradeItem> mGlobalRouterDegradeList = new ArrayList<>();

    /**
     * 是否降级处理列表发生变化
     */
    private boolean isRouterDegradeListHaveChange = false;

    @NonNull
    public List<RouterDegrade> getGlobalRouterDegradeList() {
        if (isRouterDegradeListHaveChange) {
            loadAllGlobalRouterDegrade();
            isRouterDegradeListHaveChange = false;
        }
        List<RouterDegrade> result = new ArrayList<>();
        for (RouterDegradeItem item : mGlobalRouterDegradeList) {
            result.add(item.routerDegrade);
        }
        return result;
    }

    /**
     * 按顺序弄好所有全局拦截器
     */
    private void loadAllGlobalRouterDegrade() {
        mGlobalRouterDegradeList.clear();
        List<RouterDegradeBean> totalList = new ArrayList<>();
        // 加载各个子拦截器对象中的拦截器列表
        for (Map.Entry<String, IComponentHostRouterDegrade> entry : moduleRouterDegradeMap.entrySet()) {
            List<RouterDegradeBean> list = entry.getValue().listRouterDegrade();
            totalList.addAll(list);
        }
        for (RouterDegradeBean routerDegradeBean : totalList) {
            mGlobalRouterDegradeList.add(
                    new RouterDegradeItem(
                            routerDegradeBean.getPriority(),
                            RouterDegradeCache.getRouterDegradeByClass(routerDegradeBean.getTargetClass()),
                            routerDegradeBean.getInterceptors(),
                            routerDegradeBean.getInterceptorNames()
                    ));
        }
        // 排序所有的拦截器对象,按照优先级排序
        Collections.sort(mGlobalRouterDegradeList, new Comparator<RouterDegradeItem>() {
            @Override
            public int compare(RouterDegradeItem o1, RouterDegradeItem o2) {
                if (o1.priority == o2.priority) {
                    return 0;
                } else if (o1.priority > o2.priority) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

    }

    @Override
    public void register(@Nullable IComponentHostRouterDegrade routerDegrade) {
        if (routerDegrade == null) {
            return;
        }
        isRouterDegradeListHaveChange = true;
        moduleRouterDegradeMap.put(routerDegrade.getHost(), routerDegrade);
    }

    @Override
    public void register(@NonNull String host) {
        if (moduleRouterDegradeMap.containsKey(host)) {
            return;
        }
        IComponentHostRouterDegrade moduleRouterDegrade = findModuleRouterDegrade(host);
        register(moduleRouterDegrade);
    }

    @Override
    public void unregister(@Nullable IComponentHostRouterDegrade routerDegrade) {
        if (routerDegrade == null) {
            return;
        }
        isRouterDegradeListHaveChange = true;
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostRouterDegrade moduleRouterDegrade = moduleRouterDegradeMap.remove(host);
        unregister(moduleRouterDegrade);
    }

    @NonNull
    @Override
    public List<RouterInterceptor> listDegradeInterceptors(@NonNull RouterRequest request) throws Exception {

        if (isRouterDegradeListHaveChange) {
            loadAllGlobalRouterDegrade();
            isRouterDegradeListHaveChange = false;
        }

        RouterDegradeItem findRouterDegradeItem = null;

        for (RouterDegradeItem item : mGlobalRouterDegradeList) {
            if (item.routerDegrade.isMatch(request)) {
                findRouterDegradeItem = item;
                break;
            }
        }
        if (findRouterDegradeItem != null) {
            final List<Class<? extends RouterInterceptor>> targetInterceptors = findRouterDegradeItem.interceptors;
            final List<String> targetInterceptorNames = findRouterDegradeItem.interceptorNames;
            // 如果没有拦截器直接返回 null
            if (targetInterceptors.isEmpty() && targetInterceptorNames.isEmpty()) {
                return Collections.emptyList();
            }
            final List<RouterInterceptor> result = new ArrayList<>();
            for (Class<? extends RouterInterceptor> interceptorClass : targetInterceptors) {
                final RouterInterceptor interceptor = RouterInterceptorCache.getInterceptorByClass(interceptorClass);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is " + interceptorClass + ",target url is " + request.uri.toString());
                }
                result.add(interceptor);
            }
            for (String interceptorName : targetInterceptorNames) {
                final RouterInterceptor interceptor = InterceptorCenter.getInstance().getByName(interceptorName);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's name is " + interceptorName + ",target url is " + request.uri.toString());
                }
                result.add(interceptor);
            }
            return result;
        }

        return Collections.EMPTY_LIST;

    }

    @Nullable
    public IComponentHostRouterDegrade findModuleRouterDegrade(String host) {
        String className = ComponentUtil.genHostRouterDegradeClassName(host);
        try {
            Class<?> clazz = Class.forName(className);
            return (IComponentHostRouterDegrade) clazz.newInstance();
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

    private class RouterDegradeItem {

        /**
         * 优先级
         */
        public int priority;

        @NonNull
        public RouterDegrade routerDegrade;

        /**
         * 这个目标界面要执行的拦截器
         */
        @Nullable
        public List<Class<? extends RouterInterceptor>> interceptors;

        /**
         * 这个目标界面要执行的拦截器
         */
        @Nullable
        public List<String> interceptorNames;

        public RouterDegradeItem(int priority, @NonNull RouterDegrade routerDegrade,
                                 @Nullable List<Class<? extends RouterInterceptor>> interceptors,
                                 @Nullable List<String> interceptorNames) {
            this.priority = priority;
            this.routerDegrade = routerDegrade;
            this.interceptors = interceptors;
            this.interceptorNames = interceptorNames;
        }

    }

}
