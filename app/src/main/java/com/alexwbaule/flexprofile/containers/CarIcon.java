package com.alexwbaule.flexprofile.containers;

/**
 * Created by alex on 16/07/15.
 */
public class CarIcon {
    private String name;
    private String image_name;
    private int image;
    private int id;

    public CarIcon(String s, String f, int h, int i) {
        this.name = s;
        this.image_name = f;
        this.image = h;
        this.id = i;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image_name;
    }

    public int getImageId() {
        return image;
    }

    public int getId() {
        return id;
    }

}
