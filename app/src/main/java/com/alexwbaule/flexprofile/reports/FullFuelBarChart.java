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
import com.alexwbaule.flexprofile.containers.FuelReportBar;
import com.alexwbaule.flexprofile.fragments.BaseFragment;
import com.alexwbaule.flexprofile.utils.ReportLabelFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;

/**
 * Created by alex on 20/08/15.
 */

public class FullFuelBarChart extends BaseFragment {
    private BarChart chart;

    public FullFuelBarChart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);

        chart = rootView.findViewById(R.id.barchart);
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDescription(null);
        chart.setNoDataText(getString(R.string.nao_ha_graficos));

        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        l.setYOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        YAxis leftAxis = chart.getAxisLeft();
        chart.getAxisRight().setDrawLabels(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(30f);

        chart.setBorderWidth(30f);

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

        chart.getAxisRight().setEnabled(false);
        if (report_ini_date != null && report_end_date != null) {
            chart.getAxisLeft().setValueFormatter(new ReportLabelFormatter(ReportLabelFormatter.types.DISTANCE));
            FuelReportBar data = MeuCarroApplication.getInstance().reportFuelInterval(report_ini_date.getText().toString(), report_end_date.getText().toString());
            chart.setData(data.getData());
            chart.getAxisLeft().removeAllLimitLines();

            if (data.getMedia() > 0) {
                LimitLine ll = new LimitLine(data.getMedia(), getString(R.string.distancia_media, data.getMediaKM()));
                ll.setLineColor(getResources().getColor(R.color.gasolina_bg));
                ll.setLineWidth(3f);
                ll.setTextColor(Color.LTGRAY);
                ll.setTextSize(6f);
                chart.getAxisLeft().addLimitLine(ll);
            }
        }
        if (invalid)
            chart.invalidate();
        //chart.animateY(1500);
    }
}
