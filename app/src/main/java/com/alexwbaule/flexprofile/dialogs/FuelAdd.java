package com.alexwbaule.flexprofile.dialogs;


import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.FuelPreferenceAdapter;

/**
 * Created by alex on 26/02/17.
 */

public class FuelAdd extends PreferenceDialogFragmentCompat implements DialogColorPicker.OnAmbilWarnaListener {
    private final Context ctx;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private ListView list;
    private ImageView backgroundImage;
    private EditText name;

    public FuelAdd(Context context, AttributeSet attrs) {
        ctx = context;
    }

    @Override
    protected View onCreateDialogView(Context context) {
        getPreference().setDialogLayoutResource(R.layout.preference_add_fuel);
        return super.onCreateDialogView(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        list = view.findViewById(R.id.preference_fuel_list);
        list.setAdapter(new FuelPreferenceAdapter(getContext(), android.R.layout.simple_list_item_1, rootapp.getCombustiveis()));

        backgroundImage = view.findViewById(R.id.new_combustivel_icon);
        name = view.findViewById(R.id.new_fuel_name);

        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogColorPicker diag = new DialogColorPicker(getContext(), Color.LTGRAY, FuelAdd.this);
                diag.show();
            }
        });

        super.onBindDialogView(view);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @Override
    protected void onPrepareDialogBuilder(androidx.appcompat.app.AlertDialog.Builder builder) {
        int currentNightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int color = Color.BLACK;
        Drawable myIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_comb);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            color = Color.WHITE;
        }
        setTint(myIcon, color);
        builder.setTitle(R.string.comb_list);
        builder.setPositiveButton(null, null);
        builder.setIcon(myIcon);
        super.onPrepareDialogBuilder(builder);
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    @Override
    public void onCancel(DialogColorPicker dialog) {

    }

    @Override
    public void onOk(DialogColorPicker dialog, int color) {
        char n = name.getText().charAt(0);
        Drawable newDraw = rootapp.createFuelIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_paint), n, color);
        backgroundImage.setImageDrawable(newDraw);
        backgroundImage.invalidate();
    }
}
