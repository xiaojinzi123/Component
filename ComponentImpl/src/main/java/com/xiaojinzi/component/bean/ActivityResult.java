package com.xiaojinzi.component.bean;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.error.ignore.ActivityResultException;

/**
 * activity result 的返回对象,{@link android.app.Activity#onActivityResult(int, int, Intent)}
 * time   : 2018/12/04
 *
 * @author : xiaojinzi 30212
 */
public class ActivityResult {

    public final int requestCode;

    public final int resultCode;

    @Nullable
    public final Intent data;

    public ActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public Intent intentCheckAndGet() throws ActivityResultException {
        if (data == null) {
            throw new ActivityResultException("the intent result data is null");
        }
        return data;
    }

    public Intent intentWithResultCodeCheckAndGet(int expectedResultCode) {
        if (data == null) {
            throw new ActivityResultException("the intent result data is null");
        }
        if (expectedResultCode != resultCode) {
            throw new ActivityResultException("the resultCode is not matching " + expectedResultCode);
        }
        return data;
    }

}
