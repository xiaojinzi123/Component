package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.StringDefaultAnno;
import com.xiaojinzi.component.support.ParameterInject;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component1.R;

@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_INJECT1
)
public class TestInjectParameterAct1 extends BaseAct {

    @ParameterAnno("name")
    String name;

    @StringDefaultAnno("默认string")
    @ParameterAnno("defaultName")
    String nameDefault;

    @ParameterAnno("age")
    int age;

    @ParameterAnno(value = "ageDefault", intDefault = 25)
    int ageDefault;

    @ParameterAnno("isStudent")
    boolean isStudent;

    @ParameterAnno(value = "isStudentDefault", booleanDefault = true)
    boolean isStudentDefault;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act);
        ParameterSupport.inject(this);
    }

}
