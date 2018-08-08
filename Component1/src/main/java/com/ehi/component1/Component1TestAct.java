package com.ehi.component1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ehi.api.anno.EHiRouter;

@EHiRouter(value = "component1/test",desc = "业务组件1的测试界面")
public class Component1TestAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_act);
    }

    public void returnData(View view) {
        setResult(RESULT_OK);
        finish();
    }

}
