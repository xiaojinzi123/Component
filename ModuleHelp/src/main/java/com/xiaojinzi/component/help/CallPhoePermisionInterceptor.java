package com.xiaojinzi.component.help;

import android.Manifest;
import android.app.Activity;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.util.PermissionsCallback;
import com.xiaojinzi.base.util.PermissionsUtil;
import com.xiaojinzi.component.anno.ConditionalAnno;
import com.xiaojinzi.component.anno.InterceptorAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.support.Condition;

/**
 * 电话权限申请的拦截器
 */
@InterceptorAnno(InterceptorConfig.HELP_CALLPHOEPERMISION)
@ConditionalAnno(conditions = {
        CallPhoePermisionInterceptor.OnCondition.class,
        CallPhoePermisionInterceptor.OnCondition1.class
})
public class CallPhoePermisionInterceptor implements RouterInterceptor {

    @Override
    public void intercept(final Chain chain) throws Exception {
        Activity activity = chain.request().getRawOrTopActivity();
        PermissionsUtil.with(activity)
                .request(Manifest.permission.CALL_PHONE)
                .execute(new PermissionsCallback() {
                    @Override
                    public void onResult(boolean granted) {
                        if (granted) {
                            chain.proceed(chain.request());
                        } else {
                            chain.callback().onError(new Exception("fail to request call phone permision"));
                        }
                    }
                });
    }

    public static class OnCondition implements Condition {

        @Override
        public boolean matches() {
            return true;
        }

    }

    public static class OnCondition1 implements Condition {

        @Override
        public boolean matches() {
            return true;
        }

    }

}