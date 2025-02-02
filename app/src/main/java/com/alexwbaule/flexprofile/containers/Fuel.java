package com.alexwbaule.flexprofile.containers;

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
public class Fuel implements Serializable {
    private String created_date;
    private String created_time;
    private String month;
    private String year;
    private String day;
    private String place_name;
    private String place_addr;
    private int comb_id;
    private String combustivel;
    private double km;
    private double litros;
    private double real_litros;
    private double diff_km;
    private double kml;
    private double price;
    private long id;
    private long header;
    private double litros_partial_complete = 0.000;

    private double tanque_comb;
    private double tanque_gnv;
    private int proportion;
    private boolean isPartial;


    private boolean selected;
    private boolean fake = false;
    SaveShowCalculos calcconsumo;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();


    public Fuel(String created_date, String created_time, int comb_id, String combustivel, double km, double litros,
                double real_litros, double price, double diff_km, double kml, long id, boolean fk, double tanque_comb,
                double tanque_gnv, int proportion, boolean isPartial, long headerid, String fyear, String fmonth, String fday, String pname, String paddr) {
        this.created_date = created_date;
        this.created_time = created_time;
        this.comb_id = comb_id;
        this.combustivel = combustivel;
        this.km = km;
        this.litros = litros;
        this.real_litros = real_litros;
        this.price = price;
        this.diff_km = diff_km;
        this.kml = kml;
        this.id = id;
        this.fake = fk;
        this.tanque_comb = tanque_comb;
        this.tanque_gnv = tanque_gnv;
        this.proportion = proportion;
        this.isPartial = isPartial;
        this.header = headerid;
        this.year = fyear;
        this.month = fmonth;
        this.day = fday;
        this.place_name = pname;
        this.place_addr = paddr;
        calcconsumo = new SaveShowCalculos(rootapp);
    }

    public String toString() {
        return "[ID = " + this.getId() +
                " DATE = " + this.getCreated_date() +
                " TIME = " + this.getCreated_time() +
                " COMBID = " + this.getComb_id() +
                " COMBNAME = " + this.getCombustivel() +
                " KM = " + this.getKm() +
                " VOLUME = " + this.getLitros() +
                " ISPARTIAL = " + this.isPartial() +
                " PROPORTION = " + this.getProportion() +
                " LITROS_COMPLETE = " + this.getLitrosToComplete() +
                " COMPLETE + REAL = " + (this.getLitrosToComplete() + this.getvRealLitros()) +
                " REAL_VOLUME = " + this.getvRealLitros() +
                " PRICE = " + this.getPrice() +
                " DIFF_KM = " + this.getDiff_km() +
                " KML = " + this.getKml() +
                " PRICE_FULL = " + (price * litros) +
                " ISFAKE = " + this.isFake() +
                " HEADER = " + this.getHeader() +
                " YEAR = " + this.getYear() +
                " MONTH = " + this.getMonth() +
                " DAY = " + this.getDay() +
                "]\n";
    }

    public String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, (Integer.valueOf(month) - 1));
        return StringUtils.capitalize(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getPlace_addr() {
        return place_addr;
    }

    public String getYear() {
        return this.year;
    }

    public String getDay() {
        return this.day;
    }

    public String getDiff_km() {
        if (fake) {
            return "* " + calcconsumo.getShowOdometro(diff_km);
        } else {
            return calcconsumo.getShowOdometro(diff_km);
        }
    }

    public double getTanque() {
        if (comb_id == 3) {
            return tanque_gnv;
        }
        return tanque_comb;
    }

    public long getHeader() {
        return header;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public double getTanque_gnv() {
        return tanque_gnv;
    }

    public double getTanque_comb() {
        return tanque_comb;
    }

    public int getvProportion() {
        return proportion;
    }


    public String getProportion() {
        return rootapp.getString(R.string.percent, proportion, rootapp.getString(R.string.percent_sign));
    }

    public double getLitrosToComplete() {
        if (isPartial) {
            litros_partial_complete = ((tanque_comb * (100 - proportion)) / 100);
            if (comb_id == 3) {
                litros_partial_complete = ((tanque_gnv * (100 - proportion)) / 100);
            }
        }
        return litros_partial_complete;
    }

    public String getRealLitros() {
        if (comb_id == 3) {
            return calcconsumo.getShowVolumeGNV(real_litros);
        }
        return calcconsumo.getShowVolumeTanque(real_litros);
		/*
		if(comb_id == 3 ){
			return calcconsumo.getShowVolumeGNV(litros + getLitrosToComplete());
		}
		return calcconsumo.getShowVolumeTanque(litros + getLitrosToComplete());
		*/
    }

    public String getTotalPrice() {
        NumberFormat format2 = new DecimalFormat("0.00");
        double v = price * litros;
        return rootapp.getString(R.string.simbolo_moeda) + " " + format2.format(v);
    }

    public String getPrice() {
        NumberFormat format2 = new DecimalFormat("0.000");
        return rootapp.getString(R.string.simbolo_moeda) + " " + format2.format(price);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        this.selected = !selected;
    }

    public String getCreated_date() {
        return created_date;
    }

    public int getComb_id() {
        return comb_id;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public String getKm() {
        return calcconsumo.getShowOdometro(km);
    }

    public String getLitros() {
        if (comb_id == 3) {
            return calcconsumo.getShowVolumeGNV(litros);
        }
        return calcconsumo.getShowVolumeTanque(litros);
    }

    public double getvKml() {
        return kml;
    }

    public double getvKm() {
        return km;
    }

    public double getvRealLitros() {
        return real_litros;
    }

    public double getvPrice() {
        return price;
    }

    public double getvTotalPrice() {
        double v = price * litros;
        return v;
    }

    public double getvPriceByKm() {
        if (diff_km == 0)
            return 0.000;
        return ((price * litros) / diff_km);
    }

    public double getvLitros() {
        return litros;
    }

    public double getvDiff_km() {
        return diff_km;
    }

    public boolean isFake() {
        return fake;
    }


    public String getKml() {
        if (comb_id == 3) {
            if (fake) {
                return "* " + calcconsumo.getShowConsumoGNV(kml);
            } else {
                return calcconsumo.getShowConsumoGNV(kml);
            }
        }
        if (fake) {
            return "* " + calcconsumo.getShowConsumo(kml);
        } else {
            return calcconsumo.getShowConsumo(kml);
        }
    }

    public long getId() {
        return id;
    }

    public String getCreated_time() {
        return created_time;
    }


}
