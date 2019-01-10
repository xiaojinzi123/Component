package com.ehi.componentdemo.view;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ehi.base.ModuleConfig;
import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.base.service.inter.component2.Component2Service;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.service.EHiRxService;
import com.ehi.component.impl.service.EHiService;
import com.ehi.componentdemo.R;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 测试服务的界面
 *
 * @author xiaojinzi
 */
@EHiRouterAnno(host = ModuleConfig.App.NAME, value = ModuleConfig.App.TEST_SERVICE, desc = "测试服务的界面")
public class TestServiceAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_service_act);
        getSupportActionBar().setTitle("服务的使用(单例服务、非单例服务)");
    }

    public void loadComponent1Fragment(View view) {

        if (service1 == null) {
            Toast.makeText(this, "请先 find Component1Service服务", Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl, service1.getFragment());
        ft.commit();

    }

    Component1Service service1 = null;
    Component2Service service2 = null;

    public void callComponent2Method(View view) {
        if (service2 == null) {
            Toast.makeText(this, "请先 find Component2Service服务", Toast.LENGTH_SHORT).show();
            return;
        }
        service2.doSomeThing();
    }

    public void findComponent2Service(View view) {
        service2 = EHiService.get(Component2Service.class);
        if (service2 == null) {
            Toast.makeText(this, "Component2Service服务没找到", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void findComponent1Service(View view) {
        service1 = EHiService.get(Component1Service.class);
        if (service1 == null) {
            Toast.makeText(this, "Component1Service服务没找到", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void rxServiceUse(View view) {
        EHiRxService.with(Component2Service.class)
                .doOnSuccess(new Consumer<Component2Service>() {
                    @Override
                    public void accept(Component2Service service) throws Exception {
                        service.doSomeThing();
                    }
                })
                .ignoreElement()
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("完成了");
                    }
                });
    }

}
