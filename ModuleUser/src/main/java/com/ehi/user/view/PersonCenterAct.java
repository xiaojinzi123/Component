package com.ehi.user.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.RouterAnno;
import com.ehi.user.R;

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
