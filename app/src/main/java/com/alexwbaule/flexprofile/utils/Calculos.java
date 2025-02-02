package com.alexwbaule.flexprofile.utils;

import android.content.Context;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.CalculaCombustivel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by alex on 01/08/14.
 */
public class Calculos extends PreferencesProcessed {
    Context context;
    SaveShowCalculos saveshowcalculos;

    public Calculos(Context ctx) {
        super(ctx);
        this.context = ctx;
    }

    protected double kml_to_mil(double kml) {
        double f = 0.621371192;
        return kml * f;
    }

    protected double mil_to_kml(double mil) {
        double f = 1.609344;
        return mil * f;
    }

    protected double kml_to_kmg(double kml) {
        double f = 3.785411783;
        return kml * f;
    }

    protected double kmg_to_kml(double kmg) {
        double f = 0.264172052;
        return kmg * f;
    }

    protected double kml_to_mpg(double kml) {
        double f = 2.352145833;
        return kml * f;
    }

    protected double mpg_to_kml(double mpg) {
        double f = 0.425143707;
        return mpg * f;
    }

    protected double kml_to_kmgk(double kml) {
        double f = 4.546099294;
        return kml * f;
    }

    protected double kmgk_to_kml(double kmgk) {
        double f = 0.219969;
        return kmgk * f;
    }

    protected double kml_to_mpgk(double kml) {
        double f = 2.824809363;
        return kml * f;
    }

    protected double mpgk_to_kml(double mpgk) {
        double f = 0.35400619;
        return mpgk * f;
    }

    protected double kml_to_l100(double km) {
        double f = 100;
        return f / km;
    }

    protected double l100_to_kml(double l100) {
        double f = 100;
        return f / l100;
    }

    protected double kml_to_l100m(double kml) {
        return (100 / kml_to_mil(kml));
    }

    protected double l100m_to_kml(double l100m) {
        return (100 / mil_to_kml(l100m));
    }

    protected double kml_to_g100k(double kml) {
        return (100 / kml_to_kmg(kml));
    }

    protected double g100k_to_kml(double g100k) {
        return (100 / kmg_to_kml(g100k));
    }

    protected double kml_to_g100m(double kml) {
        return (100 / kml_to_mpg(kml));
    }

    protected double g100m_to_kml(double g100m) {
        return (100 / mpg_to_kml(g100m));
    }

    protected double kml_to_gk100k(double kml) {
        return (100 / kml_to_kmgk(kml));
    }

    protected double gk100k_to_kml(double gk100k) {
        return (100 / kmgk_to_kml(gk100k));
    }

    protected double kml_to_gk100m(double kml) {
        return (100 / kml_to_mpgk(kml));
    }

    protected double gk100m_to_kml(double gk100m) {
        return (100 / mpgk_to_kml(gk100m));
    }

    protected double kmm_to_mim(double kmm) {
        return km_to_mi(kmm);
    }

    protected double mim_to_kmm(double mim) {
        return mi_to_km(mim);
    }

    protected double kmm_to_kmy(double kmm) {
        return mts_to_yard(kmm);
    }

    protected double kmy_to_kmm(double kmy) {
        return yard_to_mts(kmy);
    }

    protected double kmm_to_miy(double kmm) {
        double f = 1.230431774;
        return kmm * f;
    }

    protected double miy_to_kmm(double miy) {
        double f = 0.475072363;
        return miy * f;
    }

    protected double kmm_to_m100k(double kmm) {
        double f = 100;
        return f / kmm;
    }

    protected double m100k_to_kmm(double m100k) {
        double f = 100;
        return f / m100k;
    }

    protected double kmm_to_m100m(double kmm) {
        return (100 / km_to_mi(kmm));
    }

    protected double m100m_to_kmm(double m100m) {
        return (100 / mi_to_km(m100m));
    }

    protected double kmm_to_y100k(double kmm) {
        return (100 / kmm_to_kmy(kmm));
    }

    protected double y100k_to_kmm(double y100k) {
        return (100 / kmy_to_kmm(y100k));
    }

    protected double kmm_to_y100m(double kmm) {
        return (100 / kmm_to_miy(kmm));
    }

    protected double y100m_to_kmm(double y100m) {
        return (100 / miy_to_kmm(y100m));
    }


    protected double km_to_mi(double km) {
        double f = 0.621369949;
        return km * f;
    }

    protected double mi_to_km(double mi) {
        double f = 1.609347219;
        return mi * f;
    }

    protected double lts_to_gls(double lts) {
        double f = 0.264172052;
        return lts * f;
    }

    protected double gls_to_lts(double gals) {
        double f = 3.785411784;
        return gals * f;
    }

    protected double lts_to_glsk(double lts) {
        double f = 0.219969248;
        return lts * f;
    }

    protected double glsk_to_lts(double gals) {
        double f = 4.54609;
        return gals * f;
    }

    protected double mts_to_yard(double mts) {
        double f = 1.307950619;
        return mts * f;
    }

    protected double yard_to_mts(double yard) {
        double f = 0.764554858;
        return yard * f;
    }


    public CalculaCombustivel CalculaCombustivel(double tanque, double consumo_, String price) {
        CalculaCombustivel resultado = new CalculaCombustivel();
        NumberFormat format = new DecimalFormat("@@##");
        NumberFormat format2 = new DecimalFormat("0.00");
        saveshowcalculos = new SaveShowCalculos(MeuCarroApplication.getInstance());


        NumberFormat parseFloat = NumberFormat.getInstance();
        Number ParsedPrice = 0;
        try {
            ParsedPrice = parseFloat.parse(price);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double tqcheio = ParsedPrice.doubleValue() * tanque;
        double autonomia = consumo_ * tanque;
        resultado.tanqueCheioValor = MeuCarroApplication.getInstance().getString(R.string.simbolo_moeda) + " " + format2.format(tqcheio);
        resultado.autonomia = saveshowcalculos.getShowOdometro(autonomia);
        resultado.custoKMRodado = MeuCarroApplication.getInstance().getString(R.string.simbolo_moeda) + " " + format.format(tqcheio / autonomia);
        resultado.custoKM = tqcheio / autonomia;
        resultado.custoAuto = autonomia;
        resultado.tqcheio = tqcheio;
        resultado.vAuto = autonomia;

        return resultado;
    }
}
