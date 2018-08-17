package com.ehi.component1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ehi.component.anno.EHiRouter;
import com.ehi.component.support.EHiParameterSupport;

@EHiRouter(host = "component1", value = "main", desc = "业务组件1的主界面")
public class Component1MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_main_act);

        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_pass = findViewById(R.id.tv_pass);

        String name = EHiParameterSupport.getString(getIntent(), "name", null);
        String pass = EHiParameterSupport.getString(getIntent(), "pass", null);

        tv_name.setText(name);
        tv_pass.setText(pass);

    }

}
