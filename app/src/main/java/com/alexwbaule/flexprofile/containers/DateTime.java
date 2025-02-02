package com.alexwbaule.flexprofile.containers;

/**
 * Created by alex on 09/05/16.
 */
public class DateTime {
    String date;
    String time;

    public DateTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public DateTime(String[] date) {
        this.date = date[0];
        this.time = date[1];
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
