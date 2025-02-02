package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Combustivel;

import java.util.ArrayList;

/**
 * Created by alex on 03/08/14.
 */
public class SpinnerCombustivelAdapter extends ArrayAdapter<Combustivel> {
    TextView fuel_name;
    ImageView fuel_img;
    ArrayList<Combustivel> myData;
    LayoutInflater inflater;
    Context context;

    public SpinnerCombustivelAdapter(Context ctx, int textViewResourceId, ArrayList<Combustivel> objects) {
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

        Combustivel var = myData.get(position);

        fuel_name = retView.findViewById(R.id.spin_car_type);
        fuel_img = retView.findViewById(R.id.spin_car_icon);
        fuel_img.setImageDrawable(var.getImage());
        fuel_name.setText(var.getCombustivel());

        if (!var.isEnabled()) {
            fuel_name.setEnabled(false);
            fuel_img.setEnabled(false);
        }
        return retView;
    }

    @Override
    public boolean isEnabled(int position) {
        return myData.get(position).isEnabled();
    }

    @Override
    public int getCount() {
        return myData.size();
    }

    @Override
    public int getPosition(Combustivel item) {
        return myData.indexOf(item);
    }

    public int getPosition(int CombId) {
        for (Combustivel dt : myData) {
            if (dt.getId() == CombId) {
                return dt.getPos();
            }
        }
        return 0;
    }

    //Precisa DISSO para o Remove Funcionar.
    @Override
    public Combustivel getItem(int position) {
        return myData.get(position);
    }

    public void add(int position) {
        myData.get(position).setEnabled(true);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        myData.get(position).setEnabled(false);
        notifyDataSetChanged();
    }
}
