package com.xiaojinzi.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time   : 2019/01/24
 *
 * @author : xiaojinzi
 */
public class UserWithParcelable implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public UserWithParcelable() {
    }

    protected UserWithParcelable(Parcel in) {
    }

    public static final Creator<UserWithParcelable> CREATOR = new Creator<UserWithParcelable>() {
        @Override
        public UserWithParcelable createFromParcel(Parcel source) {
            return new UserWithParcelable(source);
        }

        @Override
        public UserWithParcelable[] newArray(int size) {
            return new UserWithParcelable[size];
        }
    };
}
