package com.ehi.componentdemo.view.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.componentdemo.R;

@EHiRouterAnno(
        host = ModuleConfig.User.NAME,
        value = ModuleConfig.User.PERSON_CENTER,
        desc = "用户个人中心界面"
)
public class PersonCenterAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_person_center_act);
    }

}
