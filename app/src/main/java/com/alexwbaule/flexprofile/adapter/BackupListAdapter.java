package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by alex on 29/04/16.
 */
public class BackupListAdapter extends ArrayAdapter<File> {
    TextView bkp_name;
    TextView bkp_date;
    TextView bkp_size;

    ArrayList<File> myData;
    LayoutInflater inflater;
    Context context;

    public BackupListAdapter(Context context, int resource, ArrayList<File> objects) {
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
        View retView = inflater.inflate(R.layout.backup_list, parent, false);
        File var = myData.get(position);

        //if ((position % 2) == 0){
        //    retView.setBackgroundColor(ActivityCompat.getColor(getContext(), R.color.diesel_bg));
        //}

        bkp_name = retView.findViewById(R.id.bkp_name);
        bkp_date = retView.findViewById(R.id.bkp_date);
        bkp_size = retView.findViewById(R.id.bkp_size);
        bkp_name.setText(var.getName());
        bkp_date.setText(MeuCarroApplication.setDateToShow(var.lastModified()));
        bkp_size.setText(MeuCarroApplication.setSizeInKbToShow(var.length()));

        return retView;
    }
}
