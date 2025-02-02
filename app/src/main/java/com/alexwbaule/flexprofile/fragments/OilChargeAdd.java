package com.alexwbaule.flexprofile.fragments;

import android.database.Cursor;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.AutoCompleteOilNameAdapter;
import com.alexwbaule.flexprofile.containers.DateTime;
import com.alexwbaule.flexprofile.domain.SQLiteHelper;
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
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by alex on 11/07/15.
 */
public class OilChargeAdd extends BaseFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler, TimePickerDialogFragment.TimePickerDialogHandler, CalendarDatePickerDialog.OnDateSetListener {
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private static final String ARG_PARAM1 = "HISTID";
    private static final String ARG_PARAM2 = "EDTID";
    private static final String ARG_PARAM3 = "DTID";
    private final String CLASSNAME = "OilChargeAdd";

    private long mParam1 = 0;

    private int EditUsed;
    private int EditDateUsed;

    private TextInputEditText oil_add_date;
    private TextInputEditText oil_add_time;
    private TextInputEditText oil_add_km;
    private TextInputEditText oil_add_price;
    private TextInputEditText oil_add_nextkm;
    private TextInputEditText oil_add_litros;

    private TextInputLayout hint_oil_add_km;
    private TextInputLayout hint_oil_add_nextkm;
    private TextInputLayout hint_oil_add_litros;
    private TextInputLayout hint_oil_add_price;

    private ActionMode mActionMode;

    private AutoCompleteTextView oil_add_name;
    private SwitchCompat oil_add_filter;
    private long[] ret;
    private CalendarDatePickerDialog calendar;
    private PreferencesProcessed valores;
    private SaveShowCalculos saveshow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }
        if (savedInstanceState != null) {
            EditUsed = savedInstanceState.getInt(ARG_PARAM2);
            EditDateUsed = savedInstanceState.getInt(ARG_PARAM3);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PARAM2, EditUsed);
        outState.putInt(ARG_PARAM3, EditDateUsed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        saveshow = new SaveShowCalculos(rootapp);
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);


        // Inflate the layout for this fragment
        valores = new PreferencesProcessed(getActivity());
        DateTime today = rootapp.splitedNowDateTime(Calendar.getInstance().getTime());

        mActionMode = getAppCompatActivity().startSupportActionMode(mActionModeCallback);
        mActionMode.setTitle(rootapp.getString(R.string.nova_troca_oleo));

        View rootView = inflater.inflate(R.layout.fragment_oilcharge_add, container, false);

        oil_add_date = rootView.findViewById(R.id.oil_add_date);
        oil_add_time = rootView.findViewById(R.id.oil_add_time);

        oil_add_price = rootView.findViewById(R.id.oil_add_price);
        oil_add_km = rootView.findViewById(R.id.oil_add_km);
        oil_add_nextkm = rootView.findViewById(R.id.oil_add_nextkm);
        oil_add_litros = rootView.findViewById(R.id.oil_add_litros);
        oil_add_name = rootView.findViewById(R.id.oil_add_name);
        oil_add_filter = rootView.findViewById(R.id.oil_add_filtro);

        hint_oil_add_km = rootView.findViewById(R.id.hint_oil_add_km);
        hint_oil_add_nextkm = rootView.findViewById(R.id.hint_oil_add_nextkm);
        hint_oil_add_litros = rootView.findViewById(R.id.hint_oil_add_litros);
        hint_oil_add_price = rootView.findViewById(R.id.hint_oil_add_price);

        AutoCompleteOilNameAdapter adapter = new AutoCompleteOilNameAdapter(getActivity());
        oil_add_name.setAdapter(adapter);
        oil_add_name.setThreshold(3);

        ret = rootapp.getOilIntevalKM(today.getDate() + " " + today.getTime(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));

        oil_add_km.setOnClickListener(meuclick);
        oil_add_km.setTag(getString(R.string.km_atual, valores.getOdometro()));

        hint_oil_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_oil_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }

        oil_add_nextkm.setOnClickListener(meuclick);
        oil_add_nextkm.setTag(getString(R.string.proxima_troca_medida, valores.getOdometro()));
        hint_oil_add_nextkm.setHint(getString(R.string.proxima_troca_medida, valores.getOdometro()));

        oil_add_litros.setOnClickListener(meuclick);
        oil_add_litros.setTag(valores.getVolumeTanque());
        hint_oil_add_litros.setHint(valores.getVolumeTanque());


        oil_add_price.setOnClickListener(meuclick);
        oil_add_price.setTag(getString(R.string.simbolo_moeda));
        hint_oil_add_price.setHint(getString(R.string.price_by_liter, valores.getVolumeTanque(), getString(R.string.simbolo_moeda)));
        hint_oil_add_price.setError(getString(R.string.total_price, getString(R.string.simbolo_moeda), "-"));


        oil_add_date.setOnClickListener(opencalendar);
        oil_add_date.setText(today.getDate());

        oil_add_time.setOnClickListener(meuclicktime);
        oil_add_time.setText(today.getTime());

        if (mParam1 > 0) {
            Cursor cursor = rootapp.getOneOil(mParam1);
            mActionMode.setTitle(rootapp.getString(R.string.editar_troca_oleo));
            cursor.moveToFirst();
            String thisdate = cursor.getString(cursor.getColumnIndex(SQLiteHelper.HIST_DATE));
            String thistime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIME));
            oil_add_name.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_NAME)));
            oil_add_date.setText(thisdate);
            oil_add_time.setText(thistime);
            oil_add_price.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_PRICE))));
            oil_add_km.setText(saveshow.getShowOdometrov2(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_KM)), false));
            oil_add_nextkm.setText(saveshow.getShowOdometrov2(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_NEXT_KM)), false));
            oil_add_litros.setText(saveshow.getShowVolumeMix(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_LTS)), 0));
            if (cursor.getInt(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_FILTER)) == 1) {
                oil_add_filter.setChecked(true);
            }
            cursor.close();
            ret = rootapp.getOilIntevalKM(thisdate + " " + thistime, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
            hint_oil_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
            if (ret[1] == 0) {
                hint_oil_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
            }
            calcTotalPrice(rootView);
        }
        return rootView;
    }

    protected void calcTotalPrice(View v) {
        NumberFormat format2 = new DecimalFormat("0.00");
        NumberFormat parseFloat = NumberFormat.getInstance();
        Number ParsedPrice = 0;
        Number ParsedLts = 0;

        oil_add_litros = v.findViewById(R.id.oil_add_litros);
        oil_add_price = v.findViewById(R.id.oil_add_price);
        hint_oil_add_price = v.findViewById(R.id.hint_oil_add_price);

        if ((oil_add_litros != null) && (oil_add_price != null)) {
            if (oil_add_litros.getText().length() > 0 && oil_add_price.getText().length() > 0) {
                try {
                    ParsedPrice = parseFloat.parse(oil_add_price.getText().toString());
                    ParsedLts = parseFloat.parse(oil_add_litros.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                hint_oil_add_price.setError(getString(R.string.total_price, getString(R.string.simbolo_moeda), format2.format(ParsedPrice.doubleValue() * ParsedLts.doubleValue())));
            }
        }
    }

    private View.OnClickListener opencalendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditDateUsed = v.getId();
            Calendar teste = parseDate(((TextInputEditText) v).getText().toString());
            calendar = CalendarDatePickerDialog.newInstance(OilChargeAdd.this, teste.get(Calendar.YEAR), teste.get(Calendar.MONTH), teste.get(Calendar.DAY_OF_MONTH));
            calendar.setYearRange(Calendar.getInstance().get(Calendar.YEAR) - 10, Calendar.getInstance().get(Calendar.YEAR));
            calendar.setMaxDate(Calendar.getInstance());
            calendar.setTargetFragment(OilChargeAdd.this, 0);
            calendar.show(getFragmentManager(), "Calendar");
        }
    };

    View.OnClickListener meuclicktime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            TimePickerBuilder dpb = new TimePickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setTargetFragment(OilChargeAdd.this)
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
                    .setTargetFragment(OilChargeAdd.this)
                    .setPlusMinusVisibility(View.GONE)
                    .setDecimalVisibility(View.GONE)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            if (v.getId() == R.id.oil_add_litros) {
                dpb.setDecimalVisibility(View.VISIBLE);
            }
            dpb.show();
        }
    };


    @Override
    public void onDialogNumberSet(int i, int i2, double v, boolean b, double v2) {
        NumberFormat format = new DecimalFormat();
        TextInputEditText updatetxt = getActivity().findViewById(EditUsed);
        updatetxt.setText(format.format(v2));
        calcTotalPrice(getView());
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        boolean isfuture = false;
        DateTime today = rootapp.splitedNowDateTime(Calendar.getInstance().getTime());
        oil_add_date = getActivity().findViewById(R.id.oil_add_date);

        TextInputEditText updatetxt = getActivity().findViewById(EditUsed);
        String thistime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        String thisdate = oil_add_date.getText().toString();

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
        ret = rootapp.getOilIntevalKM(thisdate + " " + thistime, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        hint_oil_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_oil_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.save, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_submenu_cancel) {
                actionMode.finish();
            } else {
                menuItem.setEnabled(false);
                long new_km = 0;
                long new_next_km = 0;
                double new_lts = 0;
                double new_price = 0;
                DecimalFormat format = new DecimalFormat();

                NumberFormat parser = NumberFormat.getInstance();
                parser.setMaximumFractionDigits(3);

                try {
                    oil_add_date = getActivity().findViewById(R.id.oil_add_date);
                    oil_add_time = getActivity().findViewById(R.id.oil_add_time);
                    oil_add_km = getActivity().findViewById(R.id.oil_add_km);
                    oil_add_nextkm = getActivity().findViewById(R.id.oil_add_nextkm);
                    oil_add_litros = getActivity().findViewById(R.id.oil_add_litros);
                    oil_add_price = getActivity().findViewById(R.id.oil_add_price);
                    oil_add_name = getActivity().findViewById(R.id.oil_add_name);
                    oil_add_filter = getActivity().findViewById(R.id.oil_add_filtro);
                }catch (NullPointerException e){

                }

                String oil_add_date_full = oil_add_date.getText().toString() + " " + oil_add_time.getText().toString();

                if (oil_add_km.getText().toString().isEmpty()) {
                    MeuCarroApplication.WarnUser(getView(), R.string.km_atual_vazio, Snackbar.LENGTH_LONG, true, menuItem);
                    return true;
                }
                if (oil_add_nextkm.getText().toString().isEmpty()) {
                    MeuCarroApplication.WarnUser(getView(), R.string.km_proxima_troca_vazio, Snackbar.LENGTH_LONG, true, menuItem);
                    return true;
                }

                if (oil_add_name.getText().toString().isEmpty()) {
                    MeuCarroApplication.WarnUser(getView(), R.string.nome_do_oleo_vazio, Snackbar.LENGTH_LONG, true, menuItem);
                    return true;
                }

                try {
                    new_price = format.parse(oil_add_price.getText().toString()).doubleValue();
                } catch (NumberFormatException | ParseException e) {
                    new_price = 0;
                }

                try {
                    new_km = format.parse(oil_add_km.getText().toString()).longValue();
                } catch (NumberFormatException | ParseException e) {
                    new_km = 0;
                }

                try {
                    new_next_km = format.parse(oil_add_nextkm.getText().toString()).longValue();
                } catch (NumberFormatException | ParseException e) {
                    new_next_km = 0;
                }

                try {
                    new_lts = format.parse(oil_add_litros.getText().toString()).doubleValue();
                } catch (NumberFormatException | ParseException e) {
                    new_lts = 0;
                }

                ret = rootapp.getOilIntevalKM(oil_add_date_full, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));

                if (new_lts == 0) {
                    MeuCarroApplication.WarnUser(getView(), getString(R.string.o_volume_nao_pode, parser.format(new_lts)), Snackbar.LENGTH_LONG, true, menuItem);
                    return true;
                }

                if (ret[1] > 0 && ret[1] <= new_km) {
                    MeuCarroApplication.WarnUser(getView(), getString(R.string.km_atual_maior, new_km, ret[0]), Snackbar.LENGTH_LONG, true, menuItem);
                    return true;
                }

                if (ret[0] >= new_km) {
                    MeuCarroApplication.WarnUser(getView(), getString(R.string.km_atual_menor, new_km, ret[0]), Snackbar.LENGTH_LONG, true, menuItem);
                    return true;
                }

                if (mParam1 > 0) {
                    if (!rootapp.updateOil(mParam1, oil_add_name.getText().toString(), new_price, new_km, new_next_km, oil_add_date_full, new_lts, oil_add_filter.isChecked(), 0))
                        return false;
                } else {
                    if (!rootapp.includeOil(oil_add_name.getText().toString(), new_price, new_km, new_next_km, oil_add_date_full, new_lts, oil_add_filter.isChecked(), 0))
                        return false;
                }
                actionMode.finish();
            }
            menuItem.setEnabled(true);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            replaceFragment(new OilCharge());
        }
    };

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
        Log.d(getClass().getSimpleName(), "********** onDateSet **************************");
        Log.d(getActivity().getClass().getSimpleName(), "YEAR=" + i + " MES=" + i2 + " DIA=" + i3);
        Calendar cal = new GregorianCalendar(i, i2, i3);
        String thisdate = android.text.format.DateFormat.getDateFormat(getActivity()).format(cal.getTime());

        TextInputEditText updatetxt = getActivity().findViewById(EditDateUsed);
        updatetxt.setText(thisdate);

        long[] ret;
        oil_add_time = getActivity().findViewById(R.id.oil_add_time);
        ret = rootapp.getOilIntevalKM(thisdate + " " + oil_add_time.getText().toString(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        hint_oil_add_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_oil_add_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
    }
}
