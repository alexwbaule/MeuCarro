package com.alexwbaule.flexprofile.containers;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PriceAndKm {
    private double price;
    private double km;
    private double days;
    private double mediakm;
    private double litros;
    private double totalkm;

    private NumberFormat format2;
    private NumberFormat format;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private SaveShowCalculos calcconsumo;


    public PriceAndKm() {
        this.price = 0;
        this.km = 0;
        this.days = 0;
        this.mediakm = 0;
        this.litros = 0;
        this.totalkm = 0;
        format2 = new DecimalFormat("0.00");
        format = new DecimalFormat("0");
        calcconsumo = new SaveShowCalculos(rootapp);
    }

    public PriceAndKm(double price, double km, double days, double mediakm, double litros, double totalkm) {
        this.price = price;
        this.km = km;
        this.days = days;
        this.mediakm = mediakm;
        this.litros = litros;
        this.totalkm = totalkm;
        format2 = new DecimalFormat("0.00");
        format = new DecimalFormat("0");
        calcconsumo = new SaveShowCalculos(rootapp);
    }

    public String getPrice() {
        return rootapp.getString(R.string.simbolo_moeda_valor, format2.format(price));
    }

    public String getKm() {
        return rootapp.getString(R.string.simbolo_moeda_valor, format2.format(km));
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public String getDays() {
        return rootapp.getString(R.string.simbolo_moeda_valor, format2.format(days));
    }

    public void setDays(double days) {
        this.days = days;
    }

    public String getMediakm() {
        return calcconsumo.getShowConsumo(mediakm);
    }

    public void setMediakm(double mediakm) {
        this.mediakm = mediakm;
    }

    public String getLitros() {
        return calcconsumo.getShowVolumeTanque(litros);
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    public String getTotalkm() {
        return calcconsumo.getShowOdometro(totalkm);
    }

    public void setTotalkm(double totalkm) {
        this.totalkm = totalkm;
    }
}
