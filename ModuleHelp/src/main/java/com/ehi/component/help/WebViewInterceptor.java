package com.ehi.component.help;

import android.net.Uri;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiGlobalInterceptorAnno;
import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.EHiRouterRequest;

/**
 * 全局的一个拦截器,让网页的 schemes 跳转到网页的界面去
 */
@EHiGlobalInterceptorAnno(priority = 1000)
public class WebViewInterceptor implements EHiRouterInterceptor {

    @Override
    public void intercept(Chain chain) throws Exception {
        Uri uri = chain.request().uri;
        String scheme = uri.getScheme();
        if (ModuleConfig.HTTP_SCHEME.equalsIgnoreCase(scheme) || ModuleConfig.HTTPS_SCHEME.equalsIgnoreCase(scheme)) {
            chain.request().bundle.putString("data",uri.toString());
            EHiRouterRequest newRequest = chain.request().toBuilder()
                    .uri(new Uri.Builder()
                            .scheme(ModuleConfig.APP_SCHEME)
                            .authority(ModuleConfig.Help.NAME)
                            .path(ModuleConfig.Help.WEB)
                            .build()
                    )
                    .bundle(chain.request().bundle)
                    .build();
            chain.proceed(newRequest);
        }else {
            chain.proceed(chain.request());
        }
    }

}
