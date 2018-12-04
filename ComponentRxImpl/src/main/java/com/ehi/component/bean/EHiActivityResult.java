package com.ehi.component.bean;

import android.content.Intent;

/**
 * activity result 的返回对象
 * time   : 2018/12/04
 *
 * @author : xiaojinzi 30212
 */
public class EHiActivityResult {

    public final int requestCode;

    public final int resultCode;

    public final Intent data;

    public EHiActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

}
