package com.ehi.component.impl.interceptor;

import android.support.annotation.Nullable;

import com.ehi.component.interceptor.IComponentHostInterceptor;

import java.util.List;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
abstract class EHiMuduleInterceptorImpl implements IComponentHostInterceptor {
    @Nullable
    @Override
    public List<EHiInterceptorBean> interceptorList() {
        return null;
    }

}
