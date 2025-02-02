package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Categories;
import com.alexwbaule.flexprofile.containers.MaintenanceParts;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by alex on 25/04/16.
 */
public class MaintenancePartsAdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private static final int SECTION_TYPE = 1;
    private MaintenancePartsOnClickListener maintenancePartsOnClickListener;
    private MaintenancePartsOnEmptyState maintenanceOnEmptyState;
    private SparseArray<Categories> keys = new SparseArray<>();
    private SparseArray<MaintenanceParts> values = new SparseArray<>();
    private ArrayList<Categories> maintenanceAllCategories;

    TreeMap<Categories, ArrayList<MaintenanceParts>> dataSet;

    public MaintenancePartsAdapterRecycler(Context ct, TreeMap<Categories, ArrayList<MaintenanceParts>> ll, ArrayList<Categories> aa, MaintenancePartsOnClickListener m, MaintenancePartsOnEmptyState e) {
        context = ct;
        dataSet = ll;
        this.maintenancePartsOnClickListener = m;
        this.maintenanceOnEmptyState = e;
        maintenanceAllCategories = aa;
    }

    public void reloadData(TreeMap<Categories, ArrayList<MaintenanceParts>> newData) {
        dataSet.clear();
        dataSet.putAll(newData);
        if (newData.size() == 0) {
            maintenanceOnEmptyState.onEmptyList();
        }
        notifyDataSetChanged();
    }

    private int prepareData() {
        keys.clear();
        values.clear();
        int total = 0;
        for (Categories d : dataSet.keySet()) {
            keys.append(total, d);
            total++;
            for (MaintenanceParts p : dataSet.get(d)) {
                values.append(total, p);
                total++;
            }
        }
        Log.d(getClass().getSimpleName(), "Total --> " + total);
        return total;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE) {
            final View view = LayoutInflater.from(context).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(view);
        }
        View retView = LayoutInflater.from(context).inflate(R.layout.each_parts, parent, false);
        return new MaintananceViewHolder(retView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SectionViewHolder) {
            final Categories mData = keys.get(position);
            ((SectionViewHolder) holder).title.setText(mData.getCategoria());
            ((SectionViewHolder) holder).image.setBackgroundResource(mData.getImage());
        } else if (holder instanceof MaintananceViewHolder) {
            final MaintenanceParts mData = values.get(position);
            ((MaintananceViewHolder) holder).parts_name.setText(mData.getPartname());
            ((MaintananceViewHolder) holder).parts_qtde.setText(mData.getQtdPrice());
            ((MaintananceViewHolder) holder).parts_price.setText(mData.getTotal());

            ((MaintananceViewHolder) holder).parts_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<MaintenanceParts> mp = dataSet.get(maintenanceAllCategories.get(mData.getCatg_id()));
                    if (mp != null) {
                        mp.remove(mData);
                        if (mp.size() == 0) {
                            dataSet.remove(maintenanceAllCategories.get(mData.getCatg_id()));
                        }
                        maintenancePartsOnClickListener.onClickMaintenanceParts(v, maintenanceAllCategories.get(mData.getCatg_id()), mData);
                        notifyDataSetChanged();
                    }

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return keys.get(position) != null ? SECTION_TYPE : 0;
    }

    @Override
    public int getItemCount() {
        return prepareData();
    }

    public class MaintananceViewHolder extends RecyclerView.ViewHolder {
        TextView parts_name;
        TextView parts_qtde;
        TextView parts_price;
        ImageView parts_delete;

        public MaintananceViewHolder(View itemView) {
            super(itemView);
            parts_name = itemView.findViewById(R.id.parts_name);
            parts_qtde = itemView.findViewById(R.id.parts_qtde);
            parts_price = itemView.findViewById(R.id.parts_value);
            parts_delete = itemView.findViewById(R.id.parts_delete);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public SectionViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.parts_header_name);
            image = view.findViewById(R.id.parts_header_img);
        }
    }


    public interface MaintenancePartsOnClickListener {
        void onClickMaintenanceParts(View view, Categories cat, MaintenanceParts mData);
    }

    public interface MaintenancePartsOnEmptyState {
        void onEmptyList();
    }
}
