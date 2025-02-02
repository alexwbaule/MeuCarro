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
import com.alexwbaule.flexprofile.adapter.OilAdapterRecycler;
import com.alexwbaule.flexprofile.containers.Oils;
import com.alexwbaule.flexprofile.view.decoration.DividerDecoration;
import com.alexwbaule.flexprofile.view.decoration.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 09/07/15.
 */
public class OilCharge extends BaseFragment {
    protected RecyclerView recyclerView;
    protected OilAdapterRecycler adapter;
    private LinearLayoutManager mLayoutManager;
    private StickyHeaderDecoration stickyHeaderDecoration;
    private ActionMode actionMode;
    private List<Oils> ids;
    private View view;
    MeuCarroApplication rootapp = MeuCarroApplication.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootapp.getHasOilHistory() == 0) {
            view = inflater.inflate(R.layout.empty_oil, container, false);
            TextView txt_empty_value = view.findViewById(R.id.txt_empty_value);
            FloatingActionButton fab = view.findViewById(R.id.fab_oilcharge);
            if (rootapp.getHasCars() == 0) {
                txt_empty_value.setText(R.string.vc_nao_tem_veiculos_p_oleo);
                fab.setImageResource(R.drawable.ic_menu_car);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_oilcharge, container, false);
            ids = new ArrayList<>();
            recyclerView = view.findViewById(R.id.oil_list_recycler);
            adapter = new OilAdapterRecycler(getActivity(), rootapp.getAllOil(), rootapp.getDefaultCar(), onClickListener(), onEmptyState());

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

        final FloatingActionButton fab = view.findViewById(R.id.fab_oilcharge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MeuCarroApplication.getInstance().getHasCars() == 0) {
                    replaceFragment(new VehicleAdd());
                } else {
                    replaceFragment(new OilChargeAdd());
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
                if (item.getItemId() == R.id.action_submenu_del) {
                    for (Oils mid : ids) {
                        adapter.remove(mid);
                    }
                    Snackbar.make(recyclerView, getResources().getQuantityString(R.plurals.itens_excluidos, ids.size(), ids.size()), Snackbar.LENGTH_LONG).setAction(R.string.desfazer, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.reloadData(rootapp.getAllOil());
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
                    for (Oils mid : ids) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("HISTID", mid.getId());
                        Fragment frag = new OilChargeAdd();
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
        Log.d(getClass().getSimpleName(), "Running Removing SIZE --> " + ids.size());
        for (Oils mid : ids) {
            Log.d(getClass().getSimpleName(), "Removing =" + mid.getId());
            rootapp.deleteOil(String.valueOf(mid.getId()));
        }
        ids.clear();
        adapter.reloadData(rootapp.getAllOil());
        stickyHeaderDecoration.clearHeaderCache();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private OilAdapterRecycler.OilOnClickListener onClickListener() {
        return new OilAdapterRecycler.OilOnClickListener() {
            @Override
            public void onClickOil(View view, Oils mData) {
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

    private OilAdapterRecycler.OilOnEmptyState onEmptyState() {
        return new OilAdapterRecycler.OilOnEmptyState() {
            @Override
            public void onEmptyList() {
                try {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    inflater.inflate(R.layout.empty_oil, ((ViewGroup) getView()), true);
                } catch (Exception e) {

                }
            }
        };
    }
}
