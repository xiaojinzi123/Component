package com.xiaojinzi.componentdemo;

import android.util.Log;

import com.xiaojinzi.component.anno.ConditionalAnno;
import com.xiaojinzi.component.anno.GlobalInterceptorAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.support.Condition;

/**
 * 全局的一个监测的拦截器
 * 使用条件注解可以让这个拦截器只在 debug 的时候生效
 * time   : 2019/02/19
 *
 * @author : xiaojinzi
 */
@GlobalInterceptorAnno
@ConditionalAnno(conditions = MonitorInterceptor.OnCondition.class)
public class MonitorInterceptor implements RouterInterceptor {

    @Override
    public void intercept(Chain chain) throws Exception {
        RouterRequest request = chain.request();
        /*if (request.uri.getQueryParameter("tel") != null) {
            request = request.toBuilder()
                    .query("tel", "15857913622")
                    .build();
        }*/
        String uriStr = request.uri.toString();
        Log.d("全局监控的拦截器", "uri = " + uriStr);
        chain.proceed(request);
    }

    public static class OnCondition implements Condition {

        @Override
        public boolean matches() {
            return BuildConfig.DEBUG;
        }

    }

}
