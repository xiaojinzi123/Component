package com.xiaojinzi.component.help.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.help.R;

@RouterAnno(
        path = ModuleConfig.Help.WEB,
        desc = "项目的网页展示界面"
)
public class WebAct extends AppCompatActivity {

    @NonNull
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_web_act);
        wv = findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient(){
        });
        String url = getIntent().getStringExtra("data");
        if (TextUtils.isEmpty(url)) {
            wv.loadUrl("file:///android_asset/404.html");
        }else {
            wv.loadUrl(url);
        }
    }

}
