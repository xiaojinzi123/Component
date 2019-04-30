package com.xiaojinzi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component1.R;

@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_INJECT1
)
public class TestInjectParameterAct1 extends BaseAct {

    @ParameterAnno("name")
    String name;

    @ParameterAnno("defaultName")
    String nameDefault = "hello name";

    @ParameterAnno("age")
    Integer age;

    @ParameterAnno(value = "ageDefault")
    int ageDefault;

    @ParameterAnno("isStudent")
    boolean isStudent;

    @ParameterAnno(value = "isStudentDefault")
    boolean isStudentDefault;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act);
        ParameterSupport.inject(this);
    }

    @Override
    protected void returnData() {
        boolean isSuccess = true;

        String realName = getIntent().getStringExtra("name");
        if (realName == null) {

        }
        if (realName == null && name != null) {
            isSuccess = false;
        }
        // 如果 realName 有值得话就必须相等,否则就是错误的
        if (realName != null && !realName.equals(name)) {
            isSuccess = false;
        }

        String realDefaultName = getIntent().getStringExtra("defaultName");
        // 如果realName为空那么name也必须为空,name不为空就是错误的行为
        if (realDefaultName == null && !"默认string".equals(nameDefault)) {
            isSuccess = false;
        }
        // 如果 realName 有值得话就必须相等,否则就是错误的
        if (realDefaultName != null && !realDefaultName.equals(nameDefault)) {
            isSuccess = false;
        }

        int realAge = getIntent().getIntExtra("age", 0);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
