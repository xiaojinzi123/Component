package com.xiaojinzi.user.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.user.R;

@RouterAnno(
        host = ModuleConfig.User.NAME,
        value = ModuleConfig.User.PERSON_CENTER,
        interceptorNames = InterceptorConfig.USER_LOGIN,
        desc = "用户个人中心界面"
)
public class PersonCenterAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_person_center_act);
    }

}
