package com.alexwbaule.flexprofile.containers;

import android.graphics.Bitmap;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.utils.DbBitmapUtility;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;

import java.io.Serializable;

/**
 * Created by alex on 12/07/15.
 */
public class Vehicles implements Serializable {
    private long _id;
    private long header;
    private String icon_id;
    private boolean isSelected;
    private String profile_name;
    private double tanque_comb;
    private double tanque_gnv;
    private double gasolina;
    private double Etanol;
    private double gnv;
    private double diesel;
    SaveShowCalculos calcconsumo;

    public Vehicles() {

    }

    public Vehicles(long _id, long headerid, String icon_id, String profile_name, double tanque_comb, double tanque_gnv, double gasolina, double Etanol, double gnv, double diesel) {
        this._id = _id;
        this.icon_id = icon_id;
        this.profile_name = profile_name;
        this.tanque_comb = tanque_comb;
        this.tanque_gnv = tanque_gnv;
        this.gasolina = gasolina;
        this.Etanol = Etanol;
        this.gnv = gnv;
        this.diesel = diesel;
        this.header = headerid;
        this.isSelected = false;
        calcconsumo = new SaveShowCalculos(MeuCarroApplication.getInstance());
    }

    public Bitmap getCarImage() {
        return DbBitmapUtility.readbitmap(MeuCarroApplication.getInstance(), String.valueOf(_id));
    }


    public long getHeader() {
        return header;
    }

    public String toString() {
        return "ID = " + get_id() +
                " ICON_ID = " + getIcon_id() +
                " PROFILE_NAME = " + getProfile_name() +
                " TANQUE = " + getTanque_comb() +
                " TQGNV = " + getTanque_gnv() +
                " GASOLINA = " + getGasolina() +
                " ALCOOL = " + getEtanol() +
                " GNV = " + getGnv() +
                " DIESEL = " + getDiesel() +
                " IS_SELECTED = " + getisSelected();

    }

    public boolean getisSelected() {
        return isSelected;
    }

    public void set_Selected() {
        isSelected = !isSelected;
    }

    public long get_id() {
        return _id;
    }

    public String getIcon_id() {
        return icon_id;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public String getTanque_comb() {
        if (tanque_comb > 0)
            return calcconsumo.getShowVolumeTanque(tanque_comb);
        return null;
    }

    public String getTanque_gnv() {
        if (tanque_gnv > 0)
            return calcconsumo.getShowVolumeGNV(tanque_gnv);
        return null;
    }

    public String getGasolina() {
        if (gasolina > 0)
            return calcconsumo.getShowConsumo(gasolina);
        return null;
    }

    public String getEtanol() {
        if (Etanol > 0)
            return calcconsumo.getShowConsumo(Etanol);
        return null;
    }

    public String getGnv() {
        if (gnv > 0)
            return calcconsumo.getShowConsumoGNV(gnv);
        return null;
    }

    public String getDiesel() {
        if (diesel > 0)
            return calcconsumo.getShowConsumo(diesel);
        return null;
    }
}
