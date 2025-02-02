package com.alexwbaule.flexprofile.reports;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.PriceAndKm;
import com.alexwbaule.flexprofile.fragments.BaseFragment;


public class Costs extends BaseFragment {

    public Costs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_costs_chart, container, false);

        createGraph(rootView, false);
        Log.d(getClass().getSimpleName(), "Calling onCreateView");
        return rootView;
    }

    public void update() {
        Log.d(getClass().getSimpleName(), "Calling Update");
        createGraph(getView(), true);
    }

    protected void createGraph(View view, boolean invalid) {
        TextView price_total = view.findViewById(R.id.pricetotalkm);
        TextView price_day = view.findViewById(R.id.pricebyday);
        TextView price_km = view.findViewById(R.id.pricebykm);
        TextView cost_volume = view.findViewById(R.id.cost_volume);
        TextView cost_mediageral = view.findViewById(R.id.cost_mediageral);
        TextView cost_totalkm = view.findViewById(R.id.cost_totalkm);



        PriceAndKm priceAndKmFromCar = MeuCarroApplication.getInstance().getAllPriceAndKmFromCar();
        price_total.setText(priceAndKmFromCar.getPrice());
        price_day.setText(priceAndKmFromCar.getDays());
        price_km.setText(priceAndKmFromCar.getKm());

        cost_volume.setText(priceAndKmFromCar.getLitros());
        cost_mediageral.setText(priceAndKmFromCar.getMediakm());
        cost_totalkm.setText(priceAndKmFromCar.getTotalkm());
    }
}
