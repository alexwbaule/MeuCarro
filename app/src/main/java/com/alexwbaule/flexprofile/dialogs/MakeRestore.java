package com.alexwbaule.flexprofile.dialogs;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.DialogPreference;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.alexwbaule.flexprofile.R;


/**
 * Created by alex on 04/09/15.
 */
public class MakeRestore extends DialogPreference {
    public MakeRestore(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        prepareDialog();
    }

    public MakeRestore(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareDialog();
    }

    public MakeRestore(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareDialog();
    }

    public MakeRestore(Context context) {
        super(context);
        prepareDialog();
    }


    private void prepareDialog(){
        int currentNightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int color = Color.BLACK;
        Drawable myIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_restore);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            color = Color.WHITE;
        }
        setTint(myIcon, color);
        setDialogTitle(R.string.Restore);
        setPositiveButtonText(null);
        setDialogIcon(myIcon);
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_backup_restore;
    }
}
