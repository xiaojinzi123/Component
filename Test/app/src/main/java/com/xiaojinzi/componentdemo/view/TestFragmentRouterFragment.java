package com.xiaojinzi.componentdemo.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.interceptor.DialogShowInterceptor;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.componentdemo.R;

/**
 * time   : 2018/12/27
 *
 * @author : xiaojinzi
 */
public class TestFragmentRouterFragment extends Fragment implements View.OnClickListener {

    private TextView tv_detail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.test_fragment_router_frag, null);
        contentView.findViewById(R.id.normalJump).setOnClickListener(this);
        contentView.findViewById(R.id.rxJumpGetData).setOnClickListener(this);
        contentView.findViewById(R.id.testCallbackAfterFinish).setOnClickListener(this);
        contentView.findViewById(R.id.testCallbackAfterFinishActivity).setOnClickListener(this);
        contentView.findViewById(R.id.bt_clearInfo).setOnClickListener(this);
        tv_detail = contentView.findViewById(R.id.tv_detail);
        return contentView;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rxJumpGetData) {
            rxJumpGetData();
        } else if (viewId == R.id.normalJump) {
            normalJump();
        } else if (viewId == R.id.testCallbackAfterFinish) {
            testCallbackAfterFinish();
        } else if (viewId == R.id.testCallbackAfterFinishActivity) {
            testCallbackAfterFinishActivity();
        } else if (viewId == R.id.bt_clearInfo) {
            tv_detail.setText("");
        }
    }

    private void addInfo(@Nullable RouterResult routerResult, @Nullable Throwable error, @NonNull String url, @Nullable Integer requestCode) {
        if (requestCode == null) {
            if (routerResult != null) {
                tv_detail.setText(tv_detail.getText() + "\n\n普通跳转成功,目标:" + url);
            } else {
                tv_detail.setText(tv_detail.getText() + "\n\n普通跳转失败,目标:" + url + ",error = " + error.getClass().getSimpleName() + " ,errorMsg = " + error.getMessage());
            }
        } else {
            if (routerResult != null) {
                tv_detail.setText(tv_detail.getText() + "\n\nRequestCode=" + requestCode + "普通跳转成功,目标:" + url);
            } else {
                tv_detail.setText(tv_detail.getText() + "\n\nRequestCode=" + requestCode + "普通跳转失败,目标:" + url + ",error = " + error.getClass().getSimpleName() + " ,errorMsg = " + error.getMessage());
            }
        }
    }

    private void rxJumpGetData() {
        RxRouter
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "rxJumpGetData")
                .requestCode(456)
                .intentCall()
                .subscribe(
                        intent -> tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=456,目标:component1/test?data=rxJumpGetData,获取目标页面数据成功啦：Data = " + intent.getStringExtra("data")),
                        throwable -> tv_detail.setText(tv_detail.getText() + "\n\nrequestCode=456,目标:component1/test?data=rxJumpGetData,获取目标页面数据失败,error = " + throwable.getClass().getSimpleName() + " ,errorMsg = " + throwable.getMessage())
                );
    }

    private void normalJump() {
        Router
                .with(this)
                .host("component1")
                .path("test")
                .query("data", "normalJump")
                .putString("name", "cxj1")
                .putInt("age", 25)
                .forward(new CallbackAdapter() {
                    @Override
                    public void onSuccess(@NonNull RouterResult result) {
                        addInfo(result, null, "component1/test?data=normalJump", null);
                    }

                    @Override
                    public void onError(@NonNull RouterErrorResult errorResult) {
                        addInfo(null, errorResult.getError(), "component1/test?data=normalJump", null);
                    }
                });
    }

    public void testCallbackAfterFinish() {
        Router
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .putString("tel", "xxx")
                .interceptors(DialogShowInterceptor.class)
                .forward(new CallbackAdapter() {
                    @Override
                    public void onEvent(@Nullable RouterResult result, @Nullable RouterErrorResult errorResult) {
                    }

                    @Override
                    public void onCancel(@NonNull RouterRequest request) {
                        super.onCancel(request);
                        Toast.makeText(Component.getApplication(), "被自动取消了", Toast.LENGTH_SHORT).show();
                    }
                });

        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void testCallbackAfterFinishActivity() {
        Router
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .putString("tel", "xxx")
                .interceptors(new RouterInterceptor() {
                    @Override
                    public void intercept(final Chain chain) throws Exception {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                                chain.proceed(chain.request());
                            }
                        }.start();
                    }
                })
                .interceptors(DialogShowInterceptor.class)
                .forward(new CallbackAdapter() {
                    @Override
                    public void onEvent(@Nullable RouterResult result, @Nullable RouterErrorResult errorResult) {
                        Toast.makeText(Component.getApplication(), "onEvent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(@NonNull RouterRequest request) {
                        super.onCancel(request);
                        Toast.makeText(Component.getApplication(), "被自动取消了", Toast.LENGTH_SHORT).show();
                    }
                });
        getActivity().finish();
    }

}
