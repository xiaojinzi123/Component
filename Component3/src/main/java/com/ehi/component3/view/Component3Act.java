package com.ehi.component3.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component3.R;

@EHiRouterAnno(host = "component3", value = "main", needLogin = true, desc = "业务组件3的主界面")
public class Component3Act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component3_act);
    }

}
