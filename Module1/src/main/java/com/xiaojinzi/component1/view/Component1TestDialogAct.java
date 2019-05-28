package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.interceptor.DialogShowInterceptor;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component1.R;

/**
 * 这个用来测试进这个页面都需要经过一个加载框的拦截器
 */
@RouterAnno(
        path = ModuleConfig.Module1.TEST_DIALOG,
        interceptors = DialogShowInterceptor.class
)
public class Component1TestDialogAct extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_dialog_act);
    }
}
