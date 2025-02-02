package com.alexwbaule.flexprofile.utils;

import android.content.Context;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by alex on 23/07/15.
 */
public class SaveShowCalculos extends Calculos {
    private Context context;

    public SaveShowCalculos(Context ctx) {
        super(ctx);
        this.context = ctx;
    }

    public String getShowConsumo(double value) {
        return getShowConsumov2(value, true);
    }

    public String getShowConsumov2(double value, boolean sign) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);

        double resc = 0.000f;

        String v = "";

        if (sign) {
            v = " " + medida_consumo;
        }

        switch (medida_consumo_id) {
            case 1:
                return parser.format(value) + v;
            case 2:
                resc = kml_to_mil(value);
                return parser.format(resc) + v;
            case 3:
                resc = kml_to_kmg(value);
                return parser.format(resc) + v;
            case 4:
                resc = kml_to_mpg(value);
                return parser.format(resc) + v;
            case 5:
                resc = kml_to_kmgk(value);
                return parser.format(resc) + v;
            case 6:
                resc = kml_to_mpgk(value);
                return parser.format(resc) + v;
            case 7:
                resc = kml_to_l100(value);
                return parser.format(resc) + v;
            case 8:
                resc = kml_to_l100m(value);
                return parser.format(resc) + v;
            case 9:
                resc = kml_to_g100k(value);
                return parser.format(resc) + v;
            case 10:
                resc = kml_to_g100m(value);
                return parser.format(resc) + v;
            case 11:
                resc = kml_to_gk100k(value);
                return parser.format(resc) + v;
            case 12:
                resc = kml_to_gk100m(value);
                return parser.format(resc) + v;
        }
        return parser.format(value) + v;
    }

    public double getSaveConsumo(double value) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        ////parser.setMinimumFractionDigits(2);


        double resc = 0.000f;

        switch (medida_consumo_id) {
            case 1:
                return value;
            case 2:
                return mil_to_kml(value);
            case 3:
                return kmg_to_kml(value);
            case 4:
                return mpg_to_kml(value);
            case 5:
                return kmgk_to_kml(value);
            case 6:
                return mpgk_to_kml(value);
            case 7:
                return l100_to_kml(value);
            case 8:
                return l100m_to_kml(value);
            case 9:
                return g100k_to_kml(value);
            case 10:
                return g100m_to_kml(value);
            case 11:
                return gk100k_to_kml(value);
            case 12:
                return gk100m_to_kml(value);
        }
        return value;
    }

    public String getShowConsumoGNV(double value) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);

        double resc = 0.000f;

        switch (medida_consumo_gnv_id) {
            case 1:
                return parser.format(value) + " " + medida_consumo_gnv;
            case 2:
                resc = kmm_to_mim(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
            case 3:
                resc = kmm_to_kmy(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
            case 4:
                resc = kmm_to_miy(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
            case 5:
                resc = kmm_to_m100k(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
            case 6:
                resc = kmm_to_m100m(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
            case 7:
                resc = kmm_to_y100k(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
            case 8:
                resc = kmm_to_y100m(value);
                return parser.format(resc) + " " + medida_consumo_gnv;
        }
        return parser.format(value) + " " + medida_consumo_gnv;
    }

    public double getSaveConsumoGNV(double value) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);


        double resc = 0.000f;

        switch (medida_consumo_gnv_id) {
            case 1:
                return value;
            case 2:
                return mim_to_kmm(value);
            case 3:
                return kmy_to_kmm(value);
            case 4:
                return miy_to_kmm(value);
            case 5:
                return m100k_to_kmm(value);
            case 6:
                return m100m_to_kmm(value);
            case 7:
                return y100k_to_kmm(value);
            case 8:
                return y100m_to_kmm(value);
        }
        return value;
    }

    public String getShowVolumeMix(double value, int type) {
        if (type == 3) {
            return getShowVolumeGNVv2(value, false, false);
        }
        return getShowVolumeTanquev2(value, false, false);
    }


    public String getShowVolumeTanque(double value) {
        return getShowVolumeTanquev2(value, true, true);
    }

    public String getShowVolumeTanquev2(double value, boolean sign, boolean longtype) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(2);
        //parser.setMinimumFractionDigits(2);


        String v = "";

        if (sign) {
            v = " " + s_vol_tq;
            if (longtype)
                v = " " + vol_tq;
        }

        double resc = 0.000f;
        switch (vol_tq_id) {
            case 1:
                return parser.format(value) + v;
            case 2:
                resc = lts_to_gls(value);
                return parser.format(resc) + v;
            case 3:
                resc = lts_to_glsk(value);
                return parser.format(resc) + v;
        }
        return parser.format(value) + v;
    }

    public double getSaveVolume(double value) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(2);
        //parser.setMinimumFractionDigits(2);


        double resc = 0.000f;
        switch (vol_tq_id) {
            case 1:
                return value;
            case 2:
                return gls_to_lts(value);
            case 3:
                return glsk_to_lts(value);
        }
        return value;
    }

    public String getShowVolumeGNV(double value) {
        return getShowVolumeGNVv2(value, true, true);
    }

    public String getShowVolumeGNVv2(double value, boolean sign, boolean longtype) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(2);
        //parser.setMinimumFractionDigits(2);

        String v = "";

        if (sign) {
            v = " " + s_vol_gnv;
            if (longtype)
                v = " " + vol_gnv;
        }

        double resc = 0.000f;
        switch (vol_gnv_id) {
            case 1:
                return parser.format(value) + v;
            case 2:
                resc = mts_to_yard(value);
                return parser.format(resc) + v;
        }
        return parser.format(value) + v;
    }

    public double getSaveVolumeGNV(double value) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(2);
        //parser.setMinimumFractionDigits(2);


        double resc = 0.000f;
        switch (vol_gnv_id) {
            case 1:
                return value;
            case 2:
                return yard_to_mts(value);
        }
        return value;
    }

    public long getShowOdometroValue(double value) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(0);
        parser.setRoundingMode(RoundingMode.HALF_UP);

        double resc = 0.000f;
        switch (odometro_id) {
            case 1:
                return (long) value;
            case 2:
                return (long) km_to_mi(value);
        }
        return (long) value;
    }

    public String getShowOdometro(double value) {
        return getShowOdometrov2(value, true);
    }

    public String getShowOdometrov2(double value, boolean sign) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(0);
        parser.setRoundingMode(RoundingMode.HALF_UP);
        String v = "";

        if (sign)
            v = " " + odometro;

        double resc = 0.000f;
        switch (odometro_id) {
            case 1:
                return parser.format(value) + v;
            case 2:
                resc = km_to_mi(value);
                return parser.format(resc) + v;
        }
        return parser.format(value) + v;
    }

    public double getSaveOdometro(long v) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(0);
        parser.setRoundingMode(RoundingMode.CEILING);
        double value = (double) v;

        double resc = 0.000f;
        switch (odometro_id) {
            case 1:
                return value;
            case 2:
                return mi_to_km(value);
        }
        return value;
    }
}
