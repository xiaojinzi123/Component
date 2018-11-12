package com.ehi.component1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.support.EHiParameterSupport;

@EHiRouterAnno(host = "component1", value = "test", desc = "业务组件1的测试界面")
public class Component1TestAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.component1_test_act);

        TextView tv_data = findViewById(R.id.tv_data);
        tv_data.setText(EHiParameterSupport.getString(getIntent(),"data"));

    }

    public void returnData(View view) {
        Intent intent = new Intent();
        intent.putExtra("data", "this is the return data");
        setResult(RESULT_OK, intent);
        finish();
    }

}
