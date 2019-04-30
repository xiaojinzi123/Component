package com.xiaojinzi.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi 30212
 */
public class User implements Serializable, Parcelable {

    public String name = "testName";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
