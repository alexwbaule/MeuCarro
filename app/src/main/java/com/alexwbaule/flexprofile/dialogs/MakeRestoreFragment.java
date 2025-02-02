package com.alexwbaule.flexprofile.dialogs;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.BackupListAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 04/09/15.
 */
public class MakeRestoreFragment extends PreferenceDialogFragmentCompat {
    private ListView lv;
    private ProgressBar pb;
    private TextView txt;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private BackupManager backupManager;

    public static MakeRestoreFragment newInstance(String key) {
        final MakeRestoreFragment fragment = new MakeRestoreFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        lv = view.findViewById(R.id.restore_list);
        pb = view.findViewById(R.id.restore_progress);
        txt = view.findViewById(R.id.restore_txt_result);
        backupManager = new BackupManager(rootapp);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txt.setTextColor(ContextCompat.getColor(rootapp, R.color.autonomia));
                txt.setText(R.string.restaurando);
                ArrayAdapter t = (ArrayAdapter) parent.getAdapter();
                File h = (File) t.getItem(position);
                if (Build.VERSION.SDK_INT >= 23) {
                    if (rootapp.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        String name = h.getName();
                        BackgroundTask task = new BackgroundTask();
                        task.execute(name);
                    }
                } else {
                    String name = h.getName();
                    BackgroundTask task = new BackgroundTask();
                    task.execute(name);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (rootapp.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pb.setVisibility(View.VISIBLE);
                GetBackupList task = new GetBackupList();
                task.execute();
            }
        } else {
            pb.setVisibility(View.VISIBLE);
            GetBackupList task = new GetBackupList();
            task.execute();
        }
        super.onBindDialogView(view);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    private class GetBackupList extends AsyncTask<Void, Void, ArrayList<File>> {
        @Override
        protected ArrayList<File> doInBackground(Void... params) {
            ArrayList<File> files = new ArrayList<>();
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/MeuCarro");
            if (!dir.exists()) {
                dir.mkdirs();
                MediaScannerConnection.scanFile(rootapp, new String[]{dir.getAbsolutePath()}, null, null);
            }
            File[] file = dir.listFiles();
            if (file != null) {
                for (int i = 0; i < file.length; i++) {
                    file[i].length();
                    file[i].lastModified();
                    if (file[i].getName().matches("^MeuCarro_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}\\.mcb$")) {
                        files.add(file[i]);
                    }
                }
            }
            Collections.reverse(files);
            return files;
        }

        @Override
        protected void onPostExecute(ArrayList<File> strings) {
            super.onPostExecute(strings);
            pb.setVisibility(View.GONE);
            lv.setAdapter(new BackupListAdapter(getContext(), android.R.layout.simple_list_item_1, strings));
        }
    }

    private class BackgroundTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            pb.setVisibility(View.GONE);
            final AlertDialog dialogPreference = (AlertDialog) getDialog();
            if (dialogPreference != null) {
                if (result) {
                    txt.setTextColor(ContextCompat.getColor(rootapp, R.color.green));
                    txt.setText(R.string.restore_sucesso);
                } else {
                    txt.setTextColor(ContextCompat.getColor(rootapp, R.color.red));
                    txt.setText(R.string.restore_fail);
                }
                dialogPreference.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                dialogPreference.getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.botao_ok);
            }
            backupManager.dataChanged();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean retval = false;
            String filename = params[0];
            List<String> files = new ArrayList<>();
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/MeuCarro/" + filename);
            try {
                InputStream in = new FileInputStream(dir);
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = r.readLine()) != null) {
                    files.add(line);
                }
                r.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!files.isEmpty()) {
                retval = rootapp.makeRestore(files);
            }
            return retval;
        }

    }
}