package com.ehi.component.help;

import android.Manifest;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.util.PermissionsCallback;
import com.ehi.base.util.PermissionsUtil;
import com.ehi.component.anno.EHiInterceptorAnno;
import com.ehi.component.impl.EHiRouterInterceptor;

@EHiInterceptorAnno(InterceptorConfig.HELP_CAMERA)
public class TakePhonePermisionInterceptor implements EHiRouterInterceptor {

    @Override
    public void intercept(final Chain chain) throws Exception {

        PermissionsUtil.with(chain.request().getRawContext())
                .request(Manifest.permission.CAMERA)
                .execute(new PermissionsCallback() {
                    @Override
                    public void onResult(boolean granted) {
                        if (granted) {
                            try {
                                chain.proceed(chain.request());
                            } catch (Exception e) {
                                chain.callback().onError(new Exception("fail to request camera permision"));
                            }
                        }else {
                            chain.callback().onError(new Exception("fail to request camera permision"));
                        }
                    }
                });

    }

}