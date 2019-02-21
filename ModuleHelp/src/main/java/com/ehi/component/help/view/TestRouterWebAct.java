package com.ehi.component.help.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.help.R;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterErrorResult;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.support.EHiCallbackAdapter;
import com.ehi.component.support.Utils;

@EHiRouterAnno(
        host = ModuleConfig.Help.NAME,
        value = ModuleConfig.Help.TEST_WEB_ROUTER,
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
        EHiRouter.with(this)
                .url(url)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        super.onSuccess(result);
                        Toast.makeText(TestRouterWebAct.this, "路由成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(EHiRouterErrorResult errorResult) {
                        super.onError(errorResult);
                        Toast.makeText(TestRouterWebAct.this, "路由失败,class = " + Utils.getRealThrowable(errorResult.getError()).getClass().getSimpleName() + ",error msg = " + Utils.getRealMessage(errorResult.getError()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
