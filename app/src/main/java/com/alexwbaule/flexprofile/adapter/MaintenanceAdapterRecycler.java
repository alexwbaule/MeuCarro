package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Categories;
import com.alexwbaule.flexprofile.containers.MaintenanceParts;
import com.alexwbaule.flexprofile.containers.Maintenances;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by alex on 21/07/14.
 */
public class MaintenanceAdapterRecycler extends RecyclerView.Adapter<MaintenanceAdapterRecycler.MaintenanceViewHolder> implements StickyHeaderAdapter<MaintenanceAdapterRecycler.HeaderHolder> {
    private final Context context;
    private final ArrayList<Maintenances> dataSet;
    private final long defaultId;
    private MaintenanceOnClickListener maintananceOnClickListener;
    private MaintenanceOnEmptyState maintananceOnEmptyState;
    TreeMap<Categories, ArrayList<MaintenanceParts>> dataMap;


    public MaintenanceAdapterRecycler(Context ct, ArrayList<Maintenances> ll, long id, MaintenanceOnClickListener maintenanceOnClickListener, MaintenanceOnEmptyState emptyState) {
        context = ct;
        dataSet = ll;
        defaultId = id;
        this.maintananceOnClickListener = maintenanceOnClickListener;
        this.maintananceOnEmptyState = emptyState;
    }

    @Override
    public long getHeaderId(int position) {
        return dataSet.get(position).getHeader();
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.month_and_year, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        Maintenances mData = dataSet.get(position);
        viewholder.month_txt.setText(mData.getMonth());
        viewholder.year_txt.setText(mData.getYear());
    }


    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView month_txt;
        public TextView year_txt;

        public HeaderHolder(View view) {
            super(view);
            month_txt = view.findViewById(R.id.month_txt);
            year_txt = view.findViewById(R.id.year_txt);
        }
    }


    @Override
    public MaintenanceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View retView = LayoutInflater.from(context).inflate(R.layout.each_maintenance, viewGroup, false);
        MaintenanceViewHolder holder = new MaintenanceViewHolder(retView);
        return holder;
    }

    public void reloadData(List<Maintenances> newData) {
        if (newData.size() == 0) {
            maintananceOnEmptyState.onEmptyList();
        }
        dataSet.clear();
        dataSet.addAll(newData);
        notifyDataSetChanged();
    }

    public void remove(Maintenances pos) {
        dataSet.remove(pos);
    }

    public long getCarId(int pos) {
        Maintenances mData = dataSet.get(pos);
        return mData.getId();
    }

    @Override
    public void onBindViewHolder(final MaintenanceViewHolder remaintenanceViewHolder, final int i) {
        final Maintenances mData = dataSet.get(i);
        remaintenanceViewHolder.maintenance_time.setText(mData.getCreated_time());
        remaintenanceViewHolder.maintenance_date.setText(mData.getDay());
        remaintenanceViewHolder.maintenance_name.setText(mData.getMaintenance_local());
        remaintenanceViewHolder.maintenance_km.setText(mData.getKm());
        remaintenanceViewHolder.maintenance_total.setText(mData.getTotal());
        remaintenanceViewHolder.maintenance_qtde.setText(String.valueOf(mData.getCountParts()));

        dataMap = mData.getParts();

        Log.d(getClass().getSimpleName(), "dataMap Total --> " + dataMap.size());


        remaintenanceViewHolder.maintenance_layout_parts.removeAllViews();
        for (Categories cat : dataMap.keySet()) {
            View newView = LayoutInflater.from(context).inflate(R.layout.section_list, remaintenanceViewHolder.maintenance_layout_parts, false);
            remaintenanceViewHolder.maintenance_layout_parts.addView(newView);
            TextView title = newView.findViewById(R.id.list_parts_header_name);
            ImageView image = newView.findViewById(R.id.list_parts_header_img);
            title.setText(cat.getCategoria());
            image.setBackgroundResource(cat.getImage());
            Log.d(getClass().getSimpleName(), "dataMap.get(" + cat.getCategoria() + ") Total --> " + dataMap.get(cat).size());

            for (MaintenanceParts part : dataMap.get(cat)) {
                Log.d(getClass().getSimpleName(), "MaintenanceParts Total --> " + part.getPartname());

                View partview = LayoutInflater.from(context).inflate(R.layout.each_parts_for_list, remaintenanceViewHolder.maintenance_layout_parts, false);
                remaintenanceViewHolder.maintenance_layout_parts.addView(partview);
                TextView list_parts_name = partview.findViewById(R.id.list_parts_name);
                TextView list_parts_qtde = partview.findViewById(R.id.list_parts_qtde);
                TextView list_parts_price = partview.findViewById(R.id.list_parts_value);
                list_parts_name.setText(part.getPartname());
                list_parts_qtde.setText(part.getQtdPrice());
                list_parts_price.setText(part.getTotal());
            }
        }

        if (maintananceOnClickListener != null) {
            remaintenanceViewHolder.fab_maintenance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.setSelected();
                    if (mData.isSelected()) {
                        remaintenanceViewHolder.fab_maintenance.setImageResource(R.drawable.ic_menu_check);
                    } else {
                        remaintenanceViewHolder.fab_maintenance.setImageResource(R.drawable.ic_menu_maintenance);
                    }
                    maintananceOnClickListener.onClickMaintenance(remaintenanceViewHolder.fab_maintenance, mData);
                }
            });
        }
        remaintenanceViewHolder.fab_maintenance.setImageResource(R.drawable.ic_menu_maintenance);

        if (mData.isSelected()) {
            remaintenanceViewHolder.fab_maintenance.setImageResource(R.drawable.ic_menu_check);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(getClass().getSimpleName(), "Total --> " + dataSet.size());
        return dataSet.size();
    }

    public class MaintenanceViewHolder extends RecyclerView.ViewHolder {
        TextView maintenance_date;
        TextView maintenance_time;
        TextView maintenance_km;
        TextView maintenance_name;
        TextView maintenance_total;
        TextView maintenance_qtde;
        FloatingActionButton fab_maintenance;
        LinearLayout maintenance_layout_parts;
        View view;

        public MaintenanceViewHolder(View view) {
            super(view);
            this.view = view;
            maintenance_date = view.findViewById(R.id.maintenance_date);
            maintenance_time = view.findViewById(R.id.maintenance_time);
            maintenance_km = view.findViewById(R.id.maintenance_km);
            maintenance_name = view.findViewById(R.id.maintenance_name);
            maintenance_total = view.findViewById(R.id.maintenance_total);
            maintenance_qtde = view.findViewById(R.id.maintenance_qtde);
            fab_maintenance = view.findViewById(R.id.fab_maintenance);
            maintenance_layout_parts = view.findViewById(R.id.maintenance_layout_parts);
        }
    }

    public interface MaintenanceOnClickListener {
        void onClickMaintenance(View view, Maintenances mData);
    }

    public interface MaintenanceOnEmptyState {
        void onEmptyList();
    }
}

