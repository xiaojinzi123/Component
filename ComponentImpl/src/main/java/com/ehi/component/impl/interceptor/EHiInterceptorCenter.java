package com.ehi.component.impl.interceptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentUtil;
import com.ehi.component.error.InterceptorNameExistException;
import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.Router;
import com.ehi.component.interceptor.IComponentCenterInterceptor;
import com.ehi.component.interceptor.IComponentHostInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 中央拦截器
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 * @hide
 */
public class EHiInterceptorCenter implements IComponentCenterInterceptor {

    private EHiInterceptorCenter() {
    }

    /**
     * 单例对象
     */
    private static volatile EHiInterceptorCenter instance;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static EHiInterceptorCenter getInstance() {
        if (instance == null) {
            synchronized (EHiInterceptorCenter.class) {
                if (instance == null) {
                    instance = new EHiInterceptorCenter();
                }
            }
        }
        return instance;
    }

    /**
     * 子拦截器对象管理 map
     */
    private Map<String, IComponentHostInterceptor> moduleInterceptorMap = new HashMap<>();

    /**
     * 公共的拦截器列表
     */
    private List<EHiRouterInterceptor> mGlobalInterceptorList = new ArrayList<>();

    /**
     * 每个业务组件的拦截器 name --> Class 映射关系的总的集合
     * 这种拦截器不是全局拦截器,是随时随地使用的拦截器,见 {@link Router.Builder#interceptorNames(String...)}
     */
    private Map<String, Class<? extends EHiRouterInterceptor>> mInterceptorMap = new HashMap<>();

    /**
     * 是否公共的拦截器列表发生变化
     */
    private boolean isInterceptorListHaveChange = false;

    /**
     * 获取全局拦截器
     *
     * @return
     */
    public List<EHiRouterInterceptor> getGlobalInterceptorList() {
        if (isInterceptorListHaveChange) {
            loadAllGlobalInterceptor();
            isInterceptorListHaveChange = false;
        }
        return mGlobalInterceptorList;
    }

    @Override
    public void register(@Nullable IComponentHostInterceptor interceptor) {
        if (interceptor == null) {
            return;
        }
        isInterceptorListHaveChange = true;
        moduleInterceptorMap.put(interceptor.getHost(), interceptor);
        // 子拦截器列表
        Map<String, Class<? extends EHiRouterInterceptor>> childInterceptorMap = interceptor.getInterceptorMap();
        if (childInterceptorMap != null) {
            mInterceptorMap.putAll(childInterceptorMap);
        }
    }

    @Override
    public void register(@NonNull String host) {
        if (moduleInterceptorMap.containsKey(host)) {
            return;
        }
        IComponentHostInterceptor moduleInterceptor = findModuleInterceptor(host);
        register(moduleInterceptor);
    }

    @Override
    public void unregister(@Nullable IComponentHostInterceptor interceptor) {
        if (interceptor == null) {
            return;
        }
        isInterceptorListHaveChange = true;
        // 子拦截器列表
        Map<String, Class<? extends EHiRouterInterceptor>> childInterceptorMap = interceptor.getInterceptorMap();
        if (childInterceptorMap != null) {
            for (Map.Entry<String, Class<? extends EHiRouterInterceptor>> entry : childInterceptorMap.entrySet()) {
                mInterceptorMap.remove(entry.getKey());
                EHiRouterInterceptorCache.removeCache(entry.getValue());
            }
        }
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostInterceptor moduleInterceptor = moduleInterceptorMap.remove(host);
        unregister(moduleInterceptor);
    }

    /**
     * 按顺序弄好所有全局拦截器
     */
    private void loadAllGlobalInterceptor() {
        mGlobalInterceptorList.clear();
        List<EHiInterceptorBean> totalList = new ArrayList<>();
        // 加载各个子拦截器对象中的拦截器列表
        for (Map.Entry<String, IComponentHostInterceptor> entry : moduleInterceptorMap.entrySet()) {
            List<EHiInterceptorBean> list = entry.getValue().globalInterceptorList();
            totalList.addAll(list);
        }
        // 排序所有的拦截器对象,按照优先级排序
        Collections.sort(totalList, new Comparator<EHiInterceptorBean>() {
            @Override
            public int compare(EHiInterceptorBean o1, EHiInterceptorBean o2) {
                if (o1.priority == o2.priority) {
                    return 0;
                } else if (o1.priority > o2.priority) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        for (EHiInterceptorBean interceptorBean : totalList) {
            mGlobalInterceptorList.add(interceptorBean.interceptor);
        }
    }

    @Nullable
    public IComponentHostInterceptor findModuleInterceptor(String host) {
        String className = ComponentUtil.genHostInterceptorClassName(host);
        try {
            Class<?> clazz = Class.forName(className);
            return (IComponentHostInterceptor) clazz.newInstance();
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

    @Nullable
    @Override
    public EHiRouterInterceptor getByName(@Nullable String interceptorName) {
        if (interceptorName == null) {
            return null;
        }
        // 先到缓存中找
        EHiRouterInterceptor result = null;
        // 拿到拦截器的 Class 对象
        Class<? extends EHiRouterInterceptor> interceptorClass = mInterceptorMap.get(interceptorName);
        if (interceptorClass == null) {
            result = null;
        } else {
            result = EHiRouterInterceptorCache.getInterceptorByClass(interceptorClass);
        }
        return result;
    }

    /**
     * 做拦截器的名称是否重复的工作
     */
    public void check() {
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, IComponentHostInterceptor> entry : moduleInterceptorMap.entrySet()) {
            IComponentHostInterceptor childInterceptor = entry.getValue();
            if (childInterceptor == null || childInterceptor.getInterceptorNames() == null) {
                continue;
            }
            Set<String> childInterceptorNames = childInterceptor.getInterceptorNames();
            for (String interceptorName : childInterceptorNames) {
                if (set.contains(interceptorName)) {
                    throw new InterceptorNameExistException("the interceptor's name is exist：" + interceptorName);
                }
                set.add(interceptorName);
            }
        }
    }

}
