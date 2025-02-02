package com.alexwbaule.flexprofile.containers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by alex on 25/04/16.
 */
public class MaintenanceParts implements Parcelable, Comparable<MaintenanceParts> {
    private String partname;
    private int quantd;
    private double price;
    private int catg_id;
    private int categ_img;
    private long id;
    private String part_categname;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MaintenanceParts(String n, int qt, double p, int ct, int cti, long i, String categname) {
        super();
        partname = n;
        quantd = qt;
        price = p;
        catg_id = ct;
        id = i;
        categ_img = cti;
        part_categname = categname;
    }

    protected MaintenanceParts(Parcel in) {
        partname = in.readString();
        quantd = in.readInt();
        price = in.readDouble();
        catg_id = in.readInt();
        id = in.readLong();
        categ_img = in.readInt();
        part_categname = in.readString();
    }

    public MaintenanceParts(String part, String s, String s1, String s2, int cti, String s4, String categname) {
        super();
        try {
            partname = part;
            quantd = Integer.valueOf(s);
            price = Double.valueOf(s1);
            catg_id = Integer.valueOf(s2);
            id = Long.valueOf(s4);
            categ_img = cti;
            part_categname = categname;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "##### Error to Create MaintenanceParts");
        }
    }

    public String getQtdPrice() {
        NumberFormat format2 = new DecimalFormat("0.00");
        return String.format(Locale.getDefault(), "%d x %s", getQuantd(), format2.format(price));
    }

    public String getTotal() {
        NumberFormat format2 = new DecimalFormat("0.00");
        double v = price * quantd;
        return rootapp.getString(R.string.simbolo_moeda) + " " + format2.format(v);
    }

    public static final Creator<MaintenanceParts> CREATOR = new Creator<MaintenanceParts>() {
        @Override
        public MaintenanceParts createFromParcel(Parcel in) {
            return new MaintenanceParts(in);
        }

        @Override
        public MaintenanceParts[] newArray(int size) {
            return new MaintenanceParts[size];
        }
    };

    public String getPart_categname() {
        return part_categname;
    }

    public void setPart_categname(String part_categname) {
        this.part_categname = part_categname;
    }

    public int getCateg_img() {
        return categ_img;
    }

    public void setCateg_img(int categ_img) {
        this.categ_img = categ_img;
    }

    public String getPartname() {
        return partname;
    }

    public void setPartname(String partname) {
        this.partname = partname;
    }

    public int getQuantd() {
        return quantd;
    }

    public void setQuantd(int quantd) {
        this.quantd = quantd;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCatg_id() {
        return catg_id;
    }

    public void setCatg_id(int catg_id) {
        this.catg_id = catg_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(partname);
        dest.writeInt(quantd);
        dest.writeDouble(price);
        dest.writeInt(catg_id);
        dest.writeInt(categ_img);
        dest.writeString(part_categname);
        dest.writeLong(id);
    }

    @Override
    public int compareTo(MaintenanceParts another) {
        return catg_id < another.catg_id ? -1 : (catg_id == another.catg_id ? 0 : 1);
    }
}
