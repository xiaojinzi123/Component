package com.xiaojinzi.componentdemo.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.service.inter.app.AnnoMethodService;
import com.xiaojinzi.base.service.inter.component1.Component1Service;
import com.xiaojinzi.base.service.inter.component2.Component2Service;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.service.RxServiceManager;
import com.xiaojinzi.component.impl.service.ServiceManager;
import com.xiaojinzi.component.support.Utils;
import com.xiaojinzi.componentdemo.R;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 测试服务的界面
 *
 * @author xiaojinzi
 */
@RouterAnno(
        path = ModuleConfig.App.TEST_SERVICE,
        desc = "测试服务的界面"
)
public class TestServiceAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_service_act);
        getSupportActionBar().setTitle("服务的使用(单例服务、非单例服务)");
    }

    public void loadComponent1Fragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("age", 22);
        Fragment fragment = Router
                .with("component1.fragment")
                .putInt("age", 22)
                .navigate();
        if (fragment == null) {
            Toast.makeText(this, "对应的 component1.fragment 没有找到", Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl, fragment);
        ft.commit();
    }

    Component1Service service1 = null;
    Component2Service service2 = null;

    public void callComponent1Method(View view) {
        if (service1 == null) {
            Toast.makeText(this, "请先 find Component2Service服务", Toast.LENGTH_SHORT).show();
            return;
        }
        service1.doSomeThing();
    }

    public void callComponent2Method(View view) {
        if (service2 == null) {
            Toast.makeText(this, "请先 find Component2Service服务", Toast.LENGTH_SHORT).show();
            return;
        }
        service2.doSomeThing();
    }

    public void findComponent2Service(View view) {
        service2 = ServiceManager.get(Component2Service.class);
        if (service2 == null) {
            Toast.makeText(this, "Component2Service服务没找到", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void findComponent1Service(View view) {
        service1 = ServiceManager.get(Component1Service.class);
        if (service1 == null) {
            Toast.makeText(this, "Component1Service服务没找到", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void test(View view) {
        new Thread() {
            @Override
            public void run() {
                TestInterface testInterface = ServiceManager.get(TestInterface.class);
                System.out.println("testInterface = " + testInterface);
            }
        }.start();
    }

    public void rxServiceUse1(View view) {
        RxServiceManager.with(AnnoMethodService.class)
                .map(service -> service.test())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(TestServiceAct.this, "完成服务的调用啦,内容是：" + s, Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(TestServiceAct.this, "可以不用处理的错误,错误信息：" + Utils.getRealThrowable(throwable), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void rxServiceUse2(View view) {
        RxServiceManager.with(Component1Service.class)
                .flatMap((Function<Component1Service, SingleSource<String>>) service -> service.testError())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        s -> Toast.makeText(TestServiceAct.this, "完成服务的调用啦,内容是：" + s, Toast.LENGTH_SHORT).show(),
                        throwable -> Toast.makeText(TestServiceAct.this, "可以不用处理的错误,错误信息：" + Utils.getRealThrowable(throwable), Toast.LENGTH_SHORT).show()
                );
    }

    public interface TestInterface {
        void test();
    }

}
