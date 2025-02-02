package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Fuel;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderAdapter;

import java.util.List;

/**
 * Created by alex on 21/07/14.
 */
public class FuelAdapterRecycler extends RecyclerView.Adapter<FuelAdapterRecycler.RefuelViewHolder> implements StickyHeaderAdapter<FuelAdapterRecycler.HeaderHolder> {
    private final Context context;
    private final List<Fuel> dataSet;
    private final long defaultId;
    private RefuelOnClickListener refuelOnClickListener;
    private RefuelOnEmptyState refuelOnEmptyState;

    public FuelAdapterRecycler(Context ct, List<Fuel> ll, long id, RefuelOnClickListener refuelOnClickListener, RefuelOnEmptyState emptyState) {
        context = ct;
        dataSet = ll;
        defaultId = id;
        this.refuelOnClickListener = refuelOnClickListener;
        this.refuelOnEmptyState = emptyState;
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
        Fuel mData = dataSet.get(position);
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
    public RefuelViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View retView = LayoutInflater.from(context).inflate(R.layout.each_fuel_new, viewGroup, false);
        RefuelViewHolder holder = new RefuelViewHolder(retView);
        return holder;
    }

    public void reloadData(List<Fuel> newData) {
        if (newData.size() == 0) {
            refuelOnEmptyState.onEmptyList();
        }
        dataSet.clear();
        dataSet.addAll(newData);
        notifyDataSetChanged();
    }

    public void remove(Fuel pos) {
        dataSet.remove(pos);
    }

    public long getCarId(int pos) {
        Fuel mData = dataSet.get(pos);
        return mData.getId();
    }

    @Override
    public void onBindViewHolder(final RefuelViewHolder refuelViewHolder, final int i) {
        final Fuel mData = dataSet.get(i);

        if (refuelOnClickListener != null) {
            refuelViewHolder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.setSelected();
                    if (mData.isSelected()) {
                        refuelViewHolder.fab.setImageResource(R.drawable.ic_menu_check);
                    } else {
                        refuelViewHolder.fab.setImageResource(setCombIcon(mData));
                    }
                    refuelOnClickListener.onClickRefuel(refuelViewHolder.fab, mData);
                }
            });
        }
        refuelViewHolder.fab.setImageResource(setCombIcon(mData));
        refuelViewHolder.fab.setBackgroundTintList(ColorStateList.valueOf(setIconBgColor(mData)));

        refuelViewHolder.fuel_kml.setTextColor(ContextCompat.getColor(context, R.color.secondary_text));
        refuelViewHolder.fuel_diff_km.setTextColor(ContextCompat.getColor(context, R.color.secondary_text));

        if (mData.isFake()) {
            refuelViewHolder.fuel_kml.setTextColor(ContextCompat.getColor(context, R.color.red));
            refuelViewHolder.fuel_diff_km.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        if (mData.isSelected()) {
            refuelViewHolder.fab.setImageResource(R.drawable.ic_menu_check);
        }

        refuelViewHolder.fuel_type.setTextColor(ContextCompat.getColor(context, R.color.secondary_text));
        refuelViewHolder.fuel_type.setText("Completo");
        refuelViewHolder.fuel_proportion.setVisibility(View.GONE);
        refuelViewHolder.fuel_proportion_label.setVisibility(View.GONE);


        if (mData.isPartial()) {
            refuelViewHolder.fuel_type.setText("Parcial");
            refuelViewHolder.fuel_type.setTextColor(ContextCompat.getColor(context, R.color.warning));
            refuelViewHolder.fuel_proportion.setVisibility(View.VISIBLE);
            refuelViewHolder.fuel_proportion_label.setVisibility(View.VISIBLE);
            refuelViewHolder.fuel_proportion.setText(mData.getProportion());
        }
        refuelViewHolder.fuel_qtde_used.setText(mData.getRealLitros());

        refuelViewHolder.fuel_price_unit.setText(mData.getPrice());
        refuelViewHolder.fuel_price_tot.setText(mData.getTotalPrice());
        refuelViewHolder.fuel_time.setText(mData.getCreated_time());
        refuelViewHolder.fuel_date.setText(mData.getDay());
        refuelViewHolder.fuel_qtde.setText(mData.getLitros());
        refuelViewHolder.fuel_kml.setText(mData.getKml());
        refuelViewHolder.fuel_diff_km.setText(mData.getDiff_km());
        refuelViewHolder.fuel_km.setText(mData.getKm());
        refuelViewHolder.fuel_comb_name.setText(mData.getCombustivel());
        refuelViewHolder.fuel_comb_name.setTextColor(setBgColor(mData));

        refuelViewHolder.fuel_place_name.setText(mData.getPlace_name());
        refuelViewHolder.fuel_place_addr.setText(mData.getPlace_addr());

        if (mData.getDiff_km() == null) {
            refuelViewHolder.fuel_diff_km.setText("--");
        }
        if (mData.getKml() == null) {
            refuelViewHolder.fuel_kml.setText("--");
        }
    }

    private int setCombIcon(Fuel mData) {
        if (mData.getComb_id() == 1) {
            return R.drawable.ic_stat_gasolina;
        } else if (mData.getComb_id() == 2) {
            return R.drawable.ic_stat_alcool;
        } else if (mData.getComb_id() == 3) {
            return R.drawable.ic_stat_gnv;
        } else if (mData.getComb_id() == 4) {
            return R.drawable.ic_stat_diesel;
        }
        return 0;
    }

    private int setIconBgColor(Fuel mData) {
        if (mData.getComb_id() == 1) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.mini_fab_gas);
        } else if (mData.getComb_id() == 2) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.mini_fab_alc);
        } else if (mData.getComb_id() == 3) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.mini_fab_gnv);
        } else if (mData.getComb_id() == 4) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.mini_fab_diesel);
        }
        return 0;
    }

    private int setBgColor(Fuel mData) {
        if (mData.getComb_id() == 1) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.gasolina);
        } else if (mData.getComb_id() == 2) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.Etanol);
        } else if (mData.getComb_id() == 3) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.gnv);
        } else if (mData.getComb_id() == 4) {
            return ContextCompat.getColor(MeuCarroApplication.getInstance(), R.color.diesel);
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class RefuelViewHolder extends RecyclerView.ViewHolder {
        TextView fuel_date;
        TextView fuel_time;
        TextView fuel_qtde;
        TextView fuel_km;
        TextView fuel_diff_km;
        TextView fuel_kml;
        TextView fuel_comb_name;
        TextView fuel_price_unit;
        TextView fuel_price_tot;
        TextView fuel_proportion;
        TextView fuel_proportion_label;
        TextView fuel_type;
        TextView fuel_qtde_used;
        TextView fuel_qtde_used_label;
        TextView fuel_place_name;
        TextView fuel_place_addr;

        FloatingActionButton fab;


        public RefuelViewHolder(View view) {
            super(view);
            fuel_date = view.findViewById(R.id.fuel_date);
            fuel_time = view.findViewById(R.id.fuel_time);
            fuel_qtde = view.findViewById(R.id.fuel_qtde);
            fuel_km = view.findViewById(R.id.fuel_km);
            fuel_kml = view.findViewById(R.id.fuel_kml);
            fuel_diff_km = view.findViewById(R.id.fuel_km_road);
            fuel_comb_name = view.findViewById(R.id.fuel_comb_name);
            fuel_price_unit = view.findViewById(R.id.fuel_price_unit);
            fuel_price_tot = view.findViewById(R.id.fuel_price_tot);
            fuel_proportion = view.findViewById(R.id.fuel_proportion);
            fuel_proportion_label = view.findViewById(R.id.fuel_proportion_label);
            fuel_type = view.findViewById(R.id.fuel_type);
            fuel_qtde_used = view.findViewById(R.id.fuel_qtde_used);
            fuel_qtde_used_label = view.findViewById(R.id.fuel_used_label);
            fuel_place_name = view.findViewById(R.id.fuel_local_name);
            fuel_place_addr = view.findViewById(R.id.fuel_local_addr);

            fab = view.findViewById(R.id.comb_type);
        }
    }

    public interface RefuelOnClickListener {
        void onClickRefuel(View view, Fuel mData);
    }

    public interface RefuelOnEmptyState {
        void onEmptyList();
    }
}

