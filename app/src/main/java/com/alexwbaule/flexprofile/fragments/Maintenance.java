package com.alexwbaule.flexprofile.fragments;

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
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.MaintenanceAdapterRecycler;
import com.alexwbaule.flexprofile.containers.Maintenances;
import com.alexwbaule.flexprofile.view.decoration.DividerDecoration;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by alex on 10/04/16.
 */
public class Maintenance extends BaseFragment {
    protected RecyclerView recyclerView;
    protected MaintenanceAdapterRecycler adapter;
    private LinearLayoutManager mLayoutManager;
    private StickyHeaderDecoration stickyHeaderDecoration;
    private ActionMode actionMode;
    private List<Maintenances> ids;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (MeuCarroApplication.getInstance().getHasMaintenance() == 0) {
            view = inflater.inflate(R.layout.empty_maintenance, container, false);
            TextView txt_empty_value = view.findViewById(R.id.txt_empty_value);
            FloatingActionButton fab = view.findViewById(R.id.fab_maintenance);
            if (MeuCarroApplication.getInstance().getHasCars() == 0) {
                txt_empty_value.setText(R.string.vc_nao_tem_veiculo_maintenance);
                fab.setImageResource(R.drawable.ic_menu_car);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_maintenance, container, false);
            recyclerView = view.findViewById(R.id.maintenance_list_recycler);
            ids = new ArrayList<>();
            adapter = new MaintenanceAdapterRecycler(getActivity(), rootapp.getAllMaintenances(), rootapp.getDefaultCar(), onClickListener(), onEmptyState());


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


            recyclerView.setAdapter(adapter);

        }
        final FloatingActionButton fab = view.findViewById(R.id.fab_maintenance);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MeuCarroApplication.getInstance().getHasCars() == 0) {
                    replaceFragment(new VehicleAdd());
                } else {
                    replaceFragment(new MaintenanceAdd());
                }
            }
        });
        return view;
    }

    private ActionMode.Callback getActionModeCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.edit_delete, menu);
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
                    for (Maintenances mid : ids) {
                        adapter.remove(mid);
                    }
                    Snackbar.make(recyclerView, getResources().getQuantityString(R.plurals.itens_excluidos, ids.size(), ids.size()), Snackbar.LENGTH_LONG).setAction(R.string.desfazer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.reloadData(rootapp.getAllMaintenances());
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
                    for (Maintenances mid : ids) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("HISTID", mid.getId());
                        Fragment frag = new MaintenanceAdd();
                        frag.setArguments(bundle);
                        replaceFragment(frag);
                    }
                }
                adapter.notifyDataSetChanged();
                stickyHeaderDecoration.clearHeaderCache();
                mode.finish();
                item.setEnabled(true);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d(getClass().getSimpleName(), "Using onDestroyActionMode");
                actionMode = null;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

    private void removeData() {
        Log.d(getClass().getSimpleName(), "Running Removing ");
        for (Maintenances mid : ids) {
            Log.d(getClass().getSimpleName(), "Removing =" + mid.getId());
            rootapp.deleteMaintenance(mid.getId());
        }
        ids.clear();
        adapter.reloadData(rootapp.getAllMaintenances());
        stickyHeaderDecoration.clearHeaderCache();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private MaintenanceAdapterRecycler.MaintenanceOnClickListener onClickListener() {
        return new MaintenanceAdapterRecycler.MaintenanceOnClickListener() {
            @Override
            public void onClickMaintenance(View view, Maintenances mData) {
                if (actionMode == null) {
                    actionMode = getAppCompatActivity().startSupportActionMode(getActionModeCallback());
                }
                Log.d(getClass().getSimpleName(), "IS SELECTED ? -> " + mData.isSelected());
                if (mData.isSelected()) {
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

    private MaintenanceAdapterRecycler.MaintenanceOnEmptyState onEmptyState() {
        return new MaintenanceAdapterRecycler.MaintenanceOnEmptyState() {
            @Override
            public void onEmptyList() {
                try {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    inflater.inflate(R.layout.empty_maintenance, ((ViewGroup) getView()), true);
                } catch (Exception e) {

                }
            }
        };
    }
}
