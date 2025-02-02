package com.alexwbaule.flexprofile.fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;

import com.alexwbaule.flexprofile.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alex on 07/07/15.
 */
public class BaseFragment extends Fragment {

    public AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }


    public void replaceFragment(Fragment frag) {
        Toolbar tb = getAppCompatActivity().findViewById(R.id.main_toolbar);
        tb.getMenu().clear();
        //tb.setSubtitle(null);
        Log.d(getClass().getSimpleName(), "replaceFragment Yeap");
        getFragmentManager().popBackStackImmediate();
        getFragmentManager().beginTransaction().replace(R.id.nav_drawer_container, frag, "TAG").commit();
    }

    public void updateActionModeTitle(ActionMode actionMode, int size) {
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(size));
            if (size == 1) {
                actionMode.getMenu().setGroupVisible(R.id.menu_group, true);
            } else if (size > 1) {
                actionMode.getMenu().setGroupVisible(R.id.menu_group, false);
            }
        }
    }

    protected Calendar parseDate(String data) {
        Log.d(getClass().getSimpleName(), "Parsing -> " + data);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        Date date = null;
        try {
            date = formatter.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cc = Calendar.getInstance();
        if (date != null)
            cc.setTime(date);

        Log.d(getClass().getSimpleName(), "RESULT --> " + cc.getTime().toString());
        return cc;
    }

    public void onCoachMark() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.helper);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        View masterView = dialog.findViewById(R.id.coach_mark_master_view);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        Log.d(getClass().getSimpleName(), "onresume Fragment");
        ScrollView t = getActivity().findViewById(R.id.scrollView);
        if (t != null) {
            ViewCompat.requestApplyInsets(t);
        }
        super.onResume();
    }
}
