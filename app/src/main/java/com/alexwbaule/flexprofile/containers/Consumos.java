package com.alexwbaule.flexprofile.containers;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by alex on 01/08/14.
 */
public class Consumos {
    public int combustivel;
    public double consumo;
    Number numv;
    NumberFormat parser;

    public Consumos(int comb, String cons) {
        super();
        parser = NumberFormat.getInstance();
        this.combustivel = comb;
        try {
            numv = parser.parse(cons);
        } catch (ParseException e) {
            numv = 0.000f;
        }
        this.consumo = numv.doubleValue();
    }
}
