package com.xiaojinzi.componentdemo.view.module1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.componentdemo.R;

@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_IN_OTHER_MODULE
)
public class TestInOtherModuleAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_test_in_other_module_act);
    }

}
