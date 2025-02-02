package com.alexwbaule.flexprofile.fragments;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.VehicleAdapterRecycler;
import com.alexwbaule.flexprofile.containers.Vehicles;
import com.alexwbaule.flexprofile.view.decoration.DividerDecoration;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 09/07/15.
 */
public class Vehicle extends BaseFragment {
    protected RecyclerView recyclerView;
    protected VehicleAdapterRecycler adapter;
    private LinearLayoutManager mLayoutManager;
    private StickyHeaderDecoration stickyHeaderDecoration;
    private ActionMode actionMode;
    private List<Vehicles> ids;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    OnSetDefaultCarListener mCallback;
    private View view;


    // Container Activity must implement this interface
    public interface OnSetDefaultCarListener {
        void onDefaultCarSet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootapp.getHasCars() == 0) {
            view = inflater.inflate(R.layout.empty_car, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_vehicle, container, false);
            recyclerView = view.findViewById(R.id.vehicle_list_recycler);
            ids = new ArrayList<>();
            adapter = new VehicleAdapterRecycler(getActivity(), rootapp.fetchAllCars(), rootapp.getDefaultCar(), onClickListener(), onEmptyState());

            mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            stickyHeaderDecoration = new StickyHeaderDecoration(adapter);

            DividerDecoration divider = new DividerDecoration.Builder(getActivity())
                    .setLeftPadding(R.dimen.padding_list)
                    .setColor(ContextCompat.getColor(getActivity(), R.color.divider))
                    .build();
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(divider);
            recyclerView.addItemDecoration(stickyHeaderDecoration);
            recyclerView.setAdapter(adapter);
        }
        final FloatingActionButton fab = view.findViewById(R.id.fab_vehicle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new VehicleAdd());
            }
        });
        return view;
    }

    private ActionMode.Callback getActionModeCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.edit_delete_set, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                item.setEnabled(false);
                rootapp = MeuCarroApplication.getInstance();
                if (item.getItemId() == R.id.action_submenu_del) {
                    for (Vehicles mid : ids) {
                        adapter.remove(mid);
                    }
                    Snackbar.make(recyclerView, getResources().getQuantityString(R.plurals.itens_excluidos, ids.size(), ids.size()), Snackbar.LENGTH_LONG).setAction(R.string.desfazer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.reloadData(rootapp.fetchAllCars());
                            ids.clear();
                        }
                    }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            Log.d(getClass().getSimpleName(), "Snackbar is Dismissed BY " + event);
                            if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE) {
                                removeData();
                                stickyHeaderDecoration.clearHeaderCache();
                            }
                            super.onDismissed(snackbar, event);
                        }
                    }).show();
                } else if (item.getItemId() == R.id.action_submenu_edit) {
                    for (Vehicles mid : ids) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("CARID", mid.get_id());
                        Fragment frag = new VehicleAdd();
                        frag.setArguments(bundle);
                        replaceFragment(frag);
                    }
                } else if (item.getItemId() == R.id.action_submenu_default) {
                    for (Vehicles mid : ids) {
                        rootapp.setDefaultCar(mid.get_id());
                        adapter.setDefaultId(mid.get_id());
                    }
                    adapter.reloadData(rootapp.fetchAllCars());
                    stickyHeaderDecoration.clearHeaderCache();
                    ids.clear();
                }
                mCallback.onDefaultCarSet();
                mode.finish();
                item.setEnabled(true);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    private void removeData() {
        Log.d(getClass().getSimpleName(), "Running Removing ");
        for (Vehicles mid : ids) {
            Log.d(getClass().getSimpleName(), "Removing =" + mid.get_id());
            rootapp.deleteCar(mid.get_id());
        }
        ids.clear();
        adapter.reloadData(rootapp.fetchAllCars());
        recyclerView.getAdapter().notifyDataSetChanged();
        mCallback.onDefaultCarSet();
    }

    private VehicleAdapterRecycler.VehicleOnClickListener onClickListener() {
        return new VehicleAdapterRecycler.VehicleOnClickListener() {
            @Override
            public void onClickVehicle(View view, Vehicles mData) {
                if (actionMode == null) {
                    actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallback());
                }
                if (mData.getisSelected()) {
                    if (!ids.contains(mData)) {
                        ids.add(mData);
                    }
                } else {
                    ids.remove(mData);
                }
                if (ids.size() == 0) {
                    ids.clear();
                    if (actionMode != null) {
                        actionMode.finish();
                    }
                }
                updateActionModeTitle(actionMode, ids.size());
            }
        };
    }

    private VehicleAdapterRecycler.VehicleOnEmptyState onEmptyState() {
        return new VehicleAdapterRecycler.VehicleOnEmptyState() {
            @Override
            public void onEmptyList() {
                try {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    inflater.inflate(R.layout.empty_car, ((ViewGroup) getView()), true);
                } catch (Exception e) {

                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnSetDefaultCarListener) getAppCompatActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getAppCompatActivity().toString() + " must implement OnSetDefaultCarListener");
        }
    }
}
