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
import com.github.mikephil.charting.components.Legend;

import java.util.ArrayList;

/**
 * Created by alex on 25/07/15.
 */
public class FuelConsuptionLineChart extends BaseFragment {
    private LineChart chart;

    public FuelConsuptionLineChart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        ReportLabelFormatter labelformat = new ReportLabelFormatter(ReportLabelFormatter.types.CONSUMO);
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

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getActivity(), R.color.gasolina));
        colors.add(ContextCompat.getColor(getActivity(), R.color.Etanol));
        colors.add(ContextCompat.getColor(getActivity(), R.color.gnv));
        colors.add(ContextCompat.getColor(getActivity(), R.color.diesel));

        ArrayList<String> names = new ArrayList<>();
        names.add(getString(R.string.gasolina));
        names.add(getString(R.string.Etanol));
        names.add(getString(R.string.gnv));
        names.add(getString(R.string.diesel));


        if (report_ini_date != null && report_end_date != null) {
            chart.getAxisLeft().setValueFormatter(new ReportLabelFormatter(ReportLabelFormatter.types.CONSUMO));
            chart.setData(MeuCarroApplication.getInstance().reportConsumoV2(report_ini_date.getText().toString(), report_end_date.getText().toString()));
            Legend legend = chart.getLegend();
            legend.setExtra(colors, names);
        }
        if (invalid)
            chart.invalidate();
        //chart.animateX(3000);
    }
}