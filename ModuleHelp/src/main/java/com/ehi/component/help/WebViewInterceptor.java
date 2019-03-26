package com.ehi.component.help;

import android.net.Uri;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiGlobalInterceptorAnno;
import com.ehi.component.impl.RouterInterceptor;
import com.ehi.component.impl.RouterRequest;

/**
 * 全局的一个拦截器,让网页的 schemes 跳转到网页的界面去
 * 优先级设置的高一些
 */
@EHiGlobalInterceptorAnno(priority = 1000)
public class WebViewInterceptor implements RouterInterceptor {

    @Override
    public void intercept(Chain chain) throws Exception {
        Uri uri = chain.request().uri;
        String scheme = uri.getScheme();
        if (ModuleConfig.HTTP_SCHEME.equalsIgnoreCase(scheme) || ModuleConfig.HTTPS_SCHEME.equalsIgnoreCase(scheme)) {
            // 改变 request 对象路由到 网页的 Activity 去
            RouterRequest newRequest = chain.request().toBuilder()
                    .scheme(ModuleConfig.APP_SCHEME)
                    .host(ModuleConfig.Help.NAME)
                    .path(ModuleConfig.Help.WEB)
                    .putString("data",uri.toString())
                    .build();
            // 执行
            chain.proceed(newRequest);
        }else {
            chain.proceed(chain.request());
        }
    }

}
