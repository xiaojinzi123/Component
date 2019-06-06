package com.xiaojinzi.component1.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component1.R;

/**
 * 这个是一个测试登录的界面
 */
@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_LOGIN,
        interceptorNames = {
                InterceptorConfig.USER_LOGIN,
                InterceptorConfig.HELP_CALLPHOEPERMISION
        }
)
public class Component1TestLoginAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_login_act);
        getSupportActionBar().setTitle("测试登录");
    }

}
