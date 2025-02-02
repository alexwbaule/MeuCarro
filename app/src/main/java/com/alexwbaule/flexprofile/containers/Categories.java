package com.alexwbaule.flexprofile.containers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 25/04/16.
 */
public class Categories implements Parcelable, Comparable<Categories> {
    private String categoria;
    private int image;
    private int id;

    public Categories(String s, int f, int i) {
        this.categoria = s;
        this.image = f;
        this.id = i;
    }

    protected Categories(Parcel in) {
        categoria = in.readString();
        image = in.readInt();
        id = in.readInt();
    }

    public static final Creator<Categories> CREATOR = new Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoria);
        dest.writeInt(image);
        dest.writeInt(id);
    }

    @Override
    public int compareTo(Categories another) {
        return id < another.id ? -1 : (id == another.id ? 0 : 1);
    }
}
