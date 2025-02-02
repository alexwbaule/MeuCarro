package com.alexwbaule.flexprofile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.alexwbaule.flexprofile.R;


/**
 * Created by alex on 31/07/14.
 */
public class PreferencesProcessed {
    private Context context;

    protected String medida_consumo;
    protected int medida_consumo_id = 0;

    protected String medida_consumo_gnv;
    protected int medida_consumo_gnv_id = 0;

    protected String vol_tq;
    protected String s_vol_tq;
    protected int vol_tq_id = 0;

    protected String vol_gnv;
    protected String s_vol_gnv;

    protected int vol_gnv_id = 0;

    protected String odometro;
    protected int odometro_id = 0;

    protected String calccusto;
    protected int calccusto_id = 0;


    public PreferencesProcessed(Context ctx) {
        this.context = ctx;
        setPreferencesProcessed();
    }


    private void setPreferencesProcessed() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String un_consumo_comb = defaultSharedPreferences.getString("un_consumo_comb", "mpv");
        String un_consumo_gnv = defaultSharedPreferences.getString("un_consumo_gnv", "mpv");
        String un_volume_tanque = defaultSharedPreferences.getString("un_volume_tanque", "LTS");
        String un_volume_gnv = defaultSharedPreferences.getString("un_volume_gnv", "MTS");
        String un_medida_dist = defaultSharedPreferences.getString("un_medida_dist", "km");
        String un_calculo_custo = defaultSharedPreferences.getString("un_calculo_custo", "custo");


        if (un_consumo_comb != null && un_consumo_comb.equals("mpv")) {
            if ((un_volume_tanque != null && un_volume_tanque.equals("LTS")) && (un_medida_dist != null && un_medida_dist.equals("km"))) {
                medida_consumo = context.getString(R.string.km_l_simbolo);
                medida_consumo_id = 1;
            } else if ((un_volume_tanque != null && un_volume_tanque.equals("LTS")) && (un_medida_dist != null && un_medida_dist.equals("mi"))) {
                medida_consumo = context.getString(R.string.mi_l_simbolo);
                medida_consumo_id = 2;
            } else if (un_volume_tanque.equals("GUS") && un_medida_dist.equals("km")) {
                medida_consumo = context.getString(R.string.km_g_simbolo);
                medida_consumo_id = 3;
            } else if (un_volume_tanque.equals("GUS") && un_medida_dist.equals("mi")) {
                medida_consumo = context.getString(R.string.mpg_simbolo);
                medida_consumo_id = 4;
            } else if (un_volume_tanque.equals("GUK") && un_medida_dist.equals("km")) {
                medida_consumo = context.getString(R.string.km_g_simbolo);
                medida_consumo_id = 5;
            } else if (un_volume_tanque.equals("GUK") && un_medida_dist.equals("mi")) {
                medida_consumo = context.getString(R.string.mpg_simbolo);
                medida_consumo_id = 6;
            }
        } else if (un_consumo_comb.equals("v100m")) {
            if (un_volume_tanque.equals("LTS") && un_medida_dist.equals("km")) {
                medida_consumo = context.getString(R.string.l_100_km_simbolo);
                medida_consumo_id = 7;
            } else if (un_volume_tanque.equals("LTS") && un_medida_dist.equals("mi")) {
                medida_consumo = context.getString(R.string.l_100_mi_simbolo);
                medida_consumo_id = 8;
            } else if (un_volume_tanque.equals("GUS") && un_medida_dist.equals("km")) {
                medida_consumo = context.getString(R.string.g_100_km_simbolo);
                medida_consumo_id = 9;
            } else if (un_volume_tanque.equals("GUS") && un_medida_dist.equals("mi")) {
                medida_consumo = context.getString(R.string.g_100_m_simbolo);
                medida_consumo_id = 10;
            } else if (un_volume_tanque.equals("GUK") && un_medida_dist.equals("km")) {
                medida_consumo = context.getString(R.string.g_100_km_simbolo);
                medida_consumo_id = 11;
            } else if (un_volume_tanque.equals("GUK") && un_medida_dist.equals("mi")) {
                medida_consumo = context.getString(R.string.g_100_mi_simbolo);
                medida_consumo_id = 12;
            }
        }

        if (un_consumo_gnv.equals("mpv")) {
            if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("km")) {
                medida_consumo_gnv = context.getString(R.string.km_m_simbolo);
                medida_consumo_gnv_id = 1;
            } else if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("mi")) {
                medida_consumo_gnv = context.getString(R.string.mi_m_simbolo);
                medida_consumo_gnv_id = 2;
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("km")) {
                medida_consumo_gnv = context.getString(R.string.km_yd_simbolo);
                medida_consumo_gnv_id = 3;
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("mi")) {
                medida_consumo_gnv = context.getString(R.string.mi_yd_simbolo);
                medida_consumo_gnv_id = 4;
            }
        } else if (un_consumo_gnv.equals("v100m")) {
            if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("km")) {
                medida_consumo_gnv = context.getString(R.string.m_100_km_simbolo);
                medida_consumo_gnv_id = 5;
            } else if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("mi")) {
                medida_consumo_gnv = context.getString(R.string.m_100_m_simbolo);
                medida_consumo_gnv_id = 6;
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("km")) {
                medida_consumo_gnv = context.getString(R.string.yd_100_km_simbolo);
                medida_consumo_gnv_id = 7;
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("mi")) {
                medida_consumo_gnv = context.getString(R.string.yd_100_mi_simbolo);
                medida_consumo_gnv_id = 8;
            }
        }

        if (un_medida_dist.equals("km")) {
            odometro = context.getString(R.string.km_simbolo);
            odometro_id = 1;
        } else if (un_medida_dist.equals("mi")) {
            odometro = context.getString(R.string.milhas_simbolo);
            odometro_id = 2;
        }

        if (un_volume_tanque.equals("LTS")) {
            vol_tq = context.getString(R.string.litros);
            s_vol_tq = context.getString(R.string.default_lts);
            vol_tq_id = 1;
        } else if (un_volume_tanque.equals("GUS")) {
            vol_tq = context.getString(R.string.galoes);
            s_vol_tq = context.getString(R.string.default_gaus);
            vol_tq_id = 2;
        } else if (un_volume_tanque.equals("GUK")) {
            vol_tq = context.getString(R.string.galoes);
            s_vol_tq = context.getString(R.string.default_gauk);
            vol_tq_id = 3;
        }

        if (un_volume_gnv.equals("MTS")) {
            vol_gnv = context.getString(R.string.mcubicos);
            s_vol_gnv = context.getString(R.string.default_m3);
            vol_gnv_id = 1;
        } else if (un_volume_gnv.equals("YAD")) {
            vol_gnv = context.getString(R.string.jcubicas);
            s_vol_gnv = context.getString(R.string.default_pol3);
            vol_gnv_id = 2;
        }

        if (un_calculo_custo.equals("custo")) {
            calccusto = context.getString(R.string.custo);
            calccusto_id = 1;
        } else if (un_calculo_custo.equals("autonomia")) {
            calccusto = context.getString(R.string.autonomia);
            calccusto_id = 2;
        }
    }

    public String getVolumeSimbolTanque() {
        return s_vol_tq;
    }

    public String getVolumeSimbolGNV() {
        return s_vol_gnv;
    }

    public String getMedidor() {
        return medida_consumo;
    }

    public String getMedidorGNV() {
        return medida_consumo_gnv;
    }

    public String getVolumeTanque() {
        return vol_tq;
    }

    public String getVolumeGNV() {
        return vol_gnv;
    }

    public String getOdometro() {
        return odometro;
    }

    public String getcalccusto() {
        return calccusto;
    }

    public int getcalccusto_id() {
        return calccusto_id;
    }
}
