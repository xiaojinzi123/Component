package com.xiaojinzi.component.help.view;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.help.R;
import com.xiaojinzi.component.support.ParameterSupport;

@RouterAnno(
        regex = "^(http|https)(.*)",
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
        Uri uri = ParameterSupport.getUri(getIntent());
        if (uri == null) {
            wv.loadUrl("file:///android_asset/404.html");
        }else {
            wv.loadUrl(uri.toString());
        }
    }

}
