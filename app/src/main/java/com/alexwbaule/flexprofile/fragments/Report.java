package com.alexwbaule.flexprofile.fragments;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.ReportPagerAdapter;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by alex on 17/07/15.
 */

public class Report extends BaseFragment implements CalendarDatePickerDialog.OnDateSetListener {
    private final String CLASSNAME = "Report";
    private CalendarDatePickerDialog calendar;
    private TextInputEditText report_ini_date;
    private TextInputEditText report_end_date;
    private Button report_generate;
    private ViewPager viewPager;
    private TabLayout tbl;
    private int EditDateUsed;

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppBarLayout bar = getAppCompatActivity().findViewById(R.id.main_appbar);
            if (bar != null) {
                bar.setElevation(0);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppBarLayout bar = getAppCompatActivity().findViewById(R.id.main_appbar);
            if (bar != null) {
                bar.setElevation(getResources().getDimension(R.dimen.appbar_elevation));
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        //Toolbar toolbarBottom = (Toolbar) rootView.findViewById(R.id.toolbar_bottom);
        Date currentDate = Calendar.getInstance().getTime();
        Calendar d = Calendar.getInstance();
        d.add(Calendar.DAY_OF_MONTH, -30);
        Date lastseven = d.getTime();
        String thisdate = android.text.format.DateFormat.getDateFormat(getActivity()).format(currentDate);
        String last7 = android.text.format.DateFormat.getDateFormat(getActivity()).format(lastseven);

        report_ini_date = rootView.findViewById(R.id.report_ini_date);
        report_end_date = rootView.findViewById(R.id.report_end_date);
        report_end_date.setText(thisdate);
        report_ini_date.setText(last7);

        report_generate = rootView.findViewById(R.id.report_generate);

        report_generate.setOnClickListener(newreport);
        report_ini_date.setOnClickListener(opencalendar);
        report_end_date.setOnClickListener(opencalendar);

        viewPager = rootView.findViewById(R.id.report_pager);
        viewPager.setAdapter(new ReportPagerAdapter(getFragmentManager(), getActivity()));

        tbl = rootView.findViewById(R.id.tab_layout_report);
        tbl.setTabGravity(TabLayout.GRAVITY_CENTER);
        tbl.setTabMode(TabLayout.MODE_SCROLLABLE);


        tbl.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ReportPagerAdapter(getFragmentManager(), getActivity()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tbl));
        tbl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //tbl.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tbl));

        Log.d(getClass().getSimpleName(), "CREATE VIEW");

        return rootView;
    }

    private View.OnClickListener newreport = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    };

    private View.OnClickListener opencalendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextInputEditText report_used = null;
            EditDateUsed = v.getId();
            Calendar teste = parseDate(((TextInputEditText) v).getText().toString());

            calendar = CalendarDatePickerDialog.newInstance(Report.this, teste.get(Calendar.YEAR), teste.get(Calendar.MONTH), teste.get(Calendar.DAY_OF_MONTH));
            calendar.setYearRange(Calendar.getInstance().get(Calendar.YEAR) - 10, Calendar.getInstance().get(Calendar.YEAR));
            calendar.setTargetFragment(Report.this, 0);
            calendar.setMaxDate(Calendar.getInstance());

            if (EditDateUsed == R.id.report_ini_date) {
                report_used = getActivity().findViewById(R.id.report_end_date);
                calendar.setMaxDate(parseDate(report_used.getText().toString()));
                Log.d(getClass().getSimpleName(), "SET MAX DATE");
            } else if (EditDateUsed == R.id.report_end_date) {
                report_used = getActivity().findViewById(R.id.report_ini_date);
                calendar.setMinDate(parseDate(report_used.getText().toString()));
                Log.d(getClass().getSimpleName(), "SET MIN DATE");

            }
            calendar.show(getFragmentManager(), "Calendar");
        }
    };

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
        Log.d(getClass().getSimpleName(), "********** onDateSet **************************");
        Calendar cal = new GregorianCalendar(i, i2, i3);
        String thisdate = android.text.format.DateFormat.getDateFormat(getActivity()).format(cal.getTime());
        TextInputEditText updatetxt = getActivity().findViewById(EditDateUsed);
        updatetxt.setText(thisdate);
    }
}
