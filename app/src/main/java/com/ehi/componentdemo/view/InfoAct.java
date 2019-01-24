package com.ehi.componentdemo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.support.ParameterSupport;
import com.ehi.componentdemo.R;

@EHiRouterAnno(host = ModuleConfig.App.NAME,value = ModuleConfig.App.INFO)
public class InfoAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_act);

        String data = ParameterSupport.getString(getIntent(), "data");
        ((TextView) findViewById(R.id.tv)).setText(data);

    }

}
