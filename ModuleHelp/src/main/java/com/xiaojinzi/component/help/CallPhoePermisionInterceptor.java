package com.xiaojinzi.component.help;

import android.Manifest;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.util.PermissionsCallback;
import com.xiaojinzi.base.util.PermissionsUtil;
import com.xiaojinzi.component.anno.InterceptorAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;

/**
 * 电话权限申请的拦截器
 */
@InterceptorAnno(InterceptorConfig.HELP_CALLPHOEPERMISION)
public class CallPhoePermisionInterceptor implements RouterInterceptor {

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
                        } else {
                            chain.callback().onError(new Exception("fail to request call phone permision"));
                        }
                    }
                });
    }

}