package com.alexwbaule.flexprofile.utils;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by alex on 8/22/15.
 */
public class ReportValueFormatter implements ValueFormatter {
    public enum types {DISTANCE, MONEY, MONEY_FULL, PERCENT, VOLUME, CONSUMO, MONEY_CALC}

    private DecimalFormat mFormat;
    private types ttype;
    private SaveShowCalculos calculos;

    public ReportValueFormatter(types t) {
        mFormat = new DecimalFormat("###,###,##0");
        if (t == types.DISTANCE) {
            mFormat = new DecimalFormat("###,###,##0");
        } else if (t == types.MONEY) {
            mFormat = new DecimalFormat("###,###,##0.00");
        } else if (t == types.MONEY_FULL) {
            mFormat = new DecimalFormat("###,###,##0.000");
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (t == types.MONEY_CALC) {
            mFormat = new DecimalFormat("###,###,##0.0000");
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (t == types.PERCENT) {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (t == types.VOLUME) {
            mFormat = new DecimalFormat("###,###,##0.000"); // use one decimal
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (t == types.CONSUMO) {
            mFormat = new DecimalFormat("###,###,##0.000");
            mFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        }
        this.ttype = t;
        calculos = new SaveShowCalculos(MeuCarroApplication.getInstance());
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (ttype == types.DISTANCE)
            return calculos.getShowOdometro((double) value);
        if (ttype == types.MONEY || ttype == types.MONEY_FULL || ttype == types.MONEY_CALC)
            return MeuCarroApplication.getInstance().getString(R.string.simbolo_moeda) + " " + mFormat.format(value);
        if (ttype == types.PERCENT)
            return mFormat.format(value) + " %";
        if (ttype == types.VOLUME)
            return calculos.getShowVolumeTanquev2((double) value, true, false);
        if (ttype == types.CONSUMO)
            return calculos.getShowConsumo((double) value);
        return mFormat.format(value);
    }
}