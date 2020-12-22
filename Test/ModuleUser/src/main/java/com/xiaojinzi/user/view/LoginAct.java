package com.xiaojinzi.user.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.user.R;
import com.xiaojinzi.user.service.UserServiceImpl;

@RouterAnno(
        path = ModuleConfig.User.LOGIN,
        desc = "用户模块的登录界面"
)
public class LoginAct extends BaseAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_act);
        getSupportActionBar().setTitle("登录界面");
    }

    public void login(View v){
        returnData();
    }

    @Override
    protected void returnData() {
        UserServiceImpl.isLogin = true;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", new User());
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

}
