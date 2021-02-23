package com.xiaojinzi.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * time   : 2018/12/03
 *
 * @author : xiaojinzi
 */
public class User implements Serializable, Parcelable {

    public String name = "testName";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
