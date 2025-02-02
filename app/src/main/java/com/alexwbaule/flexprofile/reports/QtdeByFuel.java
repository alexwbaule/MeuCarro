package com.alexwbaule.flexprofile.reports;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.fragments.BaseFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;


/**
 * Created by alex on 20/08/15.
 */


public class QtdeByFuel extends BaseFragment {
    private PieChart chart;

    public QtdeByFuel() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        chart = rootView.findViewById(R.id.piechart);
        chart.setUsePercentValues(true);
        chart.setDescription(null);
        chart.setRotationEnabled(false);
        chart.setNoDataText(getString(R.string.nao_ha_graficos));

        // radius of the center hole in percent of maximum radius
        chart.setHoleRadius(55f);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleRadius(60f);
        chart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        chart.getLegend().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));

        createGraph(rootView, false);
        Log.d(getClass().getSimpleName(), "Calling onCreateView");
        return rootView;
    }

    public void update() {
        Log.d(getClass().getSimpleName(), "Calling Update");
        createGraph(getView(), true);
    }

    protected void createGraph(View view, boolean invalid) {
        TextInputEditText report_ini_date = getActivity().findViewById(R.id.report_ini_date);
        TextInputEditText report_end_date = getActivity().findViewById(R.id.report_end_date);
        if (report_ini_date != null && report_end_date != null) {
            chart.setData(MeuCarroApplication.getInstance().reportQtdeByFuel(report_ini_date.getText().toString(), report_end_date.getText().toString()));
        }
        if (invalid)
            chart.invalidate();
        //chart.animateX(1500);
    }
}
