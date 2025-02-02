package com.alexwbaule.flexprofile.tasks;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.domain.SQLiteHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PopfromAssets extends AsyncTask<SQLiteHelper.DBandFile, Void, String> {
    protected String doInBackground(SQLiteHelper.DBandFile... params) {
        SQLiteDatabase db = params[0].db;
        String file = params[0].file;

        Log.d(getClass().getSimpleName(), String.format("##################### STARTING POP (%s) ####################", file));

        db.beginTransaction();
        try {
            InputStream sql = MeuCarroApplication.getInstance().getAssets().open(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(sql, StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                if (!str.isEmpty() || str.contains("INSERT INTO")) {
                    db.execSQL(str);
                }
            }
            db.setTransactionSuccessful();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.endTransaction();
        Log.d(getClass().getSimpleName(), String.format("##################### FINISHED POP (%s) ####################", file));
        return file;
    }

    @Override
    protected void onPostExecute(String file) {
        Log.d(getClass().getSimpleName(), String.format("##################### POSTEXECUTE POP (%s) ####################", file));
    }
}