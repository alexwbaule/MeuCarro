package com.alexwbaule.flexprofile.utils;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by alex on 8/22/15.
 */
public class ReportLabelFormatter implements YAxisValueFormatter {
    public enum types {DISTANCE, MONEY, MONEY_FULL, PERCENT, VOLUME, CONSUMO, NORMAL}

    private DecimalFormat mFormat;
    private types ttype;
    private SaveShowCalculos calculos;

    public ReportLabelFormatter(types t) {
        mFormat = new DecimalFormat("###,###,##0");
        if (t == types.MONEY) {
            mFormat = new DecimalFormat("###,###,##0.00");
        } else if (t == types.MONEY_FULL) {
            mFormat = new DecimalFormat("###,###,##0.000");
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (t == types.PERCENT) {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (t == types.NORMAL) {
            mFormat = new DecimalFormat("###,###,##0");
        }
        this.ttype = t;
        calculos = new SaveShowCalculos(MeuCarroApplication.getInstance());
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        if (ttype == types.DISTANCE)
            return calculos.getShowOdometrov2((double) value, false);
        if (ttype == types.MONEY || ttype == types.MONEY_FULL)
            return "R$ " + mFormat.format(value);
        if (ttype == types.PERCENT)
            return mFormat.format(value) + " %";
        if (ttype == types.VOLUME)
            return calculos.getShowVolumeTanquev2((double) value, false, false);
        if (ttype == types.CONSUMO)
            return calculos.getShowConsumov2((double) value, false);
        return mFormat.format(value);
    }
}