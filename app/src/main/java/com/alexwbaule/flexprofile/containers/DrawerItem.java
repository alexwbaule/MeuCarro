package com.alexwbaule.flexprofile.containers;

/**
 * Created by alex on 24/07/14.
 */
public class DrawerItem {
    String icon;
    String name;
    long id;

    public DrawerItem(String icon, String name, long id) {
        this.icon = icon;
        this.name = name;
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
