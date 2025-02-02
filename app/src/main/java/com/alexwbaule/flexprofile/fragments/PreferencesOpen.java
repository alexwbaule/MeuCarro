package com.alexwbaule.flexprofile.fragments;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceFragmentCompat;
import android.view.Menu;
import android.view.MenuInflater;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.dialogs.MakeBackup;
import com.alexwbaule.flexprofile.dialogs.MakeBackupFragment;
import com.alexwbaule.flexprofile.dialogs.MakeRestore;
import com.alexwbaule.flexprofile.dialogs.MakeRestoreFragment;


public class PreferencesOpen extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    OnSetChangePreferenceListener mCallback;

    public void updatePreference() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            findPreference("bkp_make").setEnabled(true);
            findPreference("bkp_restore").setEnabled(true);
        } else {
            findPreference("bkp_make").setEnabled(false);
            findPreference("bkp_restore").setEnabled(false);
        }
    }

    // Container Activity must implement this interface
    public interface OnSetChangePreferenceListener {
        void onChangePreferenceSet(boolean newmedia);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedpref = getPreferenceScreen().getSharedPreferences();

        String un_volume_tanque = sharedpref.getString("un_volume_tanque", "LTS");
        String un_volume_gnv = sharedpref.getString("un_volume_gnv", "MTS");
        String un_calculo_custo = sharedpref.getString("un_calculo_custo", "custo");
        String un_medida_dist = sharedpref.getString("un_medida_dist", "km");
        String un_limit_select = sharedpref.getString("un_limit_select", "0");

        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                findPreference("bkp_make").setEnabled(true);
                findPreference("bkp_restore").setEnabled(true);
            } else {
                findPreference("bkp_make").setEnabled(false);
                findPreference("bkp_restore").setEnabled(false);
            }
        }
        findPreference("bkp_make").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mCallback.onChangePreferenceSet(false);
                return true;
            }
        });
        findPreference("bkp_restore").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mCallback.onChangePreferenceSet(true);
                return true;
            }
        });
        setConsumos(sharedpref);

        if (un_medida_dist.equals("km")) {
            findPreference("un_medida_dist").setSummary(R.string.medida_km);
        } else if (un_medida_dist.equals("mi")) {
            findPreference("un_medida_dist").setSummary(R.string.medida_mi);
        }

        if (un_volume_tanque.equals("LTS")) {
            findPreference("un_volume_tanque").setSummary(R.string.vol_tanque_lts);
        } else if (un_volume_tanque.equals("GUS")) {
            findPreference("un_volume_tanque").setSummary(R.string.vol_tanque_gus);
        } else if (un_volume_tanque.equals("GUK")) {
            findPreference("un_volume_tanque").setSummary(R.string.vol_tanque_guk);
        }

        if (un_volume_gnv.equals("MTS")) {
            findPreference("un_volume_gnv").setSummary(R.string.vol_gnv_mt);
        } else if (un_volume_gnv.equals("YAD")) {
            findPreference("un_volume_gnv").setSummary(R.string.vol_gnv_jd);
        }

        if (un_calculo_custo.equals("custo")) {
            findPreference("un_calculo_custo").setSummary(R.string.calculo_type_custo);
        } else if (un_calculo_custo.equals("autonomia")) {
            findPreference("un_calculo_custo").setSummary(R.string.calculo_type_autonomia);
        }

        if (un_limit_select.equals("0")) {
            findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_full);
        } else if (un_limit_select.equals("5")) {
            findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_5);
        } else if (un_limit_select.equals("10")) {
            findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_10);
        } else if (un_limit_select.equals("15")) {
            findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_15);
        } else if (un_limit_select.equals("20")) {
            findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_20);
        }
        updatePreference();

        sharedpref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof MakeBackup){
            DialogFragment dialogFragment = MakeBackupFragment.newInstance("bkp_make");
            dialogFragment.setTargetFragment(this,0);
            dialogFragment.show(this.getParentFragmentManager(), "MAKEBACKUP");
        }else if (preference instanceof MakeRestore){
            DialogFragment dialogFragment = MakeRestoreFragment.newInstance("bkp_restore");
            dialogFragment.setTargetFragment(this,0);
            dialogFragment.show(this.getParentFragmentManager(), "MAKERESTORE");
        }else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean newmedia = false;

        if (key.equals("un_medida_dist")) {
            if (sharedPreferences.getString(key, "km").equals("km")) {
                findPreference("un_medida_dist").setSummary(R.string.medida_km);
            } else if (sharedPreferences.getString(key, "mi").equals("mi")) {
                findPreference("un_medida_dist").setSummary(R.string.medida_mi);
            }
            setConsumos(sharedPreferences);
        } else if (key.equals("un_consumo_gnv")) {
            setConsumos(sharedPreferences);
        } else if (key.equals("un_consumo_comb")) {
            setConsumos(sharedPreferences);
        } else if (key.equals("un_volume_tanque")) {
            if (sharedPreferences.getString(key, "LTS").equals("LTS")) {
                findPreference("un_volume_tanque").setSummary(R.string.vol_tanque_lts);
            } else if (sharedPreferences.getString(key, "GUS").equals("GUS")) {
                findPreference("un_volume_tanque").setSummary(R.string.vol_tanque_gus);
            } else if (sharedPreferences.getString(key, "GUK").equals("GUK")) {
                findPreference("un_volume_tanque").setSummary(R.string.vol_tanque_guk);
            }
            setConsumos(sharedPreferences);
        } else if (key.equals("un_volume_gnv")) {
            if (sharedPreferences.getString(key, "MTS").equals("MTS")) {
                findPreference("un_volume_gnv").setSummary(R.string.vol_gnv_mt);
            } else if (sharedPreferences.getString(key, "YAD").equals("YAD")) {
                findPreference("un_volume_gnv").setSummary(R.string.vol_gnv_jd);
            }
            setConsumos(sharedPreferences);
        } else if (key.equals("un_calculo_custo")) {
            if (sharedPreferences.getString(key, "un_calculo_custo").equals("custo")) {
                findPreference("un_calculo_custo").setSummary(R.string.calculo_type_custo);
            } else if (sharedPreferences.getString(key, "un_calculo_custo").equals("autonomia")) {
                findPreference("un_calculo_custo").setSummary(R.string.calculo_type_autonomia);
            }
        } else if (key.equals("un_limit_select")) {
            if (sharedPreferences.getString(key, "un_limit_select").equals("0")) {
                findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_full);
            } else if (sharedPreferences.getString(key, "un_limit_select").equals("5")) {
                findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_5);
            } else if (sharedPreferences.getString(key, "un_limit_select").equals("10")) {
                findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_10);
            } else if (sharedPreferences.getString(key, "un_limit_select").equals("15")) {
                findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_15);
            } else if (sharedPreferences.getString(key, "un_limit_select").equals("20")) {
                findPreference("un_limit_select").setSummary(R.string.calculo_media_consumo_20);
            }
            newmedia = true;
        }
        updatePreference();

        mCallback.onChangePreferenceSet(newmedia);
    }

    private void setConsumos(SharedPreferences sharedpref) {

        String un_consumo_gnv = sharedpref.getString("un_consumo_gnv", "kmm");
        String un_consumo_comb = sharedpref.getString("un_consumo_comb", "kml");
        String un_volume_tanque = sharedpref.getString("un_volume_tanque", "LTS");
        String un_volume_gnv = sharedpref.getString("un_volume_gnv", "MTS");
        String un_medida_dist = sharedpref.getString("un_medida_dist", "km");

        ListPreference consumo_comb = (ListPreference) findPreference("un_consumo_comb");
        ListPreference consumo_gnv = (ListPreference) findPreference("un_consumo_gnv");

        if (un_consumo_comb.equals("mpv")) {
            if (un_volume_tanque.equals("LTS") && un_medida_dist.equals("km")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_kml);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_kml);
            } else if (un_volume_tanque.equals("LTS") && un_medida_dist.equals("mi")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_mil);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_mil);
            } else if (!un_volume_tanque.equals("LTS") && un_medida_dist.equals("km")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_kmg);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_kmg);
            } else if (!un_volume_tanque.equals("LTS") && un_medida_dist.equals("mi")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_mig);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_mig);
            }
        } else if (un_consumo_comb.equals("v100m")) {
            if (un_volume_tanque.equals("LTS") && un_medida_dist.equals("km")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_kml);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_l100k);
            } else if (un_volume_tanque.equals("LTS") && un_medida_dist.equals("mi")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_mil);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_l100m);
            } else if (!un_volume_tanque.equals("LTS") && un_medida_dist.equals("km")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_kmg);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_g100k);
            } else if (!un_volume_tanque.equals("LTS") && un_medida_dist.equals("mi")) {
                consumo_comb.setEntries(R.array.consumo_comb_entry_mig);
                findPreference("un_consumo_comb").setSummary(R.string.consumo_g100m);
            }
        }

        if (un_consumo_gnv.equals("mpv")) {
            if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("km")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_kmm);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_kmm);
            } else if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("mi")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_mim);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_mim);
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("km")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_kmy);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_kmy);
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("mi")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_miy);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_miy);
            }
        } else if (un_consumo_gnv.equals("v100m")) {
            if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("km")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_kmm);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_m100k);
            } else if (un_volume_gnv.equals("MTS") && un_medida_dist.equals("mi")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_mim);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_m100m);
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("km")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_kmy);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_y100k);
            } else if (un_volume_gnv.equals("YAD") && un_medida_dist.equals("mi")) {
                consumo_gnv.setEntries(R.array.consumo_gnv_entry_miy);
                findPreference("un_consumo_gnv").setSummary(R.string.consumo_y100m);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnSetChangePreferenceListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnSetChangePreferenceListener");
        }
    }
}
