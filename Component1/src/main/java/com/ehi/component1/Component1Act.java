package com.ehi.component1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ehi.api.anno.EHiRouter;

@EHiRouter(value = "component3",desc = "业务组件1的主界面")
public class Component1Act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_act);
    }

}
