package com.xiaojinzi.component.help.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.help.R;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.component.support.Utils;

@RouterAnno(
        path = ModuleConfig.Help.TEST_WEB_ROUTER,
        desc = "网页测试跳转"
)
public class TestRouterWebAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_test_router_web);
        WebView wv = findViewById(R.id.wv);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(this, "testWebRouter");

        String url = "file:///android_asset/index.html";
        wv.loadUrl(url);

    }

    /**
     * 打开网页
     *
     * @param url
     */
    @JavascriptInterface
    public void openUrl(final String url) {
        Router.with(this)
                .url(url)
                .navigate(new CallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull RouterResult result) {
                        super.onSuccess(result);
                        Toast.makeText(TestRouterWebAct.this, "路由成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(RouterErrorResult errorResult) {
                        super.onError(errorResult);
                        Toast.makeText(TestRouterWebAct.this, "路由失败,class = " + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ",error msg = " + Utils.getRealMessage(errorResult.getError()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
