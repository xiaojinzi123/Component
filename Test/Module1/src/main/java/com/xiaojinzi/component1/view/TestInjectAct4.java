package com.xiaojinzi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.service.inter.user.UserService;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.ServiceAutowiredAnno;
import com.xiaojinzi.component1.R;

/**
 * 测试这个界面是一个 Singletask 模式的
 * 当收到 onNewIntent 方法回调的时候才会返回
 */
@RouterAnno(
        path = ModuleConfig.Module1.TEST_INJECT4
)
public class TestInjectAct4 extends BaseAct {

    private String compareStr = "defaultName";

    TextView tv_name;

    @AttrValueAutowiredAnno("name")
    String name = compareStr;

    @ServiceAutowiredAnno(name = "user1")
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act4);
        tv_name = findViewById(R.id.tv_name);
        Component.inject(this);
        tv_name.setText(name);

        if (compareStr.equals(name)) {
            returnData();
        }else {
            compareStr = name;
        }

    }

    @Override
    protected boolean isReturnError() {
        return compareStr.equals(name);
    }

    @Override
    protected boolean isReturn() {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Component.inject(this);
        // Component.injectFromIntent(this, intent);
        tv_name.setText(name);

        returnData();
    }

}
