package com.ehi.component.help.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.help.R;
import com.ehi.component.impl.EHiRouter;

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
                .navigate();
    }

}
