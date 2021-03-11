package com.xiaojinzi.component.help;

import android.net.Uri;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.GlobalInterceptorAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 全局的一个拦截器,让网页的 schemes 跳转到网页的界面去
 * 优先级设置的高一些
 */
@GlobalInterceptorAnno(priority = 1000)
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
