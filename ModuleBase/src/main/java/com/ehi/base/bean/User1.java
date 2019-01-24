package com.ehi.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time   : 2019/01/24
 *
 * @author : xiaojinzi 30212
 */
public class User1 implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public User1() {
    }

    protected User1(Parcel in) {
    }

    public static final Creator<User1> CREATOR = new Creator<User1>() {
        @Override
        public User1 createFromParcel(Parcel source) {
            return new User1(source);
        }

        @Override
        public User1[] newArray(int size) {
            return new User1[size];
        }
    };
}
