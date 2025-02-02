package com.alexwbaule.flexprofile.containers;

import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by alex on 12/07/15.
 */

public class Oils implements Serializable {
    private String created_date;
    private String created_time;
    private String month;
    private String year;
    private String day;
    private double km;
    private double next_km;
    private double litros;
    private boolean has_filter;
    private double price;
    private String oil_name;
    private long id;
    private long header;
    private boolean selected;
    SaveShowCalculos calcconsumo;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();

    public Oils(String created_date, String created_time, double km, double next_km, double price, double litros,
                boolean has_filter, String oil_name, long id, long headerid, String fyear, String fmonth, String fday) {
        this.created_date = created_date;
        this.created_time = created_time;
        this.km = km;
        this.next_km = next_km;
        this.price = price;
        this.litros = litros;
        this.has_filter = has_filter;
        this.oil_name = oil_name;
        this.id = id;
        this.header = headerid;
        this.year = fyear;
        this.month = fmonth;
        this.day = fday;
        this.selected = false;
        calcconsumo = new SaveShowCalculos(rootapp);
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

    public boolean isSelected() {
        return selected;
    }

    public String getTotalPrice() {
        NumberFormat format2 = new DecimalFormat("0.00");
        double v = price * litros;
        return rootapp.getString(R.string.simbolo_moeda) + " " + format2.format(v);
    }

    public double getvPrice() {
        return price;
    }

    public double getvTotalPrice() {
        double v = price * litros;
        return v;
    }

    public String getPrice() {
        NumberFormat format2 = new DecimalFormat("0.00");
        return rootapp.getString(R.string.simbolo_moeda) + " " + format2.format(price);
    }

    public void setSelected() {
        this.selected = !selected;
        Log.d(getClass().getSimpleName(), "SELECTED => " + selected);
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getKm() {
        return calcconsumo.getShowOdometro(km);
    }

    public String getNext_km() {
        return calcconsumo.getShowOdometro(next_km);
    }

    public String getLitros() {
        return calcconsumo.getShowVolumeTanque(litros);
    }

    public boolean isHas_filter() {
        return has_filter;
    }

    public String getOil_name() {
        return oil_name;
    }

    public long getId() {
        return id;
    }

    public String getCreated_time() {
        return created_time;
    }
}
