package com.ehi.component.impl.interceptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentUtil;
import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.service.EHiCenterService;
import com.ehi.component.interceptor.IComponentHostInterceptor;
import com.ehi.component.interceptor.IComponentModuleInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 * @hide
 */
public class EHiCenterInterceptor implements IComponentModuleInterceptor {

    private EHiCenterInterceptor() {
    }

    private static volatile EHiCenterInterceptor instance;

    public static EHiCenterInterceptor getInstance() {
        if (instance == null) {
            synchronized (EHiCenterService.class) {
                if (instance == null) {
                    instance = new EHiCenterInterceptor();
                }
            }
        }
        return instance;
    }

    private Map<String, IComponentHostInterceptor> moduleInterceptorMap = new HashMap<>();

    private List<EHiRouterInterceptor> mInterceptorList = new ArrayList<>();

    private boolean isInterceptorListHaveChange = false;

    public List<EHiRouterInterceptor> getInterceptorList() {
        if (isInterceptorListHaveChange) {
            loadAllInterceptor();
            isInterceptorListHaveChange = false;
        }
        return mInterceptorList;
    }

    @Override
    public void register(@NonNull IComponentHostInterceptor interceptor) {
        if (interceptor == null) {
            return;
        }
        moduleInterceptorMap.put(interceptor.getHost(), interceptor);
        isInterceptorListHaveChange = true;
    }

    @Override
    public void register(@NonNull String host) {
        IComponentHostInterceptor moduleInterceptor = findModuleInterceptor(host);
        register(moduleInterceptor);
    }

    @Override
    public void unregister(@NonNull IComponentHostInterceptor interceptor) {
        unregister(interceptor.getHost());
    }

    @Override
    public void unregister(@NonNull String host) {
        IComponentHostInterceptor moduleInterceptor = moduleInterceptorMap.remove(host);
        if (moduleInterceptor == null) {
            return;
        }
        isInterceptorListHaveChange = true;
    }

    /**
     * 按顺序弄好所有拦截器
     */
    private void loadAllInterceptor() {

        mInterceptorList.clear();
        List<EHiInterceptorBean> totalList = new ArrayList<>();
        for (Map.Entry<String, IComponentHostInterceptor> entry : moduleInterceptorMap.entrySet()) {
            List<EHiInterceptorBean> list = entry.getValue().interceptorList();
            if (list == null) {
                continue;
            }
            totalList.addAll(list);
        }

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
            mInterceptorList.add(interceptorBean.interceptor);
        }

    }

    @Nullable
    public IComponentHostInterceptor findModuleInterceptor(String host) {

        String className = ComponentUtil.genHostInterceptorClassName(host);

        try {
            Class<?> clazz = Class.forName(className);

            IComponentHostInterceptor instance = (IComponentHostInterceptor) clazz.newInstance();

            return instance;

        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }

        return null;

    }

}
