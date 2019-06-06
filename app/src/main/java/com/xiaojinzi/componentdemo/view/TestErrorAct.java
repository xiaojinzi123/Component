package com.xiaojinzi.componentdemo.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
        hostAndPath = ModuleConfig.App.NAME + "/" + ModuleConfig.App.TEST_ERROR
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
                .navigate(new CallbackAdapter() {
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
