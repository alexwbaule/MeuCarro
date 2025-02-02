package com.alexwbaule.flexprofile.containers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by alex on 23/04/16.
 */
public class Maintenances implements Parcelable {
    private String maintenance_local;
    private String created_date;
    private String created_time;
    private String month;
    private String year;
    private String day;
    private double km;
    private long id;
    private long header;
    private TreeMap<Categories, ArrayList<MaintenanceParts>> partsHash;

    SaveShowCalculos calcconsumo;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private boolean selected;
    private int countTotal;
    private double priceTotal;

    public Maintenances(String maintenance_local, String created_date, String created_time, double km, long id, long headerid, String fyear, String fmonth, String fday, TreeMap<Categories, ArrayList<MaintenanceParts>> parts) {
        this.maintenance_local = maintenance_local;
        this.created_date = created_date;
        this.created_time = created_time;
        this.km = km;
        this.id = id;
        this.header = headerid;
        this.year = fyear;
        this.month = fmonth;
        this.day = fday;
        this.partsHash = parts;
        calcconsumo = new SaveShowCalculos(rootapp);
        parseObject();
    }

    protected Maintenances(Parcel in) {
        maintenance_local = in.readString();
        created_date = in.readString();
        created_time = in.readString();
        km = in.readDouble();
        id = in.readLong();
        this.header = in.readLong();
        this.year = in.readString();
        this.month = in.readString();
        this.day = in.readString();
        partsHash = (TreeMap<Categories, ArrayList<MaintenanceParts>>) in.readSerializable();
        selected = in.readByte() != 0;
    }

    public static final Creator<Maintenances> CREATOR = new Creator<Maintenances>() {
        @Override
        public Maintenances createFromParcel(Parcel in) {
            return new Maintenances(in);
        }

        @Override
        public Maintenances[] newArray(int size) {
            return new Maintenances[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        this.selected = !selected;
        Log.d(getClass().getSimpleName(), "SELECTED => " + selected);
    }


    public String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, (Integer.valueOf(month) - 1));
        return StringUtils.capitalize(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
    }

    public String getYear() {
        return year;
    }

    public String getDay() {
        return day;
    }

    public long getHeader() {
        return header;
    }

    public String getMaintenance_local() {
        return maintenance_local;
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getCreated_time() {
        return created_time;
    }

    public String getKm() {
        return calcconsumo.getShowOdometro(km);
    }

    public String getSignKm() {
        return calcconsumo.getShowOdometrov2(km, false);
    }

    public long getId() {
        return id;
    }

    public TreeMap<Categories, ArrayList<MaintenanceParts>> getParts() {
        return partsHash;
    }

    public String getTotal() {
        NumberFormat format2 = new DecimalFormat("0.00");
        return rootapp.getString(R.string.simbolo_moeda) + " " + format2.format(priceTotal);
    }

    public double getRealTotal() {
        return priceTotal;
    }

    public int getCountParts() {
        return countTotal;
    }

    protected void parseObject() {
        countTotal = 0;
        for (Categories d : partsHash.keySet()) {
            for (MaintenanceParts p : partsHash.get(d)) {
                countTotal++;
                priceTotal += p.getQuantd() * p.getPrice();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maintenance_local);
        dest.writeString(created_date);
        dest.writeString(created_time);
        dest.writeDouble(km);
        dest.writeLong(id);
        dest.writeLong(header);
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(day);
        dest.writeSerializable(partsHash);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
