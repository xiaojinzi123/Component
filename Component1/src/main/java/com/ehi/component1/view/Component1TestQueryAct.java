package com.ehi.component1.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.support.QueryParameterSupport;
import com.ehi.component1.R;

@EHiRouterAnno(
        host = ModuleConfig.Component1.NAME,
        value = ModuleConfig.Component1.TESTQUERY,
        desc = "业务组件1的主界面"
)
public class Component1TestQueryAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_query_act);

        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_pass = findViewById(R.id.tv_pass);

        String name = QueryParameterSupport.getString(getIntent(), "name", null);
        String pass = QueryParameterSupport.getString(getIntent(), "pass", null);

        tv_name.setText(name);
        tv_pass.setText(pass);

    }

}
