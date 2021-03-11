package com.xiaojinzi.user.view;

import android.os.Bundle;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.user.R;

/**
 * 为了测试,启动了会自动销毁
 */
@RouterAnno(
        path = ModuleConfig.User.PERSON_CENTER_FOR_TEST,
        interceptorNames = InterceptorConfig.USER_LOGIN_FOR_TEST,
        desc = "用户个人中心界面"
)
public class PersonCenterForTestAct extends BaseAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_person_center_act);
    }

    @Override
    protected boolean isReturn() {
        return true;
    }

    @Override
    protected void returnData() {
        finish();
    }
}
