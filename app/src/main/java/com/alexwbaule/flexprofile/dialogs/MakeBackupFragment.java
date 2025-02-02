package com.alexwbaule.flexprofile.dialogs;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.preference.PreferenceDialogFragmentCompat;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by alex on 04/09/15.
 */
public class MakeBackupFragment extends PreferenceDialogFragmentCompat {
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private ProgressBar pb;
    private TextView txt;


    public static MakeBackupFragment newInstance(String key) {
        final MakeBackupFragment fragment = new MakeBackupFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        if (Build.VERSION.SDK_INT >= 23) {
            if (rootapp.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                BackgroundTask task = new BackgroundTask();
                task.execute();
            }
        } else {
            BackgroundTask task = new BackgroundTask();
            task.execute();
        }
        pb = view.findViewById(R.id.backup_progress);
        txt = view.findViewById(R.id.backup_txt_result);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    private class BackgroundTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean retval = true;
            List<String> export = rootapp.makeBackup();
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/MeuCarro");
            Log.d(getClass().getSimpleName(), "doInBackground: mkdirs " + dir.getAbsolutePath());

            boolean mkdirs = dir.mkdirs();
            Log.d(getClass().getSimpleName(), "doInBackground: mkdirs " + mkdirs);
            mkdirs = dir.setExecutable(true);
            Log.d(getClass().getSimpleName(), "doInBackground: setExecutable " + mkdirs);
            mkdirs = dir.setWritable(true);
            Log.d(getClass().getSimpleName(), "doInBackground: setWritable " + mkdirs);
            mkdirs = dir.setReadable(true);
            Log.d(getClass().getSimpleName(), "doInBackground: setReadable " + mkdirs);
            MediaScannerConnection.scanFile(rootapp, new String[]{dir.getAbsolutePath()}, null, null);
            Date currentDate = Calendar.getInstance().getTime();
            String datename = rootapp.setDateToSave(currentDate);
            File file = new File(dir, "MeuCarro_" + datename + ".mcb");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                for (String exp : export) {
                    fos.write(exp.getBytes());
                    fos.write(10);
                }
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                Log.d(getClass().getSimpleName(), "doInBackground: Try " + e.getMessage());
                retval = false;
            }
            MediaScannerConnection.scanFile(rootapp, new String[]{file.getAbsolutePath()}, null, null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                retval = false;
            }
            return retval;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            AlertDialog dialogPreference = (AlertDialog) getDialog();
            if (dialogPreference != null) {
                txt.setText(R.string.backup_sucesso);
                pb.setVisibility(View.INVISIBLE);
                dialogPreference.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                dialogPreference.getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.botao_ok);
                dialogPreference.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
            }
        }
    }
}