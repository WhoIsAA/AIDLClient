package com.aa.aidlserver;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public String name;
    public String describe;

    public Book(String name, String describe) {
        this.name = name;
        this.describe = describe;
    }

    protected Book(Parcel in) {
        name = in.readString();
        describe = in.readString();
    }

    //反序列化
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //序列化
        parcel.writeString(name);
        parcel.writeString(describe);
    }
}
