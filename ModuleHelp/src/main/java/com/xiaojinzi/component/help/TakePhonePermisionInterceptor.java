package com.xiaojinzi.component.help;

import android.Manifest;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.util.PermissionsCallback;
import com.xiaojinzi.base.util.PermissionsUtil;
import com.xiaojinzi.component.anno.InterceptorAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;

/**
 * 拍照权限申请的拦截器
 */
@InterceptorAnno(InterceptorConfig.HELP_CAMERAPERMISION)
public class TakePhonePermisionInterceptor implements RouterInterceptor {
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