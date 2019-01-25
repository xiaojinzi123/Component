package com.ehi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.support.ParameterSupport;
import com.ehi.component1.R;

/**
 * 这个界面用于显示传递过来的 Data 数据,并且返回一个Result
 */
@EHiRouterAnno(
        host = ModuleConfig.Component1.NAME,
        value = ModuleConfig.Component1.TEST,
        desc = "业务组件1的测试界面"
)
public class Component1TestAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_act);

        TextView tv_data = findViewById(R.id.tv_data);
        tv_data.setText(ParameterSupport.getString(getIntent(), "data"));
    }

    public void returnData(View view) {
        Intent intent = new Intent();
        intent.putExtra("data", "this is the return data，requestData is + " + ParameterSupport.getString(getIntent(), "data"));
        setResult(RESULT_OK, intent);
        finish();
    }

}
