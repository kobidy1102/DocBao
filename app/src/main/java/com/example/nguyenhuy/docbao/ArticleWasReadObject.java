package com.example.nguyenhuy.docbao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nguyenhuy on 17/11/2017.
 */

public class ArticleWasReadObject implements Parcelable {
    int id;
    String title;
    String link;

    public ArticleWasReadObject(int id, String title, String link) {
        this.id=id;
        this.title = title;
        this.link = link;
    }

    protected ArticleWasReadObject(Parcel in) {
        id=in.readInt();
        title = in.readString();
        link = in.readString();
    }

    public static final Creator<ArticleWasReadObject> CREATOR = new Creator<ArticleWasReadObject>() {
        @Override
        public ArticleWasReadObject createFromParcel(Parcel in) {
            return new ArticleWasReadObject(in);
        }

        @Override
        public ArticleWasReadObject[] newArray(int size) {
            return new ArticleWasReadObject[size];
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
