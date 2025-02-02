package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
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
 * Created by alex on 26/02/17.
 */

public class FuelPreferenceAdapter extends ArrayAdapter<Combustivel> {
    private TextView fuel_name;
    private ImageView fuel_img;
    private ArrayList<Combustivel> myData;
    private LayoutInflater inflater;
    Context context;

    public FuelPreferenceAdapter(Context context, int resource, ArrayList<Combustivel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.myData = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View retView = inflater.inflate(R.layout.spinner_preference_fuel, parent, false);

        Combustivel var = myData.get(position);

        fuel_name = retView.findViewById(R.id.spin_car_type);
        fuel_img = retView.findViewById(R.id.spin_icon_comb);
        fuel_img.setImageDrawable(var.getImage());
        fuel_name.setText(var.getCombustivel());

        if (!var.isEnabled()) {
            fuel_name.setEnabled(false);
            fuel_img.setEnabled(false);
        }
        return retView;
    }
}
