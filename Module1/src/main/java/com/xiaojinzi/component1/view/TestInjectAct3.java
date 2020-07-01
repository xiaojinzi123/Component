package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.service.inter.app.AnnoMethodService;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.ServiceAutowiredAnno;
import com.xiaojinzi.component1.R;

/**
 * 测试除了基本属性之外的数据类型
 */
@RouterAnno(
        path = ModuleConfig.Module1.TEST_INJECT3
)
public class TestInjectAct3 extends BaseAct {

    @AttrValueAutowiredAnno({"testName", "name"})
    String testName;

    @AttrValueAutowiredAnno({"testAge", "age"})
    int testAge;

    @AttrValueAutowiredAnno({"testUser", "user"})
    User testUser;

    @ServiceAutowiredAnno
    AnnoMethodService annoMethodService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act);
        Component.inject(this);
    }

}
