package com.xiaojinzi.componentdemo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.componentdemo.R;

@RouterAnno(
        path = ModuleConfig.App.INFO
)
public class InfoAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.info_act);
        String data = ParameterSupport.getString(getIntent(), "data");
        ((TextView) findViewById(R.id.tv)).setText(data);
    }

}
