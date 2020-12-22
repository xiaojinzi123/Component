package com.xiaojinzi.component.help.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.help.R;
import com.xiaojinzi.component.impl.Router;

@RouterAnno(
        path = ModuleConfig.Help.TEST_WEB_ROUTER,
        // interceptorNames = InterceptorConfig.USER_LOGIN,
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
        // wv.loadUrl("www.baidu.com");
        wv.loadUrl(url);

    }

    /**
     * 打开网页
     */
    @JavascriptInterface
    public void openUrl(final String url) {
        Router.with(this).url(url).forward();
    }

}
