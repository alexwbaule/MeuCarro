package com.alexwbaule.flexprofile.fragments;

/**
 * Created by alex on 10/04/16.
 */

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import android.widget.LinearLayout;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.MaintenancePartsAdapterRecycler;
import com.alexwbaule.flexprofile.containers.Categories;
import com.alexwbaule.flexprofile.containers.DateTime;
import com.alexwbaule.flexprofile.containers.MaintenanceParts;
import com.alexwbaule.flexprofile.containers.Maintenances;
import com.alexwbaule.flexprofile.dialogs.MaintanancePartsAdd;
import com.alexwbaule.flexprofile.utils.PreferencesProcessed;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

public class MaintenanceAdd extends BaseFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler, TimePickerDialogFragment.TimePickerDialogHandler, CalendarDatePickerDialog.OnDateSetListener, MaintanancePartsAdd.onAddPart {
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private static final String ARG_PARAM1 = "HISTID";
    private static final String ARG_PARAM2 = "EDTID";
    private static final String ARG_PARAM3 = "DTID";
    private static final String ARG_PARAM4 = "PRTLIST";

    private final String CLASSNAME = "MaintenanceAdd";

    private long mParam1 = 0;

    private int EditUsed;
    private int EditDateUsed;

    private TextInputEditText maintenance_add_date;
    private TextInputEditText maintenance_add_time;
    private TextInputEditText maintenance_add_km;
    private TextInputEditText maintenance_add_local;
    private LinearLayout empty_list_warn;
    private RecyclerView parts_list_recycler;
    private TextInputLayout hint_maintenance_add_km;
    private static TreeMap<Categories, ArrayList<MaintenanceParts>> partsLast;
    private static ArrayList<Categories> maintenanceAllCategories;

    protected MaintenancePartsAdapterRecycler adapter;
    private LinearLayoutManager mLayoutManager;

    OnMaintenanceComplete mCallback;

    private ActionMode mActionMode;

    private long[] ret;
    private static CalendarDatePickerDialog calendar;
    private PreferencesProcessed valores;
    private SaveShowCalculos saveshow;

    public interface OnMaintenanceComplete {
        void onMaintenance();
    }


    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i1, int i2) {
        Log.d(getClass().getSimpleName(), "********** onDateSet **************************");
        Log.d(getClass().getSimpleName(), "YEAR=" + i + " MES=" + i1 + " DIA=" + i2);
        Calendar cal = new GregorianCalendar(i, i1, i2);
        String thisdate = android.text.format.DateFormat.getDateFormat(getActivity()).format(cal.getTime());
        TextInputEditText updatetxt = getActivity().findViewById(EditDateUsed);
        updatetxt.setText(thisdate);

        long[] ret;
        maintenance_add_time = getActivity().findViewById(R.id.maintenance_add_time);
        ret = rootapp.getMaintenanceIntevalKM(thisdate + " " + maintenance_add_time.getText().toString(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        hint_maintenance_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_maintenance_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }
        if (savedInstanceState != null) {
            EditUsed = savedInstanceState.getInt(ARG_PARAM2);
            EditDateUsed = savedInstanceState.getInt(ARG_PARAM3);
            partsLast = (TreeMap<Categories, ArrayList<MaintenanceParts>>) savedInstanceState.getSerializable(ARG_PARAM4);
        } else {
            partsLast = new TreeMap<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PARAM2, EditUsed);
        outState.putInt(ARG_PARAM3, EditDateUsed);
        outState.putSerializable(ARG_PARAM4, partsLast);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnMaintenanceComplete) getAppCompatActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getAppCompatActivity().toString() + " must implement OnMaintenanceComplete");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        saveshow = new SaveShowCalculos(rootapp);
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);

        mActionMode = getAppCompatActivity().startSupportActionMode(mActionModeCallback);
        mActionMode.setTitle(rootapp.getString(R.string.nova_manutencao));

        maintenanceAllCategories = rootapp.getCategories();

        // Inflate the layout for this fragment
        valores = new PreferencesProcessed(getActivity());
        DateTime today = rootapp.splitedNowDateTime(Calendar.getInstance().getTime());

        View rootView = inflater.inflate(R.layout.fragment_maintenance_add, container, false);

        maintenance_add_date = rootView.findViewById(R.id.maintenance_add_date);
        maintenance_add_local = rootView.findViewById(R.id.maintenance_add_local);
        maintenance_add_time = rootView.findViewById(R.id.maintenance_add_time);
        parts_list_recycler = rootView.findViewById(R.id.parts_list_recycler);

        empty_list_warn = rootView.findViewById(R.id.empty_list_warn);

        HideShow(empty_list_warn);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        parts_list_recycler.setLayoutManager(mLayoutManager);
        parts_list_recycler.setItemAnimator(new DefaultItemAnimator());
        parts_list_recycler.setHasFixedSize(true);

        adapter = new MaintenancePartsAdapterRecycler(getActivity(), partsLast, maintenanceAllCategories, onClickListener(), onEmptyState());
        parts_list_recycler.setAdapter(adapter);

        maintenance_add_km = rootView.findViewById(R.id.maintenance_add_km);
        hint_maintenance_add_km = rootView.findViewById(R.id.hint_maintenance_add_km);

        ret = rootapp.getMaintenanceIntevalKM(today.getDate() + " " + today.getTime(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));

        maintenance_add_km.setOnClickListener(meuclick);
        maintenance_add_km.setTag(getString(R.string.km_atual, valores.getOdometro()));

        maintenance_add_date.setOnClickListener(opencalendar);
        maintenance_add_date.setText(today.getDate());

        maintenance_add_time.setOnClickListener(meuclicktime);
        maintenance_add_time.setText(today.getTime());

        hint_maintenance_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_maintenance_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }

        if (mParam1 > 0) {
            Maintenances maintenances = rootapp.getOneMaintenance(mParam1);
            mActionMode.setTitle(getString(R.string.edit_maintenance));
            maintenance_add_date.setText(maintenances.getCreated_date());
            maintenance_add_time.setText(maintenances.getCreated_time());
            maintenance_add_local.setText(maintenances.getMaintenance_local());
            maintenance_add_km.setText(maintenances.getSignKm());
            ret = rootapp.getMaintenanceIntevalKM(maintenances.getCreated_date() + " " + maintenances.getCreated_time(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
            hint_maintenance_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
            if (ret[1] == 0) {
                hint_maintenance_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
            }
            adapter.reloadData(maintenances.getParts());
            HideShow(empty_list_warn);

        }
        return rootView;
    }

    private void HideShow(View v) {
        empty_list_warn = v.findViewById(R.id.empty_list_warn);

        if (empty_list_warn != null) {
            Log.d(getClass().getSimpleName(), String.format("WHAAAT --> %d -- %b", partsLast.size(), partsLast.isEmpty()));
            empty_list_warn.setVisibility(View.GONE);
            if (partsLast.isEmpty()) {
                empty_list_warn.setVisibility(View.VISIBLE);
            }
        }
    }

    private MaintenancePartsAdapterRecycler.MaintenancePartsOnClickListener onClickListener() {
        return new MaintenancePartsAdapterRecycler.MaintenancePartsOnClickListener() {
            @Override
            public void onClickMaintenanceParts(View view, Categories cat, MaintenanceParts mData) {
                HideShow(getView());
            }
        };
    }

    private MaintenancePartsAdapterRecycler.MaintenancePartsOnEmptyState onEmptyState() {
        return new MaintenancePartsAdapterRecycler.MaintenancePartsOnEmptyState() {
            @Override
            public void onEmptyList() {
                HideShow(getView());
            }
        };
    }

    private View.OnClickListener opencalendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditDateUsed = v.getId();
            Calendar teste = parseDate(((TextInputEditText) v).getText().toString());
            calendar = CalendarDatePickerDialog.newInstance(MaintenanceAdd.this, teste.get(Calendar.YEAR), teste.get(Calendar.MONTH), teste.get(Calendar.DAY_OF_MONTH));
            calendar.setYearRange(Calendar.getInstance().get(Calendar.YEAR) - 10, Calendar.getInstance().get(Calendar.YEAR));
            calendar.setMaxDate(Calendar.getInstance());
            calendar.setTargetFragment(MaintenanceAdd.this, 0);
            calendar.show(getFragmentManager(), "Calendar");
        }
    };

    View.OnClickListener meuclicktime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            TimePickerBuilder dpb = new TimePickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setTargetFragment(MaintenanceAdd.this)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            dpb.show();
        }
    };

    View.OnClickListener meuclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            NumberPickerBuilder dpb = new NumberPickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setLabelText(v.getTag().toString())
                    .setTargetFragment(MaintenanceAdd.this)
                    .setPlusMinusVisibility(View.GONE)
                    .setDecimalVisibility(View.GONE)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            if (v.getId() == R.id.maintenance_add_km) {
                dpb.setDecimalVisibility(View.GONE);
            }
            dpb.show();
        }
    };

    @Override
    public void onDialogNumberSet(int i, int i1, double v, boolean b, double v1) {
        NumberFormat format = new DecimalFormat();
        TextInputEditText updatetxt = getActivity().findViewById(EditUsed);
        updatetxt.setText(format.format(v1));
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        boolean isfuture = false;
        DateTime today = rootapp.splitedNowDateTime(Calendar.getInstance().getTime());

        maintenance_add_date = getActivity().findViewById(R.id.maintenance_add_date);

        TextInputEditText updatetxt = getActivity().findViewById(EditUsed);
        String thistime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        String thisdate = maintenance_add_date.getText().toString();

        String[] hora = thistime.split(":");
        String[] hora_2 = today.getTime().split(":");

        if (today.getDate().equals(thisdate)) {
            if ((Integer.valueOf(hora[0]) == Integer.valueOf(hora_2[0])) && (Integer.valueOf(hora[1]) > Integer.valueOf(hora_2[1]))) {
                isfuture = true;
            } else if (Integer.valueOf(hora[0]) > Integer.valueOf(hora_2[0])) {
                isfuture = true;
            }
        }

        if (isfuture) {
            MeuCarroApplication.WarnUser(getView(), "Não é possível selecionar um horário no futuro", Snackbar.LENGTH_LONG, true, null);
            thistime = today.getTime();
        }

        updatetxt.setText(thistime);

        long[] ret;
        ret = rootapp.getMaintenanceIntevalKM(thisdate + " " + thistime, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        hint_maintenance_add_km = getActivity().findViewById(R.id.hint_maintenance_add_km);
        hint_maintenance_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_maintenance_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
    }

    @Override
    public void onAdd(MaintenanceParts parts) {
        ArrayList<MaintenanceParts> mp = partsLast.get(maintenanceAllCategories.get(parts.getCatg_id()));
        if (mp == null)
            mp = new ArrayList<>();
        mp.add(parts);
        partsLast.put(maintenanceAllCategories.get(parts.getCatg_id()), mp);
        adapter.notifyDataSetChanged();
        HideShow(getView());
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.save_and_parts, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            replaceFragment(new Maintenance());
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_submenu_cancel:
                    actionMode.finish();
                    break;
                case R.id.action_submenu_save:
                    NumberFormat parser = NumberFormat.getInstance();
                    parser.setMaximumFractionDigits(0);
                    DecimalFormat format = new DecimalFormat();

                    maintenance_add_local = getAppCompatActivity().findViewById(R.id.maintenance_add_local);
                    maintenance_add_km = getAppCompatActivity().findViewById(R.id.maintenance_add_km);
                    maintenance_add_date = getAppCompatActivity().findViewById(R.id.maintenance_add_date);
                    maintenance_add_time = getAppCompatActivity().findViewById(R.id.maintenance_add_time);

                    String maintenance_add_date_full = maintenance_add_date.getText().toString() + " " + maintenance_add_time.getText().toString();


                    String description = maintenance_add_local.getText().toString();

                    long new_km = 0;

                    try {
                        new_km = format.parse(maintenance_add_km.getText().toString()).longValue();
                    } catch (NumberFormatException | ParseException e) {
                        new_km = 0;
                    }
                    if (ret[1] > 0 && ret[1] <= new_km) {
                        MeuCarroApplication.WarnUser(getView(), getString(R.string.km_atual_maior, new_km, ret[0]), Snackbar.LENGTH_LONG, true, menuItem);
                        return true;
                    }

                    if (ret[0] >= new_km) {
                        MeuCarroApplication.WarnUser(getView(), getString(R.string.km_atual_menor, new_km, ret[0]), Snackbar.LENGTH_LONG, true, menuItem);
                        return true;
                    }

                    if (partsLast.size() == 0) {
                        MeuCarroApplication.WarnUser(getView(), "A lista de peças não pode estar vazio", Snackbar.LENGTH_LONG, true, menuItem);
                    } else if (description.isEmpty()) {
                        MeuCarroApplication.WarnUser(getView(), "O campo estabelecimento não pode estar vazio", Snackbar.LENGTH_LONG, true, menuItem);
                    } else {
                        if (mParam1 > 0) {
                            rootapp.updateMaintenance(mParam1, description, new_km, maintenance_add_date_full, 0);
                            rootapp.deleteAllMaintenanceParts(mParam1);
                            for (ArrayList<MaintenanceParts> p : partsLast.values()) {
                                rootapp.updateMaintenanceParts(mParam1, p);
                            }
                        } else {
                            long id = rootapp.createMaintenance(description, new_km, maintenance_add_date_full, 0);
                            for (ArrayList<MaintenanceParts> p : partsLast.values()) {
                                rootapp.createMaintenanceParts(id, p);
                            }
                        }
                        mCallback.onMaintenance();
                        actionMode.finish();
                    }
                    break;
                case R.id.action_submenu_parts:
                    MaintanancePartsAdd dlg = new MaintanancePartsAdd();
                    dlg.setTargetFragment(MaintenanceAdd.this, 0);
                    dlg.show(getFragmentManager(), "add_parts");
                    break;
            }
            menuItem.setEnabled(true);
            return true;
        }
    };
}
