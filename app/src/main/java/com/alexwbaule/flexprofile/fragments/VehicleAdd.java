package com.alexwbaule.flexprofile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.AutoCompleteCarModelAdapter;
import com.alexwbaule.flexprofile.adapter.CarIconAdapter;
import com.alexwbaule.flexprofile.adapter.SpinnerCombustivelAdapter;
import com.alexwbaule.flexprofile.containers.CarIcon;
import com.alexwbaule.flexprofile.containers.Combustivel;
import com.alexwbaule.flexprofile.containers.Consumos;
import com.alexwbaule.flexprofile.domain.SQLiteHelper;
import com.alexwbaule.flexprofile.utils.DbBitmapUtility;
import com.alexwbaule.flexprofile.utils.PreferencesProcessed;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;


/**
 * Created by alex on 11/07/15.
 */
public class VehicleAdd extends BaseFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler {
    private static final String ARG_PARAM1 = "CARID";
    private static final String ARG_PARAM2 = "EDTID";
    private final String CLASSNAME = "VehicleAdd";
    private int EditUsed;
    private long defaultId;

    private long mParam1;

    private AutoCompleteTextView txt_name;
    private TextView txt_cons_01;
    private TextView txt_cons_02;
    private TextView txt_cons_03;
    private TextView txt_cons_04;
    private TextView txt_ref_gnv;
    private TextView txt_ref_lts;
    private SwitchCompat default_star;

    private Spinner spn_comb_01;
    private Spinner spn_comb_02;
    private Spinner spn_comb_03;
    private Spinner spn_comb_04;

    private Spinner spn_iconid;
    private CarIconAdapter iconAdapter;

    private RoundedImageView car_photo;
    private Bitmap car_image_bitmap;
    private SpinnerCombustivelAdapter spn1_data;
    private SpinnerCombustivelAdapter spn2_data;
    private SpinnerCombustivelAdapter spn3_data;
    private SpinnerCombustivelAdapter spn4_data;

    private int last_ps1;
    private int last_ps2;
    private int last_ps3;
    private int last_ps4;

    private TextInputLayout hint_car_add_01kml;
    private TextInputLayout hint_car_add_02kml;
    private TextInputLayout hint_car_add_03kml;
    private TextInputLayout hint_car_add_04kml;
    private TextInputLayout hint_car_add_lts;
    private TextInputLayout hint_car_add_gnv;
    private SwitchCompat btn_default;
    private ActionMode mActionMode;

    private int[] pointers_gnv = {0, 0, 0, 0};
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    OnSetDefaultCarListener mCallback;

    private static final int SELECT_PICTURE = 390;

    // Container Activity must implement this interface
    public interface OnSetDefaultCarListener {
        void onDefaultCarSet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }
        if (savedInstanceState != null) {
            EditUsed = savedInstanceState.getInt(ARG_PARAM2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PARAM2, EditUsed);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri selectedImage = data.getData();
            car_image_bitmap = rootapp.getScaledBitmap(selectedImage, 200, 200);
            car_photo.setImageBitmap(car_image_bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);


        ArrayList<CarIcon> iconsname = new ArrayList<CarIcon>();
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_sport), "ic_spin_sport", R.drawable.ic_spin_sport, 0));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_sedan), "ic_spin_sedan", R.drawable.ic_spin_sedan, 1));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_hatch), "ic_spin_hatch", R.drawable.ic_spin_hatch, 2));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_sw), "ic_spin_sw", R.drawable.ic_spin_sw, 3));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_suv), "ic_spin_suv", R.drawable.ic_spin_suv, 4));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_conversivel), "ic_spin_conv", R.drawable.ic_spin_conv, 5));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_utilitario), "ic_spin_util", R.drawable.ic_spin_util, 6));

        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_moto_sport), "ic_spin_mtsport", R.drawable.ic_spin_mtsport, 7));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_chooper), "ic_spin_chop", R.drawable.ic_spin_chop, 8));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_scooter), "ic_spin_scot", R.drawable.ic_spin_scot, 9));
        iconsname.add(new CarIcon(rootapp.getString(R.string.carro_cross), "ic_spin_cross", R.drawable.ic_spin_cross, 10));


        View rootView = inflater.inflate(R.layout.fragment_vehicle_add, container, false);

        mActionMode = getAppCompatActivity().startSupportActionMode(mActionModeCallback);
        mActionMode.setTitle(rootapp.getString(R.string.novo_veiculo));

        spn_comb_01 = rootView.findViewById(R.id.new_01comb);
        spn_comb_02 = rootView.findViewById(R.id.new_02comb);
        spn_comb_03 = rootView.findViewById(R.id.new_03comb);
        spn_comb_04 = rootView.findViewById(R.id.new_04comb);
        spn_iconid = rootView.findViewById(R.id.new_iconid);

        iconAdapter = new CarIconAdapter(getActivity(), R.layout.spinner_combustivel_add, iconsname);

        spn_iconid.setAdapter(iconAdapter);


        car_photo = rootView.findViewById(R.id.car_photo);

        car_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        ArrayList<Combustivel> data = rootapp.getCombustiveis();
        ArrayList<Combustivel> data1 = rootapp.getCombustiveis();
        ArrayList<Combustivel> data2 = rootapp.getCombustiveis();
        ArrayList<Combustivel> data3 = rootapp.getCombustiveis();

        last_ps1 = 0;
        last_ps2 = 0;
        last_ps3 = 0;
        last_ps4 = 0;

        spn1_data = new SpinnerCombustivelAdapter(getActivity(), R.layout.spinner_combustivel_add, data);
        spn2_data = new SpinnerCombustivelAdapter(getActivity(), R.layout.spinner_combustivel_add, data1);
        spn3_data = new SpinnerCombustivelAdapter(getActivity(), R.layout.spinner_combustivel_add, data2);
        spn4_data = new SpinnerCombustivelAdapter(getActivity(), R.layout.spinner_combustivel_add, data3);

        spn_comb_01.setAdapter(spn1_data);
        spn_comb_02.setAdapter(spn2_data);
        spn_comb_03.setAdapter(spn3_data);
        spn_comb_04.setAdapter(spn4_data);

        spn_comb_01.setOnItemSelectedListener(spinSelect);
        spn_comb_02.setOnItemSelectedListener(spinSelect);
        spn_comb_03.setOnItemSelectedListener(spinSelect);
        spn_comb_04.setOnItemSelectedListener(spinSelect);

        ImageView icone = rootView.findViewById(R.id.img_ref_gnv);

        PreferencesProcessed valores = new PreferencesProcessed(getActivity());

        hint_car_add_lts = rootView.findViewById(R.id.hint_car_add_lts);
        txt_ref_lts = rootView.findViewById(R.id.ref_lts);
        txt_ref_lts.setTag(valores.getVolumeTanque());
        hint_car_add_lts.setHint(valores.getVolumeTanque());
        txt_ref_lts.setOnClickListener(meuclick);

        hint_car_add_gnv = rootView.findViewById(R.id.hint_car_add_gnv);
        txt_ref_gnv = rootView.findViewById(R.id.ref_gnv);
        txt_ref_gnv.setTag(valores.getVolumeGNV());
        hint_car_add_gnv.setHint(valores.getVolumeGNV());
        txt_ref_gnv.setOnClickListener(meuclick);

        hint_car_add_01kml = rootView.findViewById(R.id.hint_car_add_01kml);
        txt_cons_01 = rootView.findViewById(R.id.new_01kml);
        txt_cons_01.setTag(valores.getMedidor());
        hint_car_add_01kml.setHint(valores.getMedidor());
        txt_cons_01.setOnClickListener(meuclick);

        hint_car_add_02kml = rootView.findViewById(R.id.hint_car_add_02kml);
        txt_cons_02 = rootView.findViewById(R.id.new_02kml);
        txt_cons_02.setTag(valores.getMedidor());
        hint_car_add_02kml.setHint(valores.getMedidor());
        txt_cons_02.setOnClickListener(meuclick);

        hint_car_add_03kml = rootView.findViewById(R.id.hint_car_add_03kml);
        txt_cons_03 = rootView.findViewById(R.id.new_03kml);
        txt_cons_03.setTag(valores.getMedidor());
        hint_car_add_03kml.setHint(valores.getMedidor());
        txt_cons_03.setOnClickListener(meuclick);

        hint_car_add_04kml = rootView.findViewById(R.id.hint_car_add_04kml);
        txt_cons_04 = rootView.findViewById(R.id.new_04kml);
        txt_cons_04.setTag(valores.getMedidor());
        hint_car_add_04kml.setHint(valores.getMedidor());
        txt_cons_04.setOnClickListener(meuclick);

        txt_name = rootView.findViewById(R.id.new_carname);

        AutoCompleteCarModelAdapter adapter = new AutoCompleteCarModelAdapter(getActivity());
        txt_name.setAdapter(adapter);
        txt_name.setThreshold(3);

        default_star = rootView.findViewById(R.id.add_default);

        default_star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText(R.string.ativo);
                } else {
                    buttonView.setText(R.string.inativo);
                }
            }
        });


        icone.setAlpha(100);
        txt_ref_gnv.setEnabled(false);

        if (mParam1 > 0) {
            Cursor cursor = rootapp.fetchCursorOneCar(mParam1);
            mActionMode.setTitle(rootapp.getString(R.string.editar_veiculo));
            this.defaultId = rootapp.getDefaultCar();


            if (cursor.moveToFirst()) {
                txt_name.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME)));

                if (cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_GASOLINA)) != null) {
                    txt_cons_01.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_GASOLINA))));
                    spn_comb_01.setSelection(1, true);
                }
                if (cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ETANOL)) != null) {
                    txt_cons_02.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_ETANOL))));
                    spn_comb_02.setSelection(2, true);
                }
                if (cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_GNV)) != null) {
                    txt_cons_03.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_GNV))));
                    spn_comb_03.setSelection(3, true);
                }
                if (cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_DIESEL)) != null) {
                    txt_cons_04.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_DIESEL))));
                    spn_comb_04.setSelection(4, true);
                }

                if (cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TQ)) != null) {
                    txt_ref_lts.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ))));
                }

                if (cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TQ_GNV)) != null) {
                    txt_ref_gnv.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ_GNV))));
                }

                if (cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID)) == defaultId) {
                    default_star.setText(R.string.ativo);
                    default_star.setChecked(true);
                } else {
                    default_star.setText(R.string.ativo);
                    default_star.setChecked(false);
                }

                int seliccar = 0;
                for (CarIcon carIcon : iconsname) {
                    if (carIcon.getImage().equals(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ICON_ID)))) {
                        seliccar = carIcon.getId();
                    }
                }
                spn_iconid.setSelection(seliccar, true);
                car_photo.setImageBitmap(DbBitmapUtility.readbitmap(getContext(), String.valueOf(mParam1)));
            }
            cursor.close();
        }
        return rootView;
    }

    View.OnClickListener meuclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PreferencesProcessed valores = new PreferencesProcessed(getActivity());
            EditUsed = v.getId();
            TextView txt = (TextView) v;
            NumberPickerBuilder dpb = new NumberPickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setLabelText(txt.getTag().toString())
                    .setTargetFragment(VehicleAdd.this)
                    .setPlusMinusVisibility(View.GONE)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            dpb.show();
        }
    };

    @Override
    public void onDialogNumberSet(int i, int i1, double v, boolean b, double v1) {
        NumberFormat format = new DecimalFormat();
        TextView updatetxt = getAppCompatActivity().findViewById(EditUsed);
        updatetxt.setText(format.format(v1));
    }

    private AdapterView.OnItemSelectedListener spinSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PreferencesProcessed valores = new PreferencesProcessed(getActivity());

            txt_cons_01 = getActivity().findViewById(R.id.new_01kml);
            txt_cons_02 = getActivity().findViewById(R.id.new_02kml);
            txt_cons_03 = getActivity().findViewById(R.id.new_03kml);
            txt_cons_04 = getActivity().findViewById(R.id.new_04kml);

            hint_car_add_01kml = getActivity().findViewById(R.id.hint_car_add_01kml);
            hint_car_add_02kml = getActivity().findViewById(R.id.hint_car_add_02kml);
            hint_car_add_03kml = getActivity().findViewById(R.id.hint_car_add_03kml);
            hint_car_add_04kml = getActivity().findViewById(R.id.hint_car_add_04kml);

            Log.d(getClass().getSimpleName(), "SPINNER POSITION=" + position + " ID=" + id);

            switch (parent.getId()) {
                case R.id.new_01comb:
                    pointers_gnv[0] = 0;
                    txt_cons_01.setTag(valores.getMedidor());
                    hint_car_add_01kml.setHint(valores.getMedidor());
                    if (position == 3) {
                        hint_car_add_01kml.setHint(valores.getMedidorGNV());
                        txt_cons_01.setTag(valores.getMedidorGNV());
                        pointers_gnv[0] = 1;
                    }
                    if (position > 0 && last_ps1 == 0) {
                        spn2_data.remove(position);
                        spn3_data.remove(position);
                        spn4_data.remove(position);
                        last_ps1 = position;
                    } else if (position >= 0 && last_ps1 > 0) {
                        spn2_data.add(last_ps1);
                        spn3_data.add(last_ps1);
                        spn4_data.add(last_ps1);
                        if (position > 0) {
                            spn2_data.remove(position);
                            spn3_data.remove(position);
                            spn4_data.remove(position);
                        }
                        last_ps1 = position;
                    }
                    break;
                case R.id.new_02comb:
                    pointers_gnv[1] = 0;
                    txt_cons_02.setTag(valores.getMedidor());
                    hint_car_add_02kml.setHint(valores.getMedidor());
                    if (position == 3) {
                        hint_car_add_02kml.setHint(valores.getMedidorGNV());
                        txt_cons_02.setTag(valores.getMedidorGNV());
                        pointers_gnv[1] = 1;
                    }
                    if (position > 0 && last_ps2 == 0) {
                        spn1_data.remove(position);
                        spn3_data.remove(position);
                        spn4_data.remove(position);
                        last_ps2 = position;
                    } else if (position >= 0 && last_ps2 > 0) {
                        spn1_data.add(last_ps2);
                        spn3_data.add(last_ps2);
                        spn4_data.add(last_ps2);
                        if (position > 0) {
                            spn1_data.remove(position);
                            spn3_data.remove(position);
                            spn4_data.remove(position);
                        }
                        last_ps2 = position;
                    }
                    break;
                case R.id.new_03comb:
                    pointers_gnv[2] = 0;
                    txt_cons_03.setTag(valores.getMedidor());
                    hint_car_add_03kml.setHint(valores.getMedidor());
                    if (position == 3) {
                        hint_car_add_03kml.setHint(valores.getMedidorGNV());
                        txt_cons_03.setTag(valores.getMedidorGNV());
                        pointers_gnv[2] = 1;
                    }
                    if (position > 0 && last_ps3 == 0) {
                        spn1_data.remove(position);
                        spn2_data.remove(position);
                        spn4_data.remove(position);
                        last_ps3 = position;
                    } else if (position >= 0 && last_ps3 > 0) {
                        spn1_data.add(last_ps3);
                        spn2_data.add(last_ps3);
                        spn4_data.add(last_ps3);
                        if (position > 0) {
                            spn1_data.remove(position);
                            spn2_data.remove(position);
                            spn4_data.remove(position);
                        }
                        last_ps3 = position;
                    }
                    break;
                case R.id.new_04comb:
                    pointers_gnv[3] = 0;
                    txt_cons_04.setTag(valores.getMedidor());
                    hint_car_add_04kml.setHint(valores.getMedidor());
                    if (position == 3) {
                        hint_car_add_04kml.setHint(valores.getMedidorGNV());
                        txt_cons_04.setTag(valores.getMedidorGNV());
                        pointers_gnv[3] = 1;
                    }
                    if (position > 0 && last_ps4 == 0) {
                        spn1_data.remove(position);
                        spn2_data.remove(position);
                        spn3_data.remove(position);
                        last_ps4 = position;
                    } else if (position >= 0 && last_ps4 > 0) {
                        spn1_data.add(last_ps4);
                        spn2_data.add(last_ps4);
                        spn3_data.add(last_ps4);
                        if (position > 0) {
                            spn1_data.remove(position);
                            spn2_data.remove(position);
                            spn3_data.remove(position);
                        }
                        last_ps4 = position;
                    }
                    break;
            }

            for (int i = 0; i < pointers_gnv.length; i++) {
                if (pointers_gnv[i] == 1) {
                    //achei um habilitado, entao true e saio do laco.
                    setGnvVisibility(true);
                    break;
                }
                setGnvVisibility(false);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void setGnvVisibility(boolean t) {
        ImageView icone = getActivity().findViewById(R.id.img_ref_gnv);
        TextView txt = getActivity().findViewById(R.id.ref_gnv);

        if (t) {
            icone.setAlpha(255);
            txt.setEnabled(true);
        } else {
            icone.setAlpha(100);
            txt.setEnabled(false);
        }
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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the user exits the action mode
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.save, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            replaceFragment(new Vehicle());
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_submenu_cancel) {
                mode.finish();
            } else {
                item.setEnabled(false);
                double new_lts = 0;
                double new_lts_gnv = 0;

                DecimalFormat format = new DecimalFormat();
                boolean hasGnv = false;
                boolean hasOther = false;
                txt_name = getActivity().findViewById(R.id.new_carname);

                txt_cons_01 = getActivity().findViewById(R.id.new_01kml);
                txt_cons_02 = getActivity().findViewById(R.id.new_02kml);
                txt_cons_03 = getActivity().findViewById(R.id.new_03kml);
                txt_cons_04 = getActivity().findViewById(R.id.new_04kml);

                spn_comb_01 = getActivity().findViewById(R.id.new_01comb);
                spn_comb_02 = getActivity().findViewById(R.id.new_02comb);
                spn_comb_03 = getActivity().findViewById(R.id.new_03comb);
                spn_comb_04 = getActivity().findViewById(R.id.new_04comb);

                spn_iconid = getActivity().findViewById(R.id.new_iconid);

                txt_ref_lts = getActivity().findViewById(R.id.ref_lts);
                txt_ref_gnv = getActivity().findViewById(R.id.ref_gnv);

                car_photo = getActivity().findViewById(R.id.car_photo);

                btn_default = getActivity().findViewById(R.id.add_default);

                ArrayList<Consumos> consumos = new ArrayList<Consumos>();

                if (spn_comb_01.getSelectedItemPosition() > 0) {
                    Combustivel comb = (Combustivel) spn_comb_01.getSelectedItem();
                    consumos.add(new Consumos(comb.getId(), txt_cons_01.getText().toString()));
                    if (comb.getIntImage() == R.drawable.ic_menu_gnv) {
                        hasGnv = true;
                    } else {
                        hasOther = true;
                    }
                    if (txt_cons_01.getText().toString().isEmpty()) {
                        MeuCarroApplication.WarnUser(getView(), R.string.primeiro_consumo_nao_vazio, Snackbar.LENGTH_LONG, true, item);
                        return true;
                    }
                }
                if (spn_comb_02.getSelectedItemPosition() > 0) {
                    Combustivel comb = (Combustivel) spn_comb_02.getSelectedItem();
                    consumos.add(new Consumos(comb.getId(), txt_cons_02.getText().toString()));
                    if (comb.getIntImage() == R.drawable.ic_menu_gnv) {
                        hasGnv = true;
                    } else {
                        hasOther = true;
                    }
                    if (txt_cons_02.getText().toString().isEmpty()) {
                        MeuCarroApplication.WarnUser(getView(), R.string.segundo_consumo_nao_vazio, Snackbar.LENGTH_LONG, true, item);
                        return true;
                    }
                }
                if (spn_comb_03.getSelectedItemPosition() > 0) {
                    Combustivel comb = (Combustivel) spn_comb_03.getSelectedItem();
                    consumos.add(new Consumos(comb.getId(), txt_cons_03.getText().toString()));
                    if (comb.getIntImage() == R.drawable.ic_menu_gnv) {
                        hasGnv = true;
                    } else {
                        hasOther = true;
                    }
                    if (txt_cons_03.getText().toString().isEmpty()) {
                        MeuCarroApplication.WarnUser(getView(), R.string.terceiro_consumo_nao_vazio, Snackbar.LENGTH_LONG, true, item);
                        return true;
                    }
                }
                if (spn_comb_04.getSelectedItemPosition() > 0) {
                    Combustivel comb = (Combustivel) spn_comb_04.getSelectedItem();
                    consumos.add(new Consumos(comb.getId(), txt_cons_04.getText().toString()));
                    if (comb.getIntImage() == R.drawable.ic_menu_gnv) {
                        hasGnv = true;
                    } else {
                        hasOther = true;
                    }
                    if (txt_cons_04.getText().toString().isEmpty()) {
                        MeuCarroApplication.WarnUser(getView(), R.string.quarto_consumo_nao_vazio, Snackbar.LENGTH_LONG, true, item);
                        return true;
                    }
                }

                CarIcon icoid = (CarIcon) spn_iconid.getSelectedItem();


                if (txt_name.getText().toString().isEmpty()) {
                    MeuCarroApplication.WarnUser(getView(), R.string.carro_nao_vazio, Snackbar.LENGTH_LONG, true, item);
                    return true;
                }

                if (consumos.isEmpty()) {
                    MeuCarroApplication.WarnUser(getView(), R.string.selecione_ao_menos_um_combustivel, Snackbar.LENGTH_LONG, true, item);
                    return true;
                }

                try {
                    new_lts = format.parse(txt_ref_lts.getText().toString()).doubleValue();
                } catch (NumberFormatException | ParseException e) {
                    new_lts = 0;
                }

                if (new_lts == 0 && hasOther) {
                    MeuCarroApplication.WarnUser(getView(), R.string.indique_volume_tanque, Snackbar.LENGTH_LONG, true, item);
                    return true;
                }

                try {
                    new_lts_gnv = format.parse(txt_ref_gnv.getText().toString()).doubleValue();
                } catch (NumberFormatException | ParseException e) {
                    new_lts_gnv = 0;
                }

                if (new_lts_gnv == 0 && hasGnv) {
                    MeuCarroApplication.WarnUser(getView(), R.string.indique_volume_gnv, Snackbar.LENGTH_LONG, true, item);
                    return true;
                }

                if (mParam1 > 0) {
                    if (rootapp.updateCar(mParam1, btn_default.isChecked(), txt_name.getText().toString(), new_lts, new_lts_gnv, icoid.getImage(), car_image_bitmap)) {
                        rootapp.updateManualConsumo(mParam1, consumos);
                    } else {
                        return false;
                    }
                } else {
                    long id = rootapp.createCar(getView(), btn_default.isChecked(), txt_name.getText().toString(), new_lts, new_lts_gnv, icoid.getImage(), car_image_bitmap);
                    if (id > 0) {
                        rootapp.createConsumo(id, consumos);
                    } else {
                        return false;
                    }
                }
                mCallback.onDefaultCarSet();
                mode.finish();
            }
            item.setEnabled(true);
            return true;
        }
    };
}
