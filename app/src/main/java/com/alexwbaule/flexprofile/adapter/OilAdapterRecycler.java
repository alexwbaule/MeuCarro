package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Oils;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderAdapter;

import java.util.List;

/**
 * Created by alex on 30/09/14.
 */
public class OilAdapterRecycler extends RecyclerView.Adapter<OilAdapterRecycler.OilViewHolder> implements StickyHeaderAdapter<OilAdapterRecycler.HeaderHolder> {
    private final Context context;
    private final List<Oils> dataSet;
    private final long defaultId;
    private OilOnClickListener oilOnClickListener;
    private OilOnEmptyState oilOnEmptyState;


    public OilAdapterRecycler(Context ct, List<Oils> ll, long id, OilOnClickListener oilOnClickListener, OilOnEmptyState emptyState) {
        context = ct;
        dataSet = ll;
        defaultId = id;
        this.oilOnClickListener = oilOnClickListener;
        this.oilOnEmptyState = emptyState;
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
        Oils mData = dataSet.get(position);
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

    public void reloadData(List<Oils> newData) {
        if (newData.size() == 0) {
            oilOnEmptyState.onEmptyList();
        }
        dataSet.clear();
        dataSet.addAll(newData);
        notifyDataSetChanged();
    }

    public void remove(Oils pos) {
        dataSet.remove(pos);
    }

    public long getOilid(int pos) {
        Oils mData = dataSet.get(pos);
        return mData.getId();
    }

    @Override
    public OilAdapterRecycler.OilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View retView = LayoutInflater.from(context).inflate(R.layout.each_oil_new, parent, false);
        return new OilViewHolder(retView);
    }

    @Override
    public void onBindViewHolder(final OilViewHolder holder, final int position) {
        final Oils mData = dataSet.get(position);

        Log.d(getClass().getSimpleName(), "Using =" + mData.getId());


        if (oilOnClickListener != null) {
            holder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.setSelected();
                    if (mData.isSelected()) {
                        holder.fab.setImageResource(R.drawable.ic_menu_check);
                    } else {
                        holder.fab.setImageResource(R.drawable.ic_menu_car_oil);
                    }
                    oilOnClickListener.onClickOil(holder.fab, mData);
                }
            });
        }
        holder.oil_price.setText(mData.getTotalPrice());
        holder.oil_price_unit.setText(mData.getPrice());

        holder.oil_time.setText(mData.getCreated_time());
        holder.oil_date.setText(mData.getDay());
        holder.oil_name.setText(mData.getOil_name());
        holder.oil_km.setText(mData.getKm());
        holder.oil_nextkm.setText(mData.getNext_km());
        holder.oil_filter.setImageResource(R.drawable.ic_stat_filter_off);
        holder.fab.setImageResource(R.drawable.ic_menu_car_oil);

        if (mData.isSelected()) {
            holder.fab.setImageResource(R.drawable.ic_menu_check);
        }

        if (mData.isHas_filter()) {
            holder.oil_filter.setImageResource(R.drawable.ic_stat_filter_on);
            holder.fab.setImageResource(R.drawable.ic_menu_car_oil);
        }
        holder.oil_lts.setText(mData.getLitros());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class OilViewHolder extends RecyclerView.ViewHolder {
        TextView oil_name;
        TextView oil_km;
        TextView oil_nextkm;
        ImageView oil_filter;
        TextView oil_lts;
        TextView oil_date;
        TextView oil_time;
        TextView oil_price;
        TextView oil_price_unit;
        FloatingActionButton fab;


        public OilViewHolder(View view) {
            super(view);
            oil_date = view.findViewById(R.id.oil_date);
            oil_time = view.findViewById(R.id.oil_time);
            oil_name = view.findViewById(R.id.pricetotalkm);
            oil_price = view.findViewById(R.id.oil_price);
            oil_price_unit = view.findViewById(R.id.oil_price_unit);
            oil_km = view.findViewById(R.id.oil_km);
            oil_nextkm = view.findViewById(R.id.oil_nextkm);
            oil_filter = view.findViewById(R.id.pricebykm);
            oil_lts = view.findViewById(R.id.pricebyday);
            fab = view.findViewById(R.id.oil_type);
        }
    }

    public interface OilOnClickListener {
        void onClickOil(View view, Oils mData);
    }

    public interface OilOnEmptyState {
        void onEmptyList();
    }
}
