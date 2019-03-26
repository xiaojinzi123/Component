package com.ehi.user.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ehi.base.ModuleConfig;
import com.ehi.base.bean.User;
import com.ehi.component.anno.RouterAnno;
import com.ehi.user.R;
import com.ehi.user.service.UserServiceImpl;

@RouterAnno(
        host = ModuleConfig.User.NAME,
        value = ModuleConfig.User.LOGIN,
        desc = "用户模块的登录界面"
)
public class LoginAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_act);
        getSupportActionBar().setTitle("登录界面");
    }

    public void login(View v){
        UserServiceImpl.isLogin = true;
        Intent intent = new Intent();
        intent.putExtra("data", new User());
        setResult(RESULT_OK,intent);
        finish();
    }

}
