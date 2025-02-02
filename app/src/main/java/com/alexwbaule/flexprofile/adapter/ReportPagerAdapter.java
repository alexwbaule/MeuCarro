package com.alexwbaule.flexprofile.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.alexwbaule.flexprofile.reports.Costs;
import com.alexwbaule.flexprofile.reports.FuelConsuptionLineChart;
import com.alexwbaule.flexprofile.reports.FuelPriceLineChart;
import com.alexwbaule.flexprofile.reports.FuelVolumeLineChart;
import com.alexwbaule.flexprofile.reports.FullFuelBarChart;
import com.alexwbaule.flexprofile.reports.QtdeByFuel;
import com.alexwbaule.flexprofile.reports.TravelDistanceLineChart;

/**
 * Created by alex on 25/07/15.
 */


public class ReportPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 7;
    private String[] tabTitles = new String[]{"Custos", "Abastecimento", "Distancia", "Volume", "Consumo", "Valor", "Distribuição"};
    private Context context;


    public ReportPagerAdapter(FragmentManager fm, Context ct) {
        super(fm);
        this.context = ct;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new Costs();
                break;
            case 1:
                frag = new FullFuelBarChart();
                break;
            case 2:
                frag = new TravelDistanceLineChart();
                break;
            case 3:
                frag = new FuelVolumeLineChart();
                break;
            case 4:
                frag = new FuelConsuptionLineChart();
                break;
            case 5:
                frag = new FuelPriceLineChart();
                break;
            case 6:
                frag = new QtdeByFuel();
                break;
        }
        return frag;
    }

    @Override
    public int getItemPosition(Object object) {
        Fragment f = (Fragment) object;
        if (f instanceof FullFuelBarChart) {
            ((FullFuelBarChart) f).update();
        } else if (f instanceof Costs) {
            ((Costs) f).update();
        } else if (f instanceof TravelDistanceLineChart) {
            ((TravelDistanceLineChart) f).update();
        } else if (f instanceof FuelVolumeLineChart) {
            ((FuelVolumeLineChart) f).update();
        } else if (f instanceof FuelConsuptionLineChart) {
            ((FuelConsuptionLineChart) f).update();
        } else if (f instanceof FuelPriceLineChart) {
            ((FuelPriceLineChart) f).update();
        } else if (f instanceof QtdeByFuel) {
            ((QtdeByFuel) f).update();
        }
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
