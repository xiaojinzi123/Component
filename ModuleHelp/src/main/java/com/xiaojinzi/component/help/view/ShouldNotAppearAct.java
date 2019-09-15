package com.xiaojinzi.component.help.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.help.R;
import com.xiaojinzi.component.impl.RxRouter;

@RouterAnno(
        path = ModuleConfig.Help.SHOULD_NOT_APPEAR,
        desc = "完成自动取消路由的测试"
)
public class ShouldNotAppearAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_should_not_appear_act);
    }

}
