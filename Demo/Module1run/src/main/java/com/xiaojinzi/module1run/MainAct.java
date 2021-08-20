package com.xiaojinzi.module1run;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaojinzi.base.router.Module1Api;
import com.xiaojinzi.component.impl.Router;

/**
 * 单独运行 Module1 做准备的
 */
public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
    }

    public void testClick1(View view) {
        Router.withApi(Module1Api.class).toTestView(this, "jumoFromModule1Run");
    }

}
