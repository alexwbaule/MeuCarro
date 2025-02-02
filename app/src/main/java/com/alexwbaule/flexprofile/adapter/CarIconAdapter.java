package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.CarIcon;

import java.util.ArrayList;

/**
 * Created by alex on 03/08/14.
 */
public class CarIconAdapter extends ArrayAdapter<CarIcon> {
    private TextView fuel_name;
    private ImageView fuel_img;
    private ArrayList<CarIcon> myData;
    private LayoutInflater inflater;

    public CarIconAdapter(Context ctx, int textViewResourceId, ArrayList<CarIcon> objects) {
        super(ctx, textViewResourceId, objects);
        myData = objects;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View retView = inflater.inflate(R.layout.spinner_combustivel_add, parent, false);

        CarIcon var = myData.get(position);

        fuel_name = retView.findViewById(R.id.spin_car_type);
        fuel_img = retView.findViewById(R.id.spin_car_icon);
        fuel_img.setImageResource(var.getImageId());
        fuel_name.setText(var.getName());

        return retView;
    }

    @Override
    public int getCount() {
        return myData.size();
    }

    @Override
    public int getPosition(CarIcon item) {
        return myData.indexOf(item);
    }

    //Precisa DISSO para o Remove Funcionar.
    @Override
    public CarIcon getItem(int position) {
        return myData.get(position);
    }
}
