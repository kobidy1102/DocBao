package com.example.nguyenhuy.docbao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nguyenhuy on 17/11/2017.
 */

public class TinDaDoc implements Parcelable {
    int id;
    String title;
    String link;

    public TinDaDoc(int id,String title, String link) {
        this.id=id;
        this.title = title;
        this.link = link;
    }

    protected TinDaDoc(Parcel in) {
        id=in.readInt();
        title = in.readString();
        link = in.readString();
    }

    public static final Creator<TinDaDoc> CREATOR = new Creator<TinDaDoc>() {
        @Override
        public TinDaDoc createFromParcel(Parcel in) {
            return new TinDaDoc(in);
        }

        @Override
        public TinDaDoc[] newArray(int size) {
            return new TinDaDoc[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(link);
    }
}
