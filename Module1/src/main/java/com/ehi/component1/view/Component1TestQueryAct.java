package com.ehi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.support.ParameterSupport;
import com.ehi.component1.R;

/**
 * 我是一个测试query传递参数的界面
 */
public class Component1TestQueryAct extends AppCompatActivity {

    @EHiRouterAnno(
            host = ModuleConfig.Component1.NAME,
            value = ModuleConfig.Component1.TEST_QUERY
    )
    public static Intent createIntent(EHiRouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), Component1TestQueryAct.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_query_act);

        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_pass = findViewById(R.id.tv_pass);

        String name = ParameterSupport.getString(getIntent(), "name", null);
        String pass = ParameterSupport.getString(getIntent(), "pass", null);

        tv_name.setText(name);
        tv_pass.setText(pass);

    }

}
