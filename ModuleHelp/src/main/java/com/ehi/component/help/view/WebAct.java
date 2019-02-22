package com.ehi.component.help.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.help.R;

@EHiRouterAnno(
        host = ModuleConfig.Help.NAME,
        value = ModuleConfig.Help.WEB,
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
