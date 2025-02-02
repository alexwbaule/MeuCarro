package com.alexwbaule.flexprofile.containers;

import android.graphics.drawable.Drawable;

/**
 * Created by alex on 03/08/14.
 */
public class Combustivel {
    private String combustivel;
    private Drawable icon;
    private int image;
    private int id;
    private int pos;
    private boolean isEnabled;

    public Combustivel(String s, Drawable f, int imd, int i, int pos) {
        this.combustivel = s;
        this.icon = f;
        this.image = imd;
        this.id = i;
        this.pos = pos;
        this.isEnabled = true;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public Drawable getImage() {
        return icon;
    }

    public int getIntImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public int getPos() {
        return pos;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean t) {
        isEnabled = t;
    }
}
