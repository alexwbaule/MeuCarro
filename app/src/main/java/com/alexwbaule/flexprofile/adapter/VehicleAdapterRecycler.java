package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.Vehicles;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderAdapter;

import java.util.List;


/**
 * Created by alex on 16/07/14.
 */
public class VehicleAdapterRecycler extends RecyclerView.Adapter<VehicleAdapterRecycler.CarViewHolder> implements StickyHeaderAdapter<VehicleAdapterRecycler.HeaderHolder> {
    private final Context context;
    private final List<Vehicles> dataSet;
    private long defaultId;
    private VehicleOnClickListener vehicleOnClickListener;
    private VehicleOnEmptyState vehicleOnEmptyState;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();

    public VehicleAdapterRecycler(Context ct, List<Vehicles> ll, long id, VehicleOnClickListener vehicleOnClickListener, VehicleOnEmptyState emptyState) {
        context = ct;
        dataSet = ll;
        defaultId = id;
        this.vehicleOnClickListener = vehicleOnClickListener;
        this.vehicleOnEmptyState = emptyState;
    }


    @Override
    public long getHeaderId(int position) {
        return dataSet.get(position).getHeader();
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.name_and_model, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        Vehicles mData = dataSet.get(position);
        viewholder.month_txt.setText(mData.getProfile_name());
        //viewholder.year_txt.setImageResource(rootapp.getImageID(mData.getIcon_id()));

        viewholder.year_txt.setImageBitmap(mData.getCarImage());
        if (mData.get_id() != defaultId) {
            viewholder.year_txt.setAlpha(0.5f);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView month_txt;
        public ImageView year_txt;

        public HeaderHolder(View view) {
            super(view);
            month_txt = view.findViewById(R.id.month_txt);
            year_txt = view.findViewById(R.id.year_txt);
        }
    }

    public void setDefaultId(long id) {
        defaultId = id;
    }

    public void reloadData(List<Vehicles> newData) {
        if (newData.size() == 0) {
            vehicleOnEmptyState.onEmptyList();
        }
        dataSet.clear();
        dataSet.addAll(newData);
        notifyDataSetChanged();
    }

    public void remove(Vehicles pos) {
        dataSet.remove(pos);
    }

    public long getCarId(int pos) {
        Vehicles mData = dataSet.get(pos);
        return mData.get_id();
    }

    @Override
    public CarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View retView = LayoutInflater.from(context).inflate(R.layout.each_car_new, viewGroup, false);
        CarViewHolder holder = new CarViewHolder(retView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CarViewHolder carsViewHolder, final int i) {
        final Vehicles mData = dataSet.get(i);

        Log.d(getClass().getSimpleName(), mData.toString());

        if (vehicleOnClickListener != null) {
            carsViewHolder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.set_Selected();
                    if (mData.getisSelected()) {
                        carsViewHolder.fab.setImageResource(R.drawable.ic_menu_check);
                    } else {
                        //carsViewHolder.fab.setImageResource(rootapp.getImageID(mData.getIcon_id()));
                        carsViewHolder.fab.setImageResource(R.drawable.ic_menu_car);
                    }
                    vehicleOnClickListener.onClickVehicle(carsViewHolder.fab, mData);
                }
            });
        }
        //carsViewHolder.fab.setImageResource(rootapp.getImageID(mData.getIcon_id()));
        carsViewHolder.car_type.setImageResource(MeuCarroApplication.getImageID(mData.getIcon_id()));
        if (mData.getisSelected()) {
            carsViewHolder.fab.setImageResource(R.drawable.ic_menu_check);
        }

        Log.d(getClass().getSimpleName(), "ETHANOL = " + mData.getEtanol() + "GASOLINA = " + mData.getGasolina());

        if (mData.getEtanol() != null) {
            carsViewHolder.consumo_Etanol.setVisibility(View.VISIBLE);
            carsViewHolder.reveal_Etanol.setVisibility(View.VISIBLE);
            carsViewHolder.consumo_Etanol.setText(mData.getEtanol());
        } else {
            carsViewHolder.consumo_Etanol.setVisibility(View.GONE);
            carsViewHolder.reveal_Etanol.setVisibility(View.GONE);
        }

        if (mData.getGasolina() != null) {
            carsViewHolder.consumo_gasolina.setVisibility(View.VISIBLE);
            carsViewHolder.reveal_gasolina.setVisibility(View.VISIBLE);
            carsViewHolder.consumo_gasolina.setText(mData.getGasolina());
        } else {
            carsViewHolder.consumo_gasolina.setVisibility(View.GONE);
            carsViewHolder.reveal_gasolina.setVisibility(View.GONE);
        }

        if (mData.getGnv() != null) {
            carsViewHolder.consumo_gnv.setVisibility(View.VISIBLE);
            carsViewHolder.reveal_gnv.setVisibility(View.VISIBLE);
            carsViewHolder.consumo_gnv.setText(mData.getGnv());
        } else {
            carsViewHolder.consumo_gnv.setVisibility(View.GONE);
            carsViewHolder.reveal_gnv.setVisibility(View.GONE);
        }

        if (mData.getDiesel() != null) {
            carsViewHolder.consumo_diesel.setVisibility(View.VISIBLE);
            carsViewHolder.reveal_diesel.setVisibility(View.VISIBLE);
            carsViewHolder.consumo_diesel.setText(mData.getDiesel());
        } else {
            carsViewHolder.consumo_diesel.setVisibility(View.GONE);
            carsViewHolder.reveal_diesel.setVisibility(View.GONE);
        }

        if (mData.getTanque_comb() != null) {
            carsViewHolder.tq_comb.setVisibility(View.VISIBLE);
            carsViewHolder.reveal_tq_comb.setVisibility(View.VISIBLE);
            carsViewHolder.tq_comb.setText(mData.getTanque_comb());
        } else {
            carsViewHolder.tq_comb.setVisibility(View.GONE);
            carsViewHolder.reveal_tq_comb.setVisibility(View.GONE);
        }

        if (mData.getTanque_gnv() != null) {
            carsViewHolder.tq_gnv.setVisibility(View.VISIBLE);
            carsViewHolder.reveal_tq_gnv.setVisibility(View.VISIBLE);
            carsViewHolder.tq_gnv.setText(mData.getTanque_gnv());
        } else {
            carsViewHolder.tq_gnv.setVisibility(View.GONE);
            carsViewHolder.reveal_tq_gnv.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView consumo_Etanol;
        TextView reveal_Etanol;

        TextView consumo_gasolina;
        TextView reveal_gasolina;

        TextView consumo_gnv;
        TextView reveal_gnv;

        TextView consumo_diesel;
        TextView reveal_diesel;

        TextView tq_comb;
        TextView reveal_tq_comb;

        TextView tq_gnv;
        TextView reveal_tq_gnv;
        FloatingActionButton fab;
        CardView cardView;

        ImageView car_type;

        public CarViewHolder(View view) {
            super(view);

            consumo_Etanol = view.findViewById(R.id.txt_Etanol);
            reveal_Etanol = view.findViewById(R.id.txt_Etanol_name);

            consumo_gasolina = view.findViewById(R.id.txt_gasolina);
            reveal_gasolina = view.findViewById(R.id.txt_gasolina_name);

            consumo_gnv = view.findViewById(R.id.txt_gnv);
            reveal_gnv = view.findViewById(R.id.txt_gnv_name);

            consumo_diesel = view.findViewById(R.id.txt_diesel);
            reveal_diesel = view.findViewById(R.id.txt_diesel_name);

            tq_comb = view.findViewById(R.id.txt_cap_tanque);
            reveal_tq_comb = view.findViewById(R.id.txt_tanque_name);

            tq_gnv = view.findViewById(R.id.txt_cap_gnv);
            reveal_tq_gnv = view.findViewById(R.id.txt_cilindro_name);
            fab = view.findViewById(R.id.fab_maintenance);
            cardView = view.findViewById(R.id.car_cardview);

            car_type = view.findViewById(R.id.car_type);
        }
    }

    public interface VehicleOnClickListener {
        void onClickVehicle(View view, Vehicles mData);
    }

    public interface VehicleOnEmptyState {
        void onEmptyList();
    }
}

