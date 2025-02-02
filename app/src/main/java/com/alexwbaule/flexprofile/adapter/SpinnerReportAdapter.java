package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.ReportContainer;

import java.util.ArrayList;

/**
 * Created by alex on 24/08/15.
 */
public class SpinnerReportAdapter extends ArrayAdapter<ReportContainer> {
    TextView report_name;
    ImageView report_img;
    ArrayList<ReportContainer> myData;
    LayoutInflater inflater;
    Context context;

    public SpinnerReportAdapter(Context context, int resource, ArrayList<ReportContainer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.myData = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View retView = inflater.inflate(R.layout.spinner_report, parent, false);

        ReportContainer var = myData.get(position);

        report_name = retView.findViewById(R.id.spin_report_name);
        report_img = retView.findViewById(R.id.spin_report_icon);
        report_img.setImageResource(var.getImage());
        report_name.setText(var.getName());

        return retView;
    }
}
