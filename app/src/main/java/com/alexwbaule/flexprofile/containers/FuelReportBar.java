package com.alexwbaule.flexprofile.containers;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.github.mikephil.charting.data.BarData;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FuelReportBar {
    BarData data;
    float media;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();


    public FuelReportBar(BarData data, float media) {
        this.data = data;
        this.media = media;
    }

    public BarData getData() {
        return data;
    }

    public float getMedia() {
        return media;
    }

    public String getMediaKM() {
        NumberFormat format2 = new DecimalFormat("0");
        return  format2.format(media) + " " +rootapp.getString(R.string.km_simbolo);
    }
}
