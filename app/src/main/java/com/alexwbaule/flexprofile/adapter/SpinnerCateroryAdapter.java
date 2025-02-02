package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Categories;

import java.util.ArrayList;

/**
 * Created by alex on 24/04/16.
 */
public class SpinnerCateroryAdapter extends ArrayAdapter<Categories> {
    TextView fuel_name;
    ImageView fuel_img;
    ArrayList<Categories> myData;
    LayoutInflater inflater;
    Context context;

    public SpinnerCateroryAdapter(Context ctx, int textViewResourceId, ArrayList<Categories> objects) {
        super(ctx, textViewResourceId, objects);
        context = ctx;
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

        Categories var = myData.get(position);

        fuel_name = retView.findViewById(R.id.spin_car_type);
        fuel_img = retView.findViewById(R.id.spin_car_icon);
        fuel_img.setImageResource(var.getImage());
        fuel_name.setText(var.getCategoria());

        return retView;
    }
}
