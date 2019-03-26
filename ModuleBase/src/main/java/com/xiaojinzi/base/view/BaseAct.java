package com.xiaojinzi.base.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * time   : 2018/12/27
 *
 * @author : xiaojinzi 30212
 */
public class BaseAct extends AppCompatActivity {

    @NonNull
    protected Activity mContext;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

}
