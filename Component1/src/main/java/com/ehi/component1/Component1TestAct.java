package com.ehi.component1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ehi.component.anno.EHiRouterAnno;

@EHiRouterAnno(host = "component1", value = "test", desc = "业务组件1的测试界面")
public class Component1TestAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_act);
    }

    public void returnData(View view) {
        Intent intent = new Intent();
        intent.putExtra("data", "this is the return data");
        setResult(RESULT_OK, intent);
        finish();
    }

}
