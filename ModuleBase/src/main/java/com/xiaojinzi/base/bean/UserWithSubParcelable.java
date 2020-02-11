package com.xiaojinzi.base.bean;

import android.os.Parcel;

public class UserWithSubParcelable implements SubParcelable {

    private String name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public UserWithSubParcelable() {
    }

    protected UserWithSubParcelable(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<UserWithSubParcelable> CREATOR = new Creator<UserWithSubParcelable>() {
        @Override
        public UserWithSubParcelable createFromParcel(Parcel source) {
            return new UserWithSubParcelable(source);
        }

        @Override
        public UserWithSubParcelable[] newArray(int size) {
            return new UserWithSubParcelable[size];
        }
    };

}
