package com.ehi.base.interceptor;

import android.Manifest;

import com.ehi.base.util.PermissionsCallback;
import com.ehi.base.util.PermissionsUtil;
import com.ehi.component.impl.EHiRouterInterceptor;

/**
 * time   : 2018/12/28
 *
 * @author : xiaojinzi 30212
 */
public class CallPhoePermisionInterceptor implements EHiRouterInterceptor {

    @Override
    public void intercept(final Chain chain) throws Exception {

        PermissionsUtil.with(chain.request().getRawContext())
                .request(Manifest.permission.CALL_PHONE)
                .execute(new PermissionsCallback() {
                    @Override
                    public void onResult(boolean granted) {
                        if (granted) {
                            try {
                                chain.proceed(chain.request());
                            } catch (Exception e) {
                                chain.callback().onError(new Exception("fail to request call phone permision"));
                            }
                        }else {
                            chain.callback().onError(new Exception("fail to request call phone permision"));
                        }
                    }
                });

    }

}
