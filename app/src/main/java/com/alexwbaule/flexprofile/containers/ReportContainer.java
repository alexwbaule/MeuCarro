package com.alexwbaule.flexprofile.containers;

/**
 * Created by alex on 24/08/15.
 */
public class ReportContainer {
    private String report_name;
    private int image;

    public ReportContainer(String s, int f) {
        this.report_name = s;
        this.image = f;
    }

    public String getName() {
        return report_name;
    }

    public int getImage() {
        return image;
    }
}
