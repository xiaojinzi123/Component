package com.ehi.componentdemo.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.support.EHiRouterInterceptor;
import com.ehi.componentdemo.R;

@EHiRouterAnno(
        host = ModuleConfig.App.NAME, value = ModuleConfig.App.TEST_INTERCEPTOR
)
public class TestInterceptor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_interceptor_act);
    }

    public void clearAllInterceptor(View view) {
        EHiRouter.clearRouterInterceptor();
    }

    public void registerAllBanInterceptor(View view) {
        EHiRouter.addRouterInterceptor(new EHiRouterInterceptor() {
            @Override
            public void intercept(Chain chain) throws Exception {
                String host = chain.request().uri.getHost();
                String path = chain.request().uri.getPath();
                if (ModuleConfig.App.NAME.equals(host) && ("/" + ModuleConfig.App.TEST_INTERCEPTOR).equals(path)) {
                    chain.proceed(chain.request());
                } else {
                    chain.proceed(
                            chain.request()
                                    .toBuilder()
                                    .uri(Uri.parse("EHi://" + ModuleConfig.App.NAME + "/" + ModuleConfig.App.INFO + "?data=所有界面被拦截器拦截了"))
                                    .build()
                    );
                }
            }
        });
    }

    public void registerLoginInterceptor(View view) {
        // EHiRouter.addRouterInterceptor(new LoginInterceptor());
    }

}
