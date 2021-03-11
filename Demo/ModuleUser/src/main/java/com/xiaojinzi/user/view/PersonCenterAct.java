package com.xiaojinzi.user.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.user.R;

@RouterAnno(
        path = ModuleConfig.User.PERSON_CENTER,
        interceptorNames = {InterceptorConfig.USER_LOGIN},
        // interceptorNamePriorities = {2, 1},
        // interceptors = PersonCenterAct.TestInterceptor.class,
        // interceptorPriorities = {1},
        desc = "用户个人中心界面"
)
public class PersonCenterAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_person_center_act);
    }

    public static class TestInterceptor implements RouterInterceptor {
        @Override
        public void intercept(@NonNull Chain chain) throws Exception {
            chain.proceed(
                    chain.request().toBuilder()
                            .url("router://system/callPhone?tel=17321174171")
                            .build()
            );
        }
    }

}
