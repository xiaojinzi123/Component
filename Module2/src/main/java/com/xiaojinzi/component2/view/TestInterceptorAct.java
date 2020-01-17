package com.xiaojinzi.component2.view;

import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;

@RouterAnno(
        path = "testInterceptor",
        interceptorNames = "testInterceptor"
)
public class TestInterceptorAct extends BaseAct {
}
