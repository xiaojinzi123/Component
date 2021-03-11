package com.xiaojinzi.base.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xiaojinzi.component.support.ParameterSupport;

import io.reactivex.disposables.CompositeDisposable;

/**
 * time   : 2018/12/27
 *
 * @author : xiaojinzi
 */
public class BaseAct extends AppCompatActivity {

    public static final int RESULT_ERROR = 4;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    protected Activity mContext;

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            returnData();
        }
    };

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Boolean isReturnAuto = isReturn();
        if (isReturnAuto) {
            h.sendEmptyMessageDelayed(0, 1000);
        }
    }

    protected boolean isReturnIntent() {
        return ParameterSupport.getBoolean(getIntent(), "isReturnIntent", true);
    }

    protected boolean isReturnError() {
        return ParameterSupport.getBoolean(getIntent(), "isReturnError", false);
    }

    protected boolean isReturn() {
        return ParameterSupport.getBoolean(getIntent(), "isReturnAuto", false);
    }

    protected void returnData() {
        Intent intent = new Intent();
        intent.putExtra("data", "this is the return data，requestData");
        if (!isReturnIntent()) {
            intent = null;
        }
        if (isReturnError()) {
            setResult(RESULT_ERROR, intent);
        }else {
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        super.onDestroy();
    }
}
