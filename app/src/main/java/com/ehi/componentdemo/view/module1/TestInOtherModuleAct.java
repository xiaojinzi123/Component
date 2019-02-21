package com.ehi.componentdemo.view.module1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.componentdemo.R;

@EHiRouterAnno(
        host = ModuleConfig.Module1.NAME,
        value = ModuleConfig.Module1.TEST_IN_OTHER_MODULE
)
public class TestInOtherModuleAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_test_in_other_module_act);
    }

}
