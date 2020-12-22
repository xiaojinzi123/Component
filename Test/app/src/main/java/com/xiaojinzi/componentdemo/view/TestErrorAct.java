package com.xiaojinzi.componentdemo.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.componentdemo.R;

/**
 * 测试错误的情况用
 */
@RouterAnno(
        path = ModuleConfig.App.TEST_ERROR
)
public class TestErrorAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_error_act);
    }

    public void testError1(View view) {
        Router
                .with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST_DIALOG)
                .forward(new CallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull RouterResult result) {
                        System.out.println("onSuccess");
                    }

                    @Override
                    public void onError(@NonNull RouterErrorResult errorResult) {
                        System.out.println("onError");
                    }
                });
        finish();
    }

}
