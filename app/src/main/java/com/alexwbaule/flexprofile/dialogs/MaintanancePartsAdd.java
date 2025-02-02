package com.alexwbaule.flexprofile.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.AutoCompletePartsAdapter;
import com.alexwbaule.flexprofile.adapter.SpinnerCateroryAdapter;
import com.alexwbaule.flexprofile.containers.Categories;
import com.alexwbaule.flexprofile.containers.MaintenanceParts;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by alex on 24/04/16.
 */
public class MaintanancePartsAdd extends AppCompatDialogFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler {
    private static final String ARG_PARAM2 = "EDTID";
    private Spinner part_category_add;
    private AutoCompleteTextView maintanance_add_desc;
    private TextInputEditText maintanance_add_qtde;
    private TextInputEditText maintanance_add_price;
    private TextView maintenace_add_subtotal;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private int EditUsed;


    onAddPart diagCallback;

    public interface onAddPart {
        void onAdd(MaintenanceParts parts);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.maps_style_day.AppTheme_NoActionBar);
        if (savedInstanceState != null) {
            EditUsed = savedInstanceState.getInt(ARG_PARAM2);
            Log.d(getClass().getSimpleName(), "RESTAURANDO EDIT");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AppCompatDialog(getActivity(), R.style.AppTheme_AppDialog_NoActionBar);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PARAM2, EditUsed);
        Log.d(getClass().getSimpleName(), "SALVANDO EDIT");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.each_add_parts, container, false);
        Toolbar toolbar = view.findViewById(R.id.dialog_toolbar);
        toolbar.inflateMenu(R.menu.save);
        toolbar.setTitle(R.string.nova_peca);
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_submenu_save:
                        NumberFormat parser = NumberFormat.getInstance();
                        parser.setMaximumFractionDigits(0);
                        DecimalFormat format = new DecimalFormat();
                        Categories ctg = (Categories) part_category_add.getSelectedItem();
                        String description = maintanance_add_desc.getText().toString();

                        double new_price = 0;
                        int new_qtde = 0;

                        try {
                            new_price = format.parse(maintanance_add_price.getText().toString()).doubleValue();
                        } catch (NumberFormatException | ParseException e) {
                            new_price = 0;
                        }
                        try {
                            new_qtde = parser.parse(maintanance_add_qtde.getText().toString()).intValue();
                        } catch (NumberFormatException | ParseException e) {

                        }
                        if (new_qtde == 0) {
                            MeuCarroApplication.WarnUser(getView(), rootapp.getString(R.string.quantidade_um), Snackbar.LENGTH_LONG, true, null);
                        } else if (description.isEmpty()) {
                            MeuCarroApplication.WarnUser(getView(), rootapp.getString(R.string.descritivo_vazio), Snackbar.LENGTH_LONG, true, null);
                        } else {
                            diagCallback.onAdd(new MaintenanceParts(
                                    maintanance_add_desc.getText().toString(),
                                    new_qtde,
                                    new_price,
                                    ctg.getId(),
                                    ctg.getImage(),
                                    0,
                                    ctg.getCategoria()));
                            getDialog().dismiss();
                        }
                        break;
                }
                return false;
            }
        });
        // Inflate a menu to be displayed in the toolbar

        ArrayList<Categories> data = rootapp.getCategories();
        part_category_add = view.findViewById(R.id.part_category_add);
        part_category_add.setAdapter(new SpinnerCateroryAdapter(getActivity(), R.layout.spinner_combustivel_add, data));

        maintanance_add_desc = view.findViewById(R.id.maintenance_add_desc);
        AutoCompletePartsAdapter adapter = new AutoCompletePartsAdapter(getActivity());
        maintanance_add_desc.setAdapter(adapter);
        maintanance_add_desc.setThreshold(3);

        maintanance_add_qtde = view.findViewById(R.id.maintenance_add_qtde);
        maintanance_add_price = view.findViewById(R.id.maintenance_add_price);

        maintanance_add_price.setTag(getString(R.string.simbolo_moeda));
        maintanance_add_price.setOnClickListener(meuclick);

        maintanance_add_qtde.setTag(rootapp.getString(R.string.qtde));
        maintanance_add_qtde.setOnClickListener(meuclick);

        maintenace_add_subtotal = view.findViewById(R.id.maintenace_add_subtotal);
        maintenace_add_subtotal.setText(getString(R.string.subtotal, getString(R.string.simbolo_moeda), "-"));

        return view;
    }

    View.OnClickListener meuclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            NumberPickerBuilder dpb = new NumberPickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setLabelText(v.getTag().toString())
                    .setTargetFragment(MaintanancePartsAdd.this)
                    .setPlusMinusVisibility(View.GONE)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            dpb.show();
        }
    };

    private void updateSubtotal() {
        TextInputEditText qtde = getDialog().findViewById(R.id.maintenance_add_qtde);
        TextInputEditText price = getDialog().findViewById(R.id.maintenance_add_price);
        TextView ptotal = getDialog().findViewById(R.id.maintenace_add_subtotal);

        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(2);
        parser.setMinimumFractionDigits(2);

        DecimalFormat format = new DecimalFormat();

        double new_price = 0;
        int new_qtde = 0;

        try {
            new_price = format.parse(price.getText().toString()).doubleValue();
        } catch (NumberFormatException | ParseException e) {
            new_price = 0;
        }
        try {
            new_qtde = parser.parse(qtde.getText().toString()).intValue();
        } catch (NumberFormatException | ParseException e) {

        }

        try {
            double total = new_price * new_qtde;

            ptotal.setText(getString(R.string.subtotal, getString(R.string.simbolo_moeda), parser.format(total)));
        } catch (Exception e) {

        }
    }

    @Override
    public void onDialogNumberSet(int i, int i2, double v, boolean b, double v2) {
        NumberFormat parser = NumberFormat.getInstance();
        if (EditUsed == R.id.maintenance_add_qtde) {
            parser.setMaximumFractionDigits(0);
            parser.setMinimumFractionDigits(0);
        } else {
            parser.setMaximumFractionDigits(2);
            parser.setMinimumFractionDigits(2);
        }
        TextInputEditText updatetxt = getDialog().findViewById(EditUsed);
        updatetxt.setText(parser.format(v2));
        updateSubtotal();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            diagCallback = (onAddPart) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onAddPart");
        }
    }
}
