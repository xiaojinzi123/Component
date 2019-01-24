package com.ehi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ehi.base.ModuleConfig;
import com.ehi.base.bean.User;
import com.ehi.base.bean.User1;
import com.ehi.component.anno.EHiParameterAnno;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterRequest;
import com.ehi.component.support.ParameterSupport;
import com.ehi.component1.R;


public class Component1TestQueryAct extends AppCompatActivity {

    @EHiRouterAnno(
            host = ModuleConfig.Component1.NAME,
            value = ModuleConfig.Component1.TEST_QUERY
    )
    public static Intent createIntent(EHiRouterRequest request,
                                      @EHiParameterAnno(value = "user1") User user1,
                                      @EHiParameterAnno(value = "user2") User1 user2,
                                      @EHiParameterAnno(value = "test1", byteDefault = 10) byte test1,
                                      @EHiParameterAnno(value = "test2") short test2,
                                      @EHiParameterAnno(value = "test3", intDefault = 100) int test3,
                                      @EHiParameterAnno(value = "test4") long test4,
                                      @EHiParameterAnno(value = "test5", floatDefault = 100.1f) float test5,
                                      @EHiParameterAnno(value = "test6") double test6,
                                      @EHiParameterAnno(value = "test7") boolean test7,
                                      @EHiParameterAnno("name") String name,
                                      @EHiParameterAnno("pass") String pass) {
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
