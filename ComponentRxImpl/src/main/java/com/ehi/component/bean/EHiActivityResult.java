package com.ehi.component.bean;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * activity result 的返回对象,{@link android.app.Activity#onActivityResult(int, int, Intent)}
 * time   : 2018/12/04
 *
 * @author : xiaojinzi 30212
 */
public class EHiActivityResult {

    public final int requestCode;

    public final int resultCode;

    @Nullable
    public final Intent data;

    public EHiActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

}
