package com.alexwbaule.flexprofile.reports;

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
import com.alexwbaule.flexprofile.utils.ReportLabelFormatter;
import com.github.mikephil.charting.charts.LineChart;

/**
 * Created by alex on 23/08/15.
 */
public class FuelPriceLineChart extends BaseFragment {
    LineChart chart;

    public FuelPriceLineChart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        ReportLabelFormatter labelformat = new ReportLabelFormatter(ReportLabelFormatter.types.NORMAL);
        chart = rootView.findViewById(R.id.linechart);
        chart.setNoDataText(getString(R.string.nao_ha_graficos));
        chart.setDescription(null);
        chart.getAxisRight().setValueFormatter(labelformat);
        chart.getAxisLeft().setValueFormatter(labelformat);

        chart.getLegend().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        chart.getXAxis().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
        chart.getAxisRight().setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));

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
            chart.setData(MeuCarroApplication.getInstance().reportFuelPrice(report_ini_date.getText().toString(), report_end_date.getText().toString()));
        }
        if (invalid)
            chart.invalidate();
        //chart.animateX(1500);
    }
}