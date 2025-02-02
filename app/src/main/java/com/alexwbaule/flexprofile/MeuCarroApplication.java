package com.alexwbaule.flexprofile;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import androidx.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alexwbaule.flexprofile.broadcasts.AlarmCheckModels;
import com.alexwbaule.flexprofile.containers.Categories;
import com.alexwbaule.flexprofile.containers.Combustivel;
import com.alexwbaule.flexprofile.containers.Consumos;
import com.alexwbaule.flexprofile.containers.DateTime;
import com.alexwbaule.flexprofile.containers.DetectedPlace;
import com.alexwbaule.flexprofile.containers.DrawerItem;
import com.alexwbaule.flexprofile.containers.Fuel;
import com.alexwbaule.flexprofile.containers.FuelReportBar;
import com.alexwbaule.flexprofile.containers.MaintenanceParts;
import com.alexwbaule.flexprofile.containers.Maintenances;
import com.alexwbaule.flexprofile.containers.Oils;
import com.alexwbaule.flexprofile.containers.PriceAndKm;
import com.alexwbaule.flexprofile.containers.Vehicles;
import com.alexwbaule.flexprofile.domain.SQLiteHelper;
import com.alexwbaule.flexprofile.json.Requestor;
import com.alexwbaule.flexprofile.network.VolleySingleton;
import com.alexwbaule.flexprofile.utils.DbBitmapUtility;
import com.alexwbaule.flexprofile.utils.ObfuscateBackup;
import com.alexwbaule.flexprofile.utils.PreferencesProcessed;
import com.alexwbaule.flexprofile.utils.ReportValueFormatter;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;
import com.android.volley.RequestQueue;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.internal.ca;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 07/07/15.
 */
public class MeuCarroApplication extends Application {
    private static final String TAG = "MeuCarroApplication";
    private static final String LAST_SHA256_FROM_MODELS = "models_sha256";
    public static final String NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID;
    public static final String ACTION_UPDATE_INTENT = "UPDATE_MODELS";


    private static MeuCarroApplication instance = null;
    private BackupManager backupManager;
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    SaveShowCalculos saveshowcalculos;
    private static String[] tables = new String[]{"cars", "consumos", "history", "place", "oilhistory", "maintenance", "maintenance_parts"};
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    public GoogleSignInClient googleSignInClient;
    public GoogleSignInAccount googleAccount;
    public boolean needsAuthentication = true;

    public static MeuCarroApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new SQLiteHelper(instance);
        database = dbHelper.getWritableDatabase();

        try {
        } catch (Exception e) {
            database = null;
        }
        backupManager = new BackupManager(instance);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Setup Places Client
        String apiKey = getString(R.string.API_PLACE_KEY);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotificationChannel();
        }
        StartAlarm();
        /*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.webclient_id))
                .build();

            googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });

        */
//        googleAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (googleAccount != null) {
//            Log.d(TAG, "+++++++++++++++++ DisplayName: "+ googleAccount.getDisplayName() +" email: "+ googleAccount.getEmail()+ " Token: " + googleAccount.getIdToken()+ "+++++++++++++++++");
//            needsAuthentication = false;
//        }
        Log.d(getClass().getSimpleName(), "MeuCarroApplication.onCreate()");
    }


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            googleAccount = completedTask.getResult(ApiException.class);
            needsAuthentication = false;
            Log.d(TAG, "+++++++++++++++++ DisplayName: "+ googleAccount.getDisplayName() +" email: "+ googleAccount.getEmail()+ " Token: " + googleAccount.getIdToken()+ "+++++++++++++++++");
        } catch (ApiException e) {
            needsAuthentication = true;
            Log.w(TAG, "handleSignInResult:error", e);
        }
    }

    public void StartAlarm(){
        Intent newIntent = new Intent(MeuCarroApplication.getInstance(), AlarmCheckModels.class);
        newIntent.setAction(MeuCarroApplication.ACTION_UPDATE_INTENT);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(MeuCarroApplication.getInstance(), 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MeuCarroApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(),AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        dbHelper.close();
        Log.d(getClass().getSimpleName(), "MeuCarroApplication.onTerminate()");
    }

    public static int getHeaderImageID(String name) {
        if (name != null) {
            String name2 = name.replace("_spin_", "_header_");
            return instance.getResources().getIdentifier(name2, "drawable", instance.getPackageName());
        }
        return 0;
    }


    protected void setLastSha256FromModels(String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString(LAST_SHA256_FROM_MODELS, value).apply();
    }

    protected String getLastSha256FromModels() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(LAST_SHA256_FROM_MODELS, "");
    }


    public Bitmap getScaledBitmap(Uri picturePath, int width, int height) {
        InputStream input = null;
        try {
            input = getContentResolver().openInputStream(picturePath);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "getScaledBitmap: ERROR" + e.getMessage());
        }
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = 4;

        Bitmap image_reduzida = BitmapFactory.decodeStream(input, null, sizeOptions);
        return ThumbnailUtils.extractThumbnail(image_reduzida, 200, 200);
    }

    public Drawable createFuelIcon(Drawable backgroundImage, char txt, int color) {
        int height = backgroundImage.getIntrinsicHeight();
        int width = backgroundImage.getIntrinsicWidth();
        String text = Character.toString(txt);

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Create a canvas, that will draw on to canvasBitmap.
        Canvas imageCanvas = new Canvas(canvasBitmap);

        // Set up the paint for use with our Canvas
        TextPaint imagePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG | TextPaint.LINEAR_TEXT_FLAG);

        //Paint imagePaint = new Paint();
        imagePaint.setTextAlign(Paint.Align.CENTER);
        imagePaint.setColor(Color.WHITE);
        imagePaint.setTypeface(Typeface.DEFAULT_BOLD);
        imagePaint.setTextSize(80f);

        // Draw the image to our canvas
        backgroundImage.draw(imageCanvas);


        Drawable wrappedDrawable = DrawableCompat.wrap(backgroundImage);
        DrawableCompat.setTint(wrappedDrawable, color);

        // Draw the text on top of our image
        imageCanvas.drawText(text, width / 2, 90, imagePaint);

        // Combine background and text to a LayerDrawable
        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{backgroundImage, new BitmapDrawable(canvasBitmap)});

        return layerDrawable;
    }

    protected String setDateTimeToDB(String dateTime) {
        String data;

        SimpleDateFormat dateFormat = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(instance);
        String datePattern = dateFormat.toLocalizedPattern();

        SimpleDateFormat timeFormat = (SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(instance);
        String timePattern = timeFormat.toLocalizedPattern();

        if (timePattern.equals("H'h'mm")) {
            timePattern = "HH:mm";
        }
        Log.d(getClass().getSimpleName(), String.format("TP ==|%s|== DT ==|%s|==", timePattern, datePattern));

        SimpleDateFormat oldformat = new SimpleDateFormat(datePattern + " " + timePattern);
        SimpleDateFormat dbformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date dt = oldformat.parse(dateTime);
            data = dbformat.format(dt);
        } catch (Exception E) {
            Log.d(getClass().getSimpleName(), String.format("ERROR ==|%s|==", E.getLocalizedMessage()));
            return dateTime + " 00:00";
        }
        return data;
    }

    public DateTime splitedNowDateTime(Date currentDate) {
        String[] data = new String[2];
        SimpleDateFormat dateFormat = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(instance);
        SimpleDateFormat timeFormat = (SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(instance);
        String datePattern = dateFormat.toLocalizedPattern();
        String timePattern = timeFormat.toLocalizedPattern();

        if (timePattern.equals("H'h'mm")) {
            timePattern = "HH:mm";
        }
        Log.d(getClass().getSimpleName(), String.format("TP ==|%s|== DT ==|%s|==", timePattern, datePattern));

        SimpleDateFormat date = new SimpleDateFormat(datePattern, Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat(timePattern, Locale.getDefault());

        try {
            data[0] = date.format(currentDate);
            data[1] = time.format(currentDate);
        } catch (Exception E) {
            Log.d(getClass().getSimpleName(), String.format("ERROR ==|%s|==", E.getLocalizedMessage()));
        }
        return new DateTime(data);
    }


    public String setDateToSave(Date dt) {
        String data;
        SimpleDateFormat dbformat = new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault());
        try {
            data = dbformat.format(dt);
        } catch (Exception E) {
            return "0000-00-00_00-00";
        }
        return data;
    }

    public static String setDateToShow(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(d);
    }

    public static String setSizeInKbToShow(long timestamp) {
        int unit = 1024;
        if (timestamp < unit) return timestamp + " B";
        int exp = (int) (Math.log(timestamp) / Math.log(unit));
        String pre = String.valueOf(("KMGTPE").charAt(exp - 1));
        return String.format(Locale.getDefault(), "%.1f %sB", timestamp / Math.pow(unit, exp), pre);
    }

    public static void WarnUser(@NonNull View v, String txt, int duration, boolean dismiss, @Nullable MenuItem menu) {
        InputMethodManager imm = (InputMethodManager) getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        Snackbar snk = Snackbar.make(v, txt, duration);
        if (dismiss) {
            snk.setAction(R.string.botao_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        snk.getView().setBackgroundResource(R.color.warning);
        snk.show();
        if (menu != null)
            menu.setEnabled(true);
    }

    public static void WarnUser(@NonNull View v, int txt, int duration, boolean dismiss, @Nullable MenuItem menu) {
        WarnUser(v, getInstance().getApplicationContext().getString(txt), duration, dismiss, menu);
    }

    public static int getImageID(String name) {
        return instance.getResources().getIdentifier(name, "drawable", instance.getPackageName());
    }

    public int getReportFuelColor(int v) {
        //int color = ChartUtils.pickColor();
        int color = Color.RED;
        if (v == 1) {
            color = ContextCompat.getColor(getInstance(), R.color.gasolina);
        } else if (v == 2) {
            color = ContextCompat.getColor(getInstance(), R.color.Etanol);
        } else if (v == 3) {
            color = ContextCompat.getColor(getInstance(), R.color.gnv);
        } else if (v == 4) {
            color = ContextCompat.getColor(getInstance(), R.color.diesel);
        }
        return color;
    }

    public void setPrices(String var, String value) {
        Number numv;
        NumberFormat parser = NumberFormat.getInstance();
        try {
            numv = parser.parse(value);
        } catch (ParseException e) {
            numv = 0.000f;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putFloat(var, numv.floatValue()).apply();
    }

    public void updatePrices() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String[] var = {"0", "0", "0", "0"};

        var[0] = preferences.getString(SQLiteHelper.LAST_GAS, "0.000");
        var[1] = preferences.getString(SQLiteHelper.LAST_ALC, "0.000");
        var[2] = preferences.getString(SQLiteHelper.LAST_GNV, "0.000");
        var[3] = preferences.getString(SQLiteHelper.LAST_DIE, "0.000");

        setPrices(SQLiteHelper.LAST_GAS, var[0]);
        setPrices(SQLiteHelper.LAST_ALC, var[1]);
        setPrices(SQLiteHelper.LAST_GNV, var[2]);
        setPrices(SQLiteHelper.LAST_DIE, var[3]);
    }

    public double[] getPrices() {
        double[] retval = {0.000, 0.000, 0.000, 0.000};
        Number[] var = {0, 0, 0, 0};
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        try {
            var[0] = preferences.getFloat(SQLiteHelper.LAST_GAS, 0.000f);
            var[1] = preferences.getFloat(SQLiteHelper.LAST_ALC, 0.000f);
            var[2] = preferences.getFloat(SQLiteHelper.LAST_GNV, 0.000f);
            var[3] = preferences.getFloat(SQLiteHelper.LAST_DIE, 0.000f);

            retval[0] = var[0].doubleValue();
            retval[1] = var[1].doubleValue();
            retval[2] = var[2].doubleValue();
            retval[3] = var[3].doubleValue();
        } catch (Exception e) {

        }
        return retval;
    }

    private void setAllConfs(String line) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String[] var = line.split(":");

        try {
            preferences.edit().putFloat(SQLiteHelper.LAST_GAS, Float.parseFloat(var[0])).apply();
            preferences.edit().putFloat(SQLiteHelper.LAST_ALC, Float.parseFloat(var[1])).apply();
            preferences.edit().putFloat(SQLiteHelper.LAST_GNV, Float.parseFloat(var[2])).apply();
            preferences.edit().putFloat(SQLiteHelper.LAST_DIE, Float.parseFloat(var[3])).apply();
            preferences.edit().putString("un_limit_select", var[4]).apply();
            preferences.edit().putString("un_consumo_gnv", var[5]).apply();
            preferences.edit().putString("un_consumo_comb", var[6]).apply();
            preferences.edit().putString("un_volume_gnv", var[7]).apply();
            preferences.edit().putString("un_volume_tanque", var[8]).apply();
            preferences.edit().putString("un_calculo_custo", var[9]).apply();
            preferences.edit().putLong("default_car_id", Long.parseLong(var[10])).apply();
            preferences.edit().putBoolean("navigation_drawer_learned", Boolean.parseBoolean(var[11])).apply();
            preferences.edit().putString("un_medida_dist", var[12]).apply();
        } catch (Exception e) {

        }
    }

    private String getAllConfs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String retVar = "";
        retVar += preferences.getFloat(SQLiteHelper.LAST_GAS, 0.000f) + ":";
        retVar += preferences.getFloat(SQLiteHelper.LAST_ALC, 0.000f) + ":";
        retVar += preferences.getFloat(SQLiteHelper.LAST_GNV, 0.000f) + ":";
        retVar += preferences.getFloat(SQLiteHelper.LAST_DIE, 0.000f) + ":";
        retVar += preferences.getString("un_limit_select", "0") + ":";
        retVar += preferences.getString("un_consumo_gnv", "mpv") + ":";
        retVar += preferences.getString("un_consumo_comb", "mpv") + ":";
        retVar += preferences.getString("un_volume_gnv", "MTS") + ":";
        retVar += preferences.getString("un_volume_tanque", "LTS") + ":";
        retVar += preferences.getString("un_calculo_custo", "custo") + ":";
        retVar += preferences.getLong("default_car_id", 0) + ":";
        retVar += preferences.getBoolean("navigation_drawer_learned", true) + ":";
        retVar += preferences.getString("un_medida_dist", "km");

        return retVar;
    }

    public List<String> makeBackup() {
        List<String> resulta = new ArrayList<>();
        Cursor cursor;

        Log.d(getClass().getSimpleName(), "********* STARTING BACKUP ***************");

        resulta.add(ObfuscateBackup.obfuscate(getAllConfs()));
        for (String table : tables) {
            cursor = database.rawQuery("SELECT * from " + table, null);
            String inserted = "INSERT INTO " + table + " (";
            int total = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                String values = "";
                String keys = "";
                int count = 1;
                for (String colname : cursor.getColumnNames()) {
                    String append = cursor.getString(cursor.getColumnIndex(colname));
                    if (count == total) {
                        keys += colname + " ";
                        values += "\"" + append + "\" ";
                    } else {
                        keys += colname + ", ";
                        values += "\"" + append + "\", ";
                    }
                    count++;
                }
                resulta.add(ObfuscateBackup.obfuscate(inserted + keys + ") VALUES (" + values + ");"));
            }
            cursor.close();
        }
        List<Vehicles> vehiclesList = fetchAllCars();
        for (Vehicles car : vehiclesList) {
            String id = String.valueOf(car.get_id());
            Bitmap bitmap = DbBitmapUtility.readbitmap(getApplicationContext(), id);
            byte[] bytes = DbBitmapUtility.getBytes(bitmap);
            resulta.add("FILE-" + id + "|" + Base64.encodeToString(bytes, Base64.NO_WRAP));
        }

        Log.d(getClass().getSimpleName(), String.format("********* FINISHED BACKUP SIZE IS (%s) ***************", resulta.size()));
        return resulta;
    }

    public boolean makeRestore(List<String> lines) {
        boolean retval = true;
        Pattern r = Pattern.compile("^FILE-(\\d+)\\|");

        Log.d(getClass().getSimpleName(), String.format("********* RESTORE SIZE IS (%s) ***************", lines.size()));


        for (String table : tables) {
            database.execSQL("DELETE from " + table);
        }

        Log.d(getClass().getSimpleName(), "********* STARTING RESTORE ***************");


        database.execSQL("BEGIN");
        try {
            boolean readed = false;
            for (String line : lines) {
                if (!readed) {
                    setAllConfs(ObfuscateBackup.unobfuscate(line));
                    readed = true;
                    continue;
                }
                Matcher m = r.matcher(line);

                if (m.find()) {
                    String s = m.group(1);
                    Log.d(getClass().getSimpleName(), "********* NUMBER IS TOTAL " + m.groupCount() + " = [" + s + "] ***************");
                    try {
                        Long aLong = Long.parseLong(s);
                        String[] splited = line.split("^FILE-(\\d+)\\|", 2);
                        byte[] decode = Base64.decode(splited[1], Base64.NO_WRAP);
                        DbBitmapUtility.savebitmap(getApplicationContext(), DbBitmapUtility.getImage(getApplicationContext(), decode), aLong);
                    } catch (IOException | SecurityException | NullPointerException e) {
                        Log.d(getClass().getSimpleName(), "********* FAIL TO SAVE = [" + s + "] ***************");
                        Log.d(getClass().getSimpleName(), "********* [" + e.getMessage() + "] ***************");
                    }
                } else {
                    database.execSQL(ObfuscateBackup.unobfuscate(line));
                }
            }
        } catch (android.database.SQLException e) {
            retval = false;
        }
        database.execSQL("COMMIT");

        Log.d(getClass().getSimpleName(), "********* FINISHED RESTORE ***************");

        return retval;
    }

    public Cursor getCarModel(String args) {
        return database.rawQuery("SELECT _id, car from cars_model where car LIKE '%" + args + "%' ", null);
    }

    public Cursor getOilName(String args) {
        return database.rawQuery("SELECT _id, oil from oil_name where oil LIKE '%" + args + "%' ", null);
    }

    public Cursor getPartsName(String args) {
        return database.rawQuery("SELECT _id, part_name from maintenance_parts_list where part_name LIKE '%" + args + "%' ", null);
    }

    public long createCar(View v, boolean is_default, String name, double tq, double tq_gnv, String iconid, Bitmap bmp) {
        ContentValues values = new ContentValues();
        saveshowcalculos = new SaveShowCalculos(getInstance());

        long id = 0;

        values.put(SQLiteHelper.KEY_NAME, name);
        values.put(SQLiteHelper.KEY_TQ, saveshowcalculos.getSaveVolume(tq));
        values.put(SQLiteHelper.KEY_TQ_GNV, saveshowcalculos.getSaveVolumeGNV(tq_gnv));
        values.put(SQLiteHelper.KEY_ICON_ID, iconid);

        try {
            id = database.insertOrThrow(SQLiteHelper.DATABASE_TABLE, null, values);
            if (is_default) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
                preferences.edit().putLong(SQLiteHelper.ID_DEFAULT, id).apply();
            }
            DbBitmapUtility.savebitmap(this, bmp, id);
            backupManager.dataChanged();
        } catch (SQLiteConstraintException | IOException e) {
            WarnUser(v, R.string.nome_modelo_existente, Snackbar.LENGTH_LONG, true, null);
        }

        return id;
    }

    public void createConsumo(long id, ArrayList<Consumos> lista) {
        saveshowcalculos = new SaveShowCalculos(getInstance());

        for (Consumos consumo : lista) {
            ContentValues values = new ContentValues();

            values.put(SQLiteHelper.KEY_CAR_ID, id);
            values.put(SQLiteHelper.KEY_COMB_ID, consumo.combustivel);
            if (consumo.combustivel == 3) {
                values.put(SQLiteHelper.KEY_CONSUMO, saveshowcalculos.getSaveConsumo(consumo.consumo));
            } else {
                values.put(SQLiteHelper.KEY_CONSUMO, saveshowcalculos.getSaveConsumoGNV(consumo.consumo));
            }
            try {
                long ins_id = database.insertOrThrow(SQLiteHelper.DATABASE_COMSUMOS, null, values);
                backupManager.dataChanged();
            } catch (SQLiteConstraintException e) {
                Toast.makeText(instance, R.string.consumo_existente, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateConsumo() {
        long defaultCar = getDefaultCar();

        double updateGasolina;
        double updateEtanol;
        double updateGnv;
        double updateDiesel;

        double kmGasolina = 0;
        double kmEtanol = 0;
        double kmGnv = 0;
        double kmDiesel = 0;

        double ltsGasolina = 0.000;
        double ltsEtanol = 0.000;
        double ltsGnv = 0.000;
        double ltsDiesel = 0.000;
        int update = 0;

        List<Fuel> ll = getAllFuel("update", "update");

        for (Fuel eachfuel : ll) {
            Log.d(getClass().getSimpleName(), eachfuel.toString());
            if (eachfuel.getvKml() == 0)
                continue;
            if (eachfuel.isFake())
                continue;
            switch (eachfuel.getComb_id()) {
                case 1:
                    kmGasolina += Double.valueOf(eachfuel.getvDiff_km());
                    ltsGasolina += Double.valueOf(eachfuel.getvRealLitros());
                    break;
                case 2:
                    kmEtanol += Double.valueOf(eachfuel.getvDiff_km());
                    ltsEtanol += Double.valueOf(eachfuel.getvRealLitros());
                    break;
                case 3:
                    kmGnv += Double.valueOf(eachfuel.getvDiff_km());
                    ltsGnv += Double.valueOf(eachfuel.getvRealLitros());
                    break;
                case 4:
                    kmDiesel += Double.valueOf(eachfuel.getvDiff_km());
                    ltsDiesel += Double.valueOf(eachfuel.getvRealLitros());
                    break;
            }
        }
        Log.d(getClass().getSimpleName(), "KM_G = " + kmGasolina + " LT_G =" + ltsGasolina);
        Log.d(getClass().getSimpleName(), "KM_E = " + kmEtanol + " LT_E =" + ltsEtanol);
        Log.d(getClass().getSimpleName(), "KM_GV = " + kmGnv + " LT_GV =" + ltsGnv);
        Log.d(getClass().getSimpleName(), "KM_D = " + kmDiesel + " LT_D =" + ltsDiesel);

        updateGasolina = kmGasolina / ltsGasolina;
        updateEtanol = kmEtanol / ltsEtanol;
        updateGnv = kmGnv / ltsGnv;
        updateDiesel = kmDiesel / ltsDiesel;

        if (updateGasolina > 0) {
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.KEY_CONSUMO, updateGasolina);
            try {
                database.update(SQLiteHelper.DATABASE_COMSUMOS, values, " car_id = ? and comb_id = 1", new String[]{String.valueOf(defaultCar)});
                update++;
            } catch (SQLiteConstraintException e) {
                Log.d(getClass().getSimpleName(), "Erro ao executar updateGasolina");
            }
        }
        if (updateEtanol > 0) {
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.KEY_CONSUMO, updateEtanol);
            try {
                database.update(SQLiteHelper.DATABASE_COMSUMOS, values, " car_id = ? and comb_id = 2", new String[]{String.valueOf(defaultCar)});
                update++;
            } catch (SQLiteConstraintException e) {
                Log.d(getClass().getSimpleName(), "Erro ao executar updateEtanol");
            }
        }
        if (updateGnv > 0) {
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.KEY_CONSUMO, updateGnv);
            try {
                database.update(SQLiteHelper.DATABASE_COMSUMOS, values, " car_id = ? and comb_id = 3", new String[]{String.valueOf(defaultCar)});
                update++;
            } catch (SQLiteConstraintException e) {
                Log.d(getClass().getSimpleName(), "Erro ao executar updateGnv");
            }
        }
        if (updateDiesel > 0) {
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.KEY_CONSUMO, updateDiesel);
            try {
                database.update(SQLiteHelper.DATABASE_COMSUMOS, values, " car_id = ? and comb_id = 4", new String[]{String.valueOf(defaultCar)});
                update++;
            } catch (SQLiteConstraintException e) {
                Log.d(getClass().getSimpleName(), "Erro ao executar updateDiesel");
            }
        }
        if (update > 0) {
            backupManager.dataChanged();
        }
    }

    public int getHasHistory() {
        long defaultCar = getDefaultCar();
        int total = 0;

        Cursor cursor;

        cursor = database.rawQuery("select count(_id) as total from history where car_id = " + defaultCar, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
        }
        cursor.close();

        return total;
    }

    public int getHasOilHistory() {
        long defaultCar = getDefaultCar();
        int total = 0;

        Cursor cursor;

        cursor = database.rawQuery("select count(_id) as total from oilhistory where car_id = " + defaultCar, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
        }
        cursor.close();

        return total;
    }

    public int getHasCars() {
        int total = 0;

        Cursor cursor;

        cursor = database.rawQuery("select count(_id) as total from cars ", null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
        }
        cursor.close();

        return total;
    }

    public int getHasMaintenance() {
        long defaultCar = getDefaultCar();
        int total = 0;

        Cursor cursor;

        cursor = database.rawQuery("select count(_id) as total from maintenance where car_id = " + defaultCar, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
        }
        cursor.close();

        return total;
    }

    public List<Fuel> getAllFuel(String start, String stop) {
        List<Fuel> ll = new ArrayList<>();
        Cursor cursor;
        long defaultCar = getDefaultCar();
        boolean reversed = false;
        String limitConsumo = getLimitConsumo();

        String limit = "";

        if (!limitConsumo.equals("0")) {
            limit = "LIMIT " + (Integer.valueOf(limitConsumo) + 1);
        }

        if (start == null || stop == null) {
            cursor = database.rawQuery("SELECT " +
                    "strftime('%d',created_date) as only_day," +
                    "strftime('%m',created_date) as only_month, " +
                    "strftime('%Y',created_date) as only_year, " +
                    "place_name, place_addr, " +
                    "strftime('%d/%m/%Y',created_date) as create_date, strftime('%H:%M', created_date) as created_time, price_lt, combustivel, km, litros, partial, proportion, tanque_comb, tanque_gnv, " +
                    " history.car_id as car_id, history._id as _id, comb_id from history" +
                    " left join combustiveis on combustiveis._id = comb_id " +
                    " left join cars on history.car_id = cars._id " +
                    " left join place on history.place_id = place._id " +
                    "where car_id = ? and history.hide != 1 order by km desc", new String[]{String.valueOf(defaultCar)});
        } else if (start.equals("update") && stop.equals("update")) {
            Log.d(getClass().getSimpleName(), "LIMIT IS " + limit);

            cursor = database.rawQuery("SELECT " +
                    "strftime('%d',created_date) as only_day," +
                    "strftime('%m',created_date) as only_month, " +
                    "strftime('%Y',created_date) as only_year, " +
                    "place_name, place_addr, " +
                    "strftime('%d/%m/%Y',created_date) as create_date, strftime('%H:%M', created_date) as created_time, price_lt, combustivel, km, litros, partial, proportion, tanque_comb, tanque_gnv," +
                    " history.car_id as car_id, history._id as _id, comb_id from history" +
                    " left join combustiveis on combustiveis._id = comb_id " +
                    " left join cars on history.car_id = cars._id " +
                    " left join place on history.place_id = place._id " +
                    "where car_id = ? and history.hide != 1 order by km desc " + limit, new String[]{String.valueOf(defaultCar)});

        } else {
            String dini = setDateTimeToDB(start + " 00:00");
            String dend = setDateTimeToDB(stop + " 23:59");
            cursor = database.rawQuery("SELECT " +
                    "strftime('%d',created_date) as only_day," +
                    "strftime('%m',created_date) as only_month, " +
                    "strftime('%Y',created_date) as only_year, " +
                    "place_name, place_addr, " +
                    "strftime('%d/%m/%Y',created_date) as create_date, strftime('%H:%M', created_date) as created_time, price_lt, combustivel, km, litros, partial, proportion, tanque_comb, tanque_gnv, " +
                    " history.car_id as car_id, history._id as _id, comb_id from history" +
                    " left join combustiveis on combustiveis._id = comb_id " +
                    " left join cars on history.car_id = cars._id " +
                    " left join place on history.place_id = place._id " +
                    "where car_id = ? and history.hide != 1 and created_date between ? and ? order by km desc", new String[]{String.valueOf(defaultCar), dini, dend});

            reversed = true;
        }
        int vvvv = 0;
        String lastDate = "";
        int headerId = 0;
        while (cursor.moveToNext()) {
            double kml = 0.000;
            double km_diff = 0.000;
            double litros_falta = 0.000;
            double km = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_KM));
            double litros = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_LTS));
            int comb_id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COMB_ID));
            double tanque_comb = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ));
            double tanque_gnv = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ_GNV));
            int proportion = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.HIST_PROPORTION));
            boolean isPartial = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.HIST_PARTIAL)) == 1;
            String histdate = cursor.getString(cursor.getColumnIndex(SQLiteHelper.CREATE_DATE));
            String keytime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIME));
            String keycombus = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL));
            double histprice = cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_PRICE));
            int rowid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
            String year = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_YEAR));
            String month = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_MONTH));
            String day = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_DAY));
            String placename = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PLACE_NAME));
            String placeaddr = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PLACE_ADDR));

            if (placename == null) {
                placename = "Desconhecido";
                placeaddr = "Desconhecido";
            }

            double real_litros = 0.000;
            boolean fake = false;

            if (ll.size() > 0) {
                int index = ll.size() - 1;
                Fuel newest = ll.get(index);
                km_diff = (newest.getvKm() - km);

                if (!newest.isPartial() && isPartial) {
                    real_litros = ((newest.getTanque() * proportion) / 100) - (newest.getTanque() - newest.getvLitros());
                    // real_litros =  litros - (newest.getTanque() - newest.getvLitros());
                    Log.d(getClass().getSimpleName(), vvvv + " --- " + real_litros);
                } else if (newest.isPartial() && !isPartial) {
                    real_litros = newest.getTanque() - (((newest.getTanque() * newest.getvProportion()) / 100) - newest.getvLitros());
                } else if (newest.isPartial() && isPartial) {
                    real_litros = ((newest.getTanque() * proportion) / 100) - (((newest.getTanque() * newest.getvProportion()) / 100) - newest.getvLitros());
                } else if (!newest.isPartial() && !isPartial) {
                    real_litros = newest.getvLitros();
                }
                if ((comb_id == 3 && newest.getComb_id() == 3) || (comb_id != 3 && newest.getComb_id() != 3)) {
                    kml = (km_diff / real_litros);
                    fake = false;
                } else {
                    kml = (km_diff / litros);
                    fake = true;
                }
            }
            if (!lastDate.equals(year + month))
                headerId = cursor.getPosition();
            Fuel add = new Fuel(
                    histdate,
                    keytime,
                    comb_id,
                    keycombus,
                    km,
                    litros,
                    real_litros,
                    histprice,
                    km_diff,
                    kml,
                    rowid,
                    fake,
                    tanque_comb,
                    tanque_gnv,
                    proportion,
                    isPartial,
                    headerId,
                    year,
                    month,
                    day,
                    placename,
                    placeaddr
            );
            lastDate = year + month;
            Log.d(getClass().getSimpleName(), vvvv + " --- " + add.toString());
            vvvv++;
            ll.add(add);
        }
        cursor.close();

        if (reversed)
            Collections.reverse(ll);
        return ll;
    }

    public List<Oils> getAllOil() {
        Log.d(getClass().getSimpleName(), "GET ALL OILS");
        List<Oils> ll = new ArrayList<>();
        Cursor cursor;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        long idSearch = preferences.getLong(SQLiteHelper.ID_DEFAULT, 0);

        cursor = database.rawQuery("SELECT created_date as ord_date ," +
                "strftime('%d',created_date) as only_day," +
                "strftime('%m',created_date) as only_month, " +
                "strftime('%Y',created_date) as only_year, " +
                "strftime('%d/%m/%Y',created_date) as created_date, strftime('%H:%M', created_date) as created_time, price_lt,  km, next_km, litros, has_filter, oil_name, oilhistory._id as _id from oilhistory left join cars on cars._id = oilhistory._id  where car_id = ? and oilhistory.hide != 1 order by ord_date desc", new String[]{String.valueOf(idSearch)});

        Log.d(getClass().getSimpleName(), "GET ALL OILS --> " + cursor.getCount());

        String lastDate = "";
        int headerId = 0;

        while (cursor.moveToNext()) {
            String year = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_YEAR));
            String month = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_MONTH));
            String day = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_DAY));
            if (!lastDate.equals(year + month))
                headerId = cursor.getPosition();
            Oils add = new Oils(
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_DATE)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIME)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_KM)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_NEXT_KM)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_PRICE)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_LTS)),
                    cursor.getInt(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_FILTER)) == 1,
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.HIST_OIL_NAME)),
                    cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID)),
                    headerId,
                    year,
                    month,
                    day
            );
            ll.add(add);
            lastDate = year + month;
        }
        cursor.close();

        Log.d(getClass().getSimpleName(), "GET ALL OILS --> " + ll.size());
        return ll;
    }

    public long insertCombustivel(String name, String letter, int color) {
        ContentValues values = new ContentValues();
        long id = 0;
        values.put(SQLiteHelper.KEY_COMBUSTIVEL, name);
        values.put(SQLiteHelper.KEY_COLOR, color);
        values.put(SQLiteHelper.KEY_LETTER, letter);

        try {
            id = database.insertOrThrow(SQLiteHelper.DATABASE_COMBUSTIVES, null, values);
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.nome_modelo_existente, Toast.LENGTH_LONG).show();
        }
        return id;
    }

    public boolean deleteCombustivel(long mid) {
        String id = Long.toString(mid);

        if (mid <= 4)
            return false;
        try {
            database.delete(SQLiteHelper.DATABASE_COMBUSTIVES, " _id = ? ", new String[]{id});
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.erro_deletar, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public ArrayList<Combustivel> getCombustiveis() {
        Cursor cursor;
        ArrayList<Combustivel> data = new ArrayList<Combustivel>();
        cursor = database.query(SQLiteHelper.DATABASE_COMBUSTIVES, new String[]{SQLiteHelper.KEY_COMBUSTIVEL, SQLiteHelper.KEY_ROWID, SQLiteHelper.KEY_LETTER, SQLiteHelper.KEY_COLOR}, null, null, null, null, null);

        data.add(new Combustivel(getString(R.string.nenhum), ContextCompat.getDrawable(this, R.drawable.ic_menu_none), R.drawable.ic_menu_none, 0, 0));
        while (cursor.moveToNext()) {
            int keyid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
            if (keyid == 3) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_gnv),
                        R.drawable.ic_menu_gnv, keyid, 1));
            } else if (keyid == 1) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_gasolina),
                        R.drawable.ic_menu_gasolina, keyid, 2));
            } else if (keyid == 2) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_alcool),
                        R.drawable.ic_menu_alcool, keyid, 3));
            } else if (keyid == 4) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_diesel),
                        R.drawable.ic_menu_diesel, keyid, 4));
            } else {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)),
                        createFuelIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_paint),
                                cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_LETTER)).charAt(0),
                                cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COLOR))),
                        0, keyid, keyid));
            }
        }
        cursor.close();

        return data;
    }

    public ArrayList<Combustivel> getCombustiveisFromCar() {
        long defaultCar = getDefaultCar();
        Cursor cursor;
        ArrayList<Combustivel> data = new ArrayList<Combustivel>();
        cursor = database.rawQuery("SELECT " + SQLiteHelper.KEY_COLOR + ", " + SQLiteHelper.KEY_LETTER + ", " + SQLiteHelper.KEY_COMBUSTIVEL + ", combustiveis." + SQLiteHelper.KEY_ROWID + " from combustiveis left join consumos on  consumos.comb_id = combustiveis._id where consumos.car_id = ? ", new String[]{String.valueOf(defaultCar)});

        data.add(new Combustivel(getString(R.string.nenhum), ContextCompat.getDrawable(this, R.drawable.ic_menu_none), R.drawable.ic_menu_none, 0, 0));
        int position = 1;
        while (cursor.moveToNext()) {
            int keyid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
            if (keyid == 3) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_gnv),
                        R.drawable.ic_menu_gnv, keyid, position));
                position++;
            } else if (keyid == 1) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_gasolina),
                        R.drawable.ic_menu_gasolina, keyid, position));
                position++;
            } else if (keyid == 2) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_alcool),
                        R.drawable.ic_menu_alcool, keyid, position));
                position++;
            } else if (keyid == 4) {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)), ContextCompat.getDrawable(this, R.drawable.ic_menu_diesel),
                        R.drawable.ic_menu_diesel, keyid, position));
                position++;
            } else {
                data.add(new Combustivel(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)),
                        createFuelIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_paint),
                                cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_LETTER)).charAt(0),
                                cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COLOR))),
                        0, keyid, position));
                position++;
            }
        }
        cursor.close();

        return data;
    }

    public boolean includeOil(String name, double price, long km1, long km2, String data, double litros, boolean filter, long place_id) {
        saveshowcalculos = new SaveShowCalculos(getInstance());
        long defaultCar = getDefaultCar();

        if (defaultCar > 0) {
            ContentValues values = new ContentValues();

            String dateTime = setDateTimeToDB(data);

            values.put(SQLiteHelper.HIST_OIL_NAME, name);
            values.put(SQLiteHelper.HIST_OIL_PRICE, price);
            values.put(SQLiteHelper.HIST_OIL_KM, saveshowcalculos.getSaveOdometro(km1));
            values.put(SQLiteHelper.HIST_OIL_NEXT_KM, saveshowcalculos.getSaveOdometro(km2));
            values.put(SQLiteHelper.HIST_OIL_DATE, dateTime);
            values.put(SQLiteHelper.HIST_OIL_FILTER, filter);
            values.put(SQLiteHelper.HIST_OIL_LTS, saveshowcalculos.getSaveVolume(litros));
            values.put(SQLiteHelper.KEY_CAR_ID, defaultCar);
            values.put(SQLiteHelper.PLACE_ID, place_id);

            try {
                database.insertOrThrow(SQLiteHelper.DATABASE_OIL, null, values);
                backupManager.dataChanged();
                return true;
            } catch (SQLiteConstraintException e) {
                Log.d(getClass().getSimpleName(), "ERROR IS --> " + e.toString() + " MESSAGE --> " + e.getMessage());
                if (e.getMessage().contains("UNIQUE constraint failed: history.created_date")) {
                    Toast.makeText(instance, R.string.existe_oil_datetime, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(instance, R.string.registro_existente, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(instance, R.string.insira_um_veiculo, Toast.LENGTH_LONG).show();
        }

        return false;
    }

    public boolean includeCharge(double litros, long km, int type, String data, double price, boolean partial, int progress, long place_id) {
        saveshowcalculos = new SaveShowCalculos(getInstance());
        long defaultCar = getDefaultCar();

        if (defaultCar > 0) {
            ContentValues values = new ContentValues();

            String dateTime = setDateTimeToDB(data);

            values.put(SQLiteHelper.HIST_DATE, dateTime);
            values.put(SQLiteHelper.KEY_COMB_ID, type);
            values.put(SQLiteHelper.HIST_PRICE, price);
            values.put(SQLiteHelper.PLACE_ID, place_id);
            if (partial && progress < 100) {
                values.put(SQLiteHelper.HIST_PARTIAL, partial);
                values.put(SQLiteHelper.HIST_PROPORTION, progress);
            }
            values.put(SQLiteHelper.HIST_KM, saveshowcalculos.getSaveOdometro(km));
            if (type == 3) {
                values.put(SQLiteHelper.HIST_LTS, saveshowcalculos.getSaveVolumeGNV(litros));
            } else {
                values.put(SQLiteHelper.HIST_LTS, saveshowcalculos.getSaveVolume(litros));
            }
            values.put(SQLiteHelper.HIST_CAR, defaultCar);

            try {
                database.insertOrThrow(SQLiteHelper.DATABASE_HISTORY, null, values);
                backupManager.dataChanged();
                updateConsumo();
                return true;
            } catch (SQLiteConstraintException e) {
                Log.d(getClass().getSimpleName(), "ERROR IS --> " + e.toString() + " MESSAGE --> " + e.getMessage());
                if (e.getMessage().contains("UNIQUE constraint failed: history.created_date")) {
                    Toast.makeText(instance, R.string.existe_charge_datetime, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(instance, R.string.registro_existente, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(instance, R.string.insira_um_veiculo, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public long getDefaultCar() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        long idSearch = preferences.getLong(SQLiteHelper.ID_DEFAULT, 0);

        return idSearch;
    }

    public void setDefaultCar(long carId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putLong(SQLiteHelper.ID_DEFAULT, carId).apply();
        backupManager.dataChanged();
    }

    public String getLimitConsumo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String idSearch = preferences.getString(SQLiteHelper.MAX_LIMIT_QUERY, "0");
        return idSearch;
    }

    public List<Vehicles> fetchAllCars() {
        Log.d(getClass().getSimpleName(), "GET ALL CARS");
        List<Vehicles> ll = new ArrayList<>();
        Cursor cursor;
        cursor = database.rawQuery("select cars._id  as _id, icon_id, profile_name, tanque_comb, tanque_gnv,  " +
                "MAX(CASE WHEN comb_id = 1 THEN consumo END) AS 'gasolina', " +
                "MAX(CASE WHEN comb_id = 2 THEN consumo END) AS 'etanol', " +
                "MAX(CASE WHEN comb_id = 3 THEN consumo END) AS 'gnv', " +
                "MAX(CASE WHEN comb_id = 4 THEN consumo END) AS 'diesel' " +
                "from cars left join consumos on cars._id = consumos.car_id " +
                "left join combustiveis on combustiveis._id = consumos.comb_id where cars.hide != 1 group by profile_name", null);

        Log.d(getClass().getSimpleName(), "GET ALL CARS -> " + cursor.getCount());

        while (cursor.moveToNext()) {
            Vehicles add = new Vehicles(
                    cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID)),
                    cursor.getPosition(),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ICON_ID)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ_GNV)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_GASOLINA)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_ETANOL)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_GNV)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_DIESEL)));
            ll.add(add);
        }
        cursor.close();

        return ll;
    }

    public ArrayList<DrawerItem> fetchCarsName() {
        Cursor cursor;
        ArrayList<DrawerItem> data = new ArrayList<>();

        cursor = database.query(SQLiteHelper.DATABASE_TABLE, new String[]{SQLiteHelper.KEY_ROWID, SQLiteHelper.KEY_ICON_ID, SQLiteHelper.KEY_NAME}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            data.add(new DrawerItem(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ICON_ID)), cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME)), cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID))));
        }
        cursor.close();

        return data;
    }

    public boolean[] getHasCombustiveis() {
        Cursor cursor;
        boolean[] hasg = {false, false, false, false};

        cursor = database.rawQuery("select " +
                "MAX(CASE WHEN comb_id = 1 THEN 1 ELSE 0 END) AS 'has_gasolina'," +
                "MAX(CASE WHEN comb_id = 2 THEN 1 ELSE 0 END) AS 'has_Etanol'," +
                "MAX(CASE WHEN comb_id = 3 THEN 1 ELSE 0 END) AS 'has_gnv'," +
                "MAX(CASE WHEN comb_id = 4 THEN 1 ELSE 0 END) AS 'has_diesel'" +
                "from consumos where car_id = ? group by car_id", new String[]{String.valueOf(getDefaultCar())});
        if (cursor.moveToFirst()) {
            hasg[0] = (cursor.getInt(cursor.getColumnIndex("has_gasolina")) == 1);
            hasg[1] = (cursor.getInt(cursor.getColumnIndex("has_Etanol")) == 1);
            hasg[2] = (cursor.getInt(cursor.getColumnIndex("has_gnv")) == 1);
            hasg[3] = (cursor.getInt(cursor.getColumnIndex("has_diesel")) == 1);

        }
        cursor.close();
        return hasg;
    }

    public double[] getDataDefaultCar() {
        Cursor cursor;
        double[] valores = {0, 0, 0, 0, 0, 0};

        cursor = database.rawQuery("select tanque_comb, tanque_gnv,  " +
                "MAX(CASE WHEN comb_id = 1 THEN consumo END) AS 'gasolina', " +
                "MAX(CASE WHEN comb_id = 2 THEN consumo END) AS 'Etanol', " +
                "MAX(CASE WHEN comb_id = 3 THEN consumo END) AS 'gnv', " +
                "MAX(CASE WHEN comb_id = 4 THEN consumo END) AS 'diesel' " +
                "from cars left join consumos on cars._id = consumos.car_id " +
                "left join combustiveis on combustiveis._id = consumos.comb_id where car_id = ?  group by profile_name", new String[]{String.valueOf(getDefaultCar())});
        if (cursor.moveToFirst()) {
            valores[0] = cursor.getDouble(cursor.getColumnIndex("gasolina"));
            valores[1] = cursor.getDouble(cursor.getColumnIndex("Etanol"));
            valores[2] = cursor.getDouble(cursor.getColumnIndex("tanque_comb"));
            valores[3] = cursor.getDouble(cursor.getColumnIndex("gnv"));
            valores[4] = cursor.getDouble(cursor.getColumnIndex("tanque_gnv"));
            valores[5] = cursor.getDouble(cursor.getColumnIndex("diesel"));
        }
        cursor.close();
        return valores;
    }


    public Vehicles fetchOneCar(long carid) {
        Vehicles vehicles = new Vehicles();
        Cursor cursor;

        cursor = database.rawQuery("select cars._id  as _id, icon_id, profile_name, tanque_comb, tanque_gnv,  " +
                "MAX(CASE WHEN comb_id = 1 THEN consumo END) AS 'gasolina', " +
                "MAX(CASE WHEN comb_id = 2 THEN consumo END) AS 'etanol', " +
                "MAX(CASE WHEN comb_id = 3 THEN consumo END) AS 'gnv', " +
                "MAX(CASE WHEN comb_id = 4 THEN consumo END) AS 'diesel' " +
                "from cars left join consumos on cars._id = consumos.car_id " +
                "left join combustiveis on combustiveis._id = consumos.comb_id where cars._id = ? group by profile_name", new String[]{String.valueOf(carid)});

        if (cursor.moveToFirst()) {
            Vehicles add = new Vehicles(
                    cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID)),
                    cursor.getPosition(),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ICON_ID)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_TQ_GNV)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_GASOLINA)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_ETANOL)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_GNV)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.KEY_DIESEL)));
            return add;
        }
        cursor.close();

        return vehicles;
    }

    public Cursor fetchCursorOneCar(long carid) {
        return database.rawQuery("select cars._id  as _id, icon_id, profile_name, tanque_comb, tanque_gnv,  " +
                "MAX(CASE WHEN comb_id = 1 THEN consumo END) AS 'gasolina', " +
                "MAX(CASE WHEN comb_id = 2 THEN consumo END) AS 'etanol', " +
                "MAX(CASE WHEN comb_id = 3 THEN consumo END) AS 'gnv', " +
                "MAX(CASE WHEN comb_id = 4 THEN consumo END) AS 'diesel' " +
                "from cars left join consumos on cars._id = consumos.car_id " +
                "left join combustiveis on combustiveis._id = consumos.comb_id where cars._id = ? group by profile_name", new String[]{String.valueOf(carid)});
    }

    public Cursor getOneFuel(long histid) {
        return database.rawQuery("SELECT strftime('%d/%m/%Y',created_date) as created_date, strftime('%H:%M', created_date) as created_time, price_lt, place_id, comb_id, combustivel, km, litros, history._id as _id, partial, proportion from history left join combustiveis on combustiveis._id = comb_id  where history._id  = ?", new String[]{String.valueOf(histid)});
    }

    public Cursor getOneOil(long histid) {
        return database.rawQuery("SELECT strftime('%d/%m/%Y',created_date) as created_date, strftime('%H:%M', created_date) as created_time, price_lt, km, price_lt,next_km, litros, has_filter, oil_name, oilhistory._id as _id from oilhistory left join cars on cars._id = car_id  where oilhistory._id = ?", new String[]{String.valueOf(histid)});
    }

    public double[] getMaxFuelCapacity() {
        double[] ret = {0.0, 0.0};
        Cursor cursor;
        cursor = database.rawQuery("select tanque_comb,tanque_gnv from cars where _id = ?", new String[]{String.valueOf(getDefaultCar())});

        if (cursor.moveToFirst()) {
            ret[0] = cursor.getInt(cursor.getColumnIndex("tanque_comb"));
            ret[1] = cursor.getInt(cursor.getColumnIndex("tanque_gnv"));
        }
        cursor.close();

        return ret;
    }

    public long[] getRefuelIntevalKM(String date, String carid, String thisid) {
        long[] ret = {0, 0};
        Cursor cursor;

        String dateTime = setDateTimeToDB(date);
        SaveShowCalculos calculos = new SaveShowCalculos(getInstance());


        cursor = database.rawQuery("select MAX(km) as km_min from history where created_date < ? and car_id = ? and _id != ?", new String[]{dateTime, carid, thisid});

        if (cursor.moveToFirst()) {
            ret[0] = calculos.getShowOdometroValue(cursor.getDouble(cursor.getColumnIndex("km_min")));
        }
        cursor.close();


        cursor = database.rawQuery("select MIN(km) as km_max from history where created_date > ? and car_id = ? and _id != ?", new String[]{dateTime, carid, thisid});

        if (cursor.moveToFirst()) {
            ret[1] = calculos.getShowOdometroValue(cursor.getDouble(cursor.getColumnIndex("km_max")));
        }
        cursor.close();

        Log.d(getClass().getSimpleName(), " Refuel RET ==> " + ret[0] + " - " + ret[1]);

        if (ret[0] == ret[1]) {
            ret[1] = 0;
        }
        return ret;
    }

    public long[] getOilIntevalKM(String date, String carid, String thisid) {
        long[] ret = {0, 0};
        Cursor cursor;

        String dateTime = setDateTimeToDB(date);
        SaveShowCalculos calculos = new SaveShowCalculos(getInstance());

        cursor = database.rawQuery("select MAX(km) as km_min from oilhistory where created_date <= ? and car_id = ? and _id != ?", new String[]{dateTime, carid, thisid});

        if (cursor.moveToFirst()) {
            ret[0] = calculos.getShowOdometroValue(cursor.getDouble(cursor.getColumnIndex("km_min")));
        }
        cursor.close();

        cursor = database.rawQuery("select MIN(km) as km_max from oilhistory where created_date >= ? and car_id = ? and _id != ?", new String[]{dateTime, carid, thisid});

        if (cursor.moveToFirst()) {
            ret[1] = calculos.getShowOdometroValue(cursor.getDouble(cursor.getColumnIndex("km_max")));
        }
        cursor.close();

        Log.d(getClass().getSimpleName(), " Oil RET ==> " + ret[0] + " - " + ret[1]);

        if (ret[0] == ret[1]) {
            ret[1] = 0;
        }
        return ret;
    }

    public boolean updateOil(long id, String name, double price, long km1, long km2, String data, double litros, boolean filter, long place_id) {
        ContentValues values = new ContentValues();
        saveshowcalculos = new SaveShowCalculos(getInstance());

        String dateTime = setDateTimeToDB(data);

        values.put(SQLiteHelper.HIST_OIL_NAME, name);
        values.put(SQLiteHelper.HIST_OIL_PRICE, price);
        values.put(SQLiteHelper.HIST_OIL_KM, saveshowcalculos.getSaveOdometro(km1));
        values.put(SQLiteHelper.HIST_OIL_NEXT_KM, saveshowcalculos.getSaveOdometro(km2));
        values.put(SQLiteHelper.HIST_OIL_DATE, dateTime);
        values.put(SQLiteHelper.HIST_OIL_FILTER, filter);
        values.put(SQLiteHelper.HIST_OIL_LTS, saveshowcalculos.getSaveVolume(litros));
        values.put(SQLiteHelper.PLACE_ID, place_id);

        try {
            database.update(SQLiteHelper.DATABASE_OIL, values, " _id = ? ", new String[]{String.valueOf(id)});
            backupManager.dataChanged();
            return true;
        } catch (SQLiteConstraintException e) {
            Log.d(getClass().getSimpleName(), "ERROR IS --> " + e.toString() + " MESSAGE --> " + e.getMessage());
            if (e.getMessage().contains("UNIQUE constraint failed: history.created_date")) {
                Toast.makeText(instance, R.string.existe_oil_datetime, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(instance, R.string.registro_existente, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public boolean updateCharge(long id, double litros, long km, int type, String data, double price, boolean partial, int progress, long place_id) {
        ContentValues values = new ContentValues();
        saveshowcalculos = new SaveShowCalculos(getInstance());

        String dateTime = setDateTimeToDB(data);

        values.put(SQLiteHelper.HIST_DATE, dateTime);
        values.put(SQLiteHelper.KEY_COMB_ID, type);
        values.put(SQLiteHelper.HIST_PRICE, price);
        values.put(SQLiteHelper.PLACE_ID, place_id);
        if (partial && progress < 100) {
            values.put(SQLiteHelper.HIST_PARTIAL, partial);
            values.put(SQLiteHelper.HIST_PROPORTION, progress);
        } else {
            values.put(SQLiteHelper.HIST_PARTIAL, false);
            values.put(SQLiteHelper.HIST_PROPORTION, 0);
        }
        values.put(SQLiteHelper.HIST_KM, saveshowcalculos.getSaveOdometro(km));
        if (type == 3) {
            values.put(SQLiteHelper.HIST_LTS, saveshowcalculos.getSaveVolumeGNV(litros));
        } else {
            values.put(SQLiteHelper.HIST_LTS, saveshowcalculos.getSaveVolume(litros));
        }

        try {
            database.update(SQLiteHelper.DATABASE_HISTORY, values, " _id = ? ", new String[]{String.valueOf(id)});
            backupManager.dataChanged();
            updateConsumo();
            return true;
        } catch (SQLiteConstraintException e) {
            Log.d(getClass().getSimpleName(), "ERROR IS --> " + e.toString() + " MESSAGE --> " + e.getMessage());
            if (e.getMessage().contains("UNIQUE constraint failed: history.created_date")) {
                Toast.makeText(instance, R.string.existe_charge_datetime, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(instance, R.string.registro_existente, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public boolean updateCar(long id, boolean is_default, String name, double tq, double tq_gnv, String iconid, Bitmap bmp) {
        ContentValues values = new ContentValues();
        saveshowcalculos = new SaveShowCalculos(getInstance());

        values.put(SQLiteHelper.KEY_NAME, name);
        values.put(SQLiteHelper.KEY_TQ, saveshowcalculos.getSaveVolume(tq));
        values.put(SQLiteHelper.KEY_TQ_GNV, saveshowcalculos.getSaveVolumeGNV(tq_gnv));
        values.put(SQLiteHelper.KEY_ICON_ID, iconid);

        try {
            database.update(SQLiteHelper.DATABASE_TABLE, values, " _id = ? ", new String[]{String.valueOf(id)});
            if (is_default) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
                preferences.edit().putLong(SQLiteHelper.ID_DEFAULT, id).apply();
            }
            DbBitmapUtility.savebitmap(this, bmp, id);
            backupManager.dataChanged();
            return true;
        } catch (SQLiteConstraintException | IOException e) {
            Toast.makeText(instance, R.string.erro_atualizar, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void updateManualConsumo(long id, ArrayList<Consumos> lista) {
        saveshowcalculos = new SaveShowCalculos(getInstance());

        for (Consumos consumo : lista) {
            ContentValues values = new ContentValues();
            ContentValues values_ins = new ContentValues();

            values_ins.put(SQLiteHelper.KEY_CAR_ID, id);
            values_ins.put(SQLiteHelper.KEY_COMB_ID, consumo.combustivel);
            if (consumo.combustivel == 3) {
                values_ins.put(SQLiteHelper.KEY_CONSUMO, saveshowcalculos.getSaveConsumoGNV(consumo.consumo));
            } else {
                values_ins.put(SQLiteHelper.KEY_CONSUMO, saveshowcalculos.getSaveConsumo(consumo.consumo));
            }
            values.put(SQLiteHelper.KEY_CONSUMO, consumo.consumo);

            try {
                long ins_id = database.update(SQLiteHelper.DATABASE_COMSUMOS, values, "car_id = ? and  comb_id = ? ", new String[]{String.valueOf(id), String.valueOf(consumo.combustivel)});
                if (ins_id == 0) {
                    ins_id = database.insertOrThrow(SQLiteHelper.DATABASE_COMSUMOS, null, values_ins);
                }
                backupManager.dataChanged();
            } catch (SQLiteConstraintException e) {
                Toast.makeText(instance, R.string.erro_atualizar, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void deleteCar(long mid) {
        String id = Long.toString(mid);
        Cursor cursor;
        ArrayList<Integer> ids = new ArrayList<Integer>();
        cursor = database.rawQuery("SELECT _id from maintenance where car_id = ?", new String[]{id});
        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_ID)));
        }
        cursor.close();

        try {
            database.delete(SQLiteHelper.DATABASE_TABLE, " _id = ? ", new String[]{id});
            long defaultCar = getDefaultCar();
            if (defaultCar == mid) {
                //setDefaultCar();
            }
            database.delete(SQLiteHelper.DATABASE_HISTORY, "car_id = ? ", new String[]{id});
            database.delete(SQLiteHelper.DATABASE_COMSUMOS, "car_id = ? ", new String[]{id});
            database.delete(SQLiteHelper.DATABASE_OIL, "car_id = ? ", new String[]{id});
            database.delete(SQLiteHelper.DATABASE_MAINTENANCE, " car_id = ? ", new String[]{id});
            for (int pid : ids) {
                database.delete(SQLiteHelper.DATABASE_MAINTENANCE_PARTS, "maintenance_id = ? ", new String[]{String.valueOf(pid)});
            }
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.erro_deletar, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteOil(String id) {
        try {
            database.delete(SQLiteHelper.DATABASE_OIL, " _id = ? ", new String[]{id});
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.erro_deletar, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteHist(String id) {
        try {
            database.delete(SQLiteHelper.DATABASE_HISTORY, " _id = ? ", new String[]{id});
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.erro_deletar, Toast.LENGTH_LONG).show();
        }
        updateConsumo();
    }

    public ArrayList<Categories> getCategories() {
        Cursor cursor;
        ArrayList<Categories> data = new ArrayList<Categories>();
        cursor = database.query(SQLiteHelper.DATABASE_MAINTENANCE_CATEG, new String[]{SQLiteHelper.MAINTENANCE_CATEG_NAME, SQLiteHelper.KEY_ROWID}, null, null, null, null, null);

        data.add(new Categories(getString(R.string.geral), R.drawable.ic_menu_car, 0));
        while (cursor.moveToNext()) {
            int keyid = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
            if (keyid == 1) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_engine, keyid));
            } else if (keyid == 2) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_susp, keyid));
            } else if (keyid == 3) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_exaust, keyid));
            } else if (keyid == 4) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_tires, keyid));
            } else if (keyid == 5) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_tape, keyid));
            } else if (keyid == 6) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_eletric, keyid));
            } else if (keyid == 7) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_eletronic, keyid));
            } else if (keyid == 8) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_body, keyid));
            } else if (keyid == 9) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_shift, keyid));
            } else if (keyid == 10) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_brakes, keyid));
            } else if (keyid == 11) {
                data.add(keyid, new Categories(cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_CATEG_NAME)), R.drawable.ico_man_wheel, keyid));
            }
        }
        Log.d(getClass().getSimpleName(), "############## GET ALL CATEGORIES ###########");

        cursor.close();
        return data;
    }

    public void deleteMaintenance(long mid) {
        String id = Long.toString(mid);
        try {
            database.delete(SQLiteHelper.DATABASE_MAINTENANCE, " _id = ? ", new String[]{id});
            database.delete(SQLiteHelper.DATABASE_MAINTENANCE_PARTS, "maintenance_id = ? ", new String[]{id});
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.erro_deletar, Toast.LENGTH_LONG).show();
        }
    }


    public long createMaintenance(String name, long km, String data, long place_id) {
        ContentValues values = new ContentValues();
        saveshowcalculos = new SaveShowCalculos(getInstance());

        String dateTime = setDateTimeToDB(data);
        long defaultCar = getDefaultCar();

        long id = 0;

        values.put(SQLiteHelper.MAINTENANCE_NAME, name);
        values.put(SQLiteHelper.MAINTENANCE_CARID, defaultCar);
        values.put(SQLiteHelper.MAINTENANCE_KM, saveshowcalculos.getSaveOdometro(km));
        values.put(SQLiteHelper.MAINTENANCE_DATE, dateTime);
        values.put(SQLiteHelper.PLACE_ID, place_id);

        try {
            id = database.insertOrThrow(SQLiteHelper.DATABASE_MAINTENANCE, null, values);
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.nome_modelo_existente, Toast.LENGTH_LONG).show();
        }
        return id;
    }

    public void createMaintenanceParts(long id, ArrayList<MaintenanceParts> lista) {
        for (MaintenanceParts maintenance : lista) {
            ContentValues values = new ContentValues();

            values.put(SQLiteHelper.MAINTENANCE_PARTS_NAME, maintenance.getPartname());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_QTE, maintenance.getQuantd());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_PRICE, maintenance.getPrice());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_CATGID, maintenance.getCatg_id());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_MAINID, id);

            try {
                long ins_id = database.insertOrThrow(SQLiteHelper.DATABASE_MAINTENANCE_PARTS, null, values);
                backupManager.dataChanged();
            } catch (SQLiteConstraintException e) {
                Toast.makeText(instance, R.string.consumo_existente, Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean updateMaintenance(long id, String name, long km, String data, long place_id) {
        ContentValues values = new ContentValues();
        saveshowcalculos = new SaveShowCalculos(getInstance());

        String dateTime = setDateTimeToDB(data);

        values.put(SQLiteHelper.MAINTENANCE_NAME, name);
        values.put(SQLiteHelper.MAINTENANCE_KM, saveshowcalculos.getSaveOdometro(km));
        values.put(SQLiteHelper.MAINTENANCE_DATE, dateTime);
        values.put(SQLiteHelper.PLACE_ID, place_id);
        try {
            database.update(SQLiteHelper.DATABASE_MAINTENANCE, values, " _id = ? ", new String[]{String.valueOf(id)});
            backupManager.dataChanged();
            return true;
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.erro_atualizar, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void deleteAllMaintenanceParts(long id) {
        database.delete(SQLiteHelper.DATABASE_MAINTENANCE_PARTS, " maintenance_id = ? ", new String[]{String.valueOf(id)});
    }

    public void deleteAllCarsModels() {
        int f = database.delete(SQLiteHelper.DATABASE_CAR_MODELS, "1", null);
        Log.d(getClass().getSimpleName(), "##### DELETED "+ f +"#######");
    }

    public void updateMaintenanceParts(long id, ArrayList<MaintenanceParts> lista) {
        createMaintenanceParts(id, lista);
	    /*
        for (MaintenanceParts maintenance : lista) {
            ContentValues values = new ContentValues();
            ContentValues values_ins = new ContentValues();

            values.put(SQLiteHelper.MAINTENANCE_PARTS_NAME, maintenance.getPartname());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_QTE, maintenance.getQuantd());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_PRICE, maintenance.getPrice());
            values.put(SQLiteHelper.MAINTENANCE_PARTS_CATGID, maintenance.getCatg_id());

            values_ins.put(SQLiteHelper.MAINTENANCE_PARTS_NAME, maintenance.getPartname());
            values_ins.put(SQLiteHelper.MAINTENANCE_PARTS_QTE, maintenance.getQuantd());
            values_ins.put(SQLiteHelper.MAINTENANCE_PARTS_PRICE, maintenance.getPrice());
            values_ins.put(SQLiteHelper.MAINTENANCE_PARTS_CATGID, maintenance.getCatg_id());
            values_ins.put(SQLiteHelper.MAINTENANCE_PARTS_MAINID, id);

            try {
                if(maintenance.getId() > 0){
                    database.update(SQLiteHelper.DATABASE_MAINTENANCE_PARTS, values, "maintenance_id = ? and  _id = ? ", new String[]{String.valueOf(id), String.valueOf(maintenance.getId())});
                }else{
                    database.insertOrThrow(SQLiteHelper.DATABASE_MAINTENANCE_PARTS, null, values_ins);
                }
                backupManager.dataChanged();
            } catch (SQLiteConstraintException e) {
                Toast.makeText(instance, R.string.erro_atualizar, Toast.LENGTH_LONG).show();
            }
        }
        */
    }


    public ArrayList<Maintenances> getAllMaintenances() {
        ArrayList<Categories> maintenanceAllCategories = getCategories();
        long defaultCar = getDefaultCar();

        String SQL = "select distinct maintenance._id, " +
                "strftime('%d',created_date) as only_day," +
                "strftime('%m',created_date) as only_month, " +
                "strftime('%Y',created_date) as only_year, " +
                "strftime('%d/%m/%Y',created_date) as created_date, strftime('%H:%M', created_date) as created_time, " +
                " company_name,km,obs,car_id," +
                " group_concat( maintenance_parts._id , '^') as part_id, " +
                " group_concat(maintenance_parts.name,'^') as part_name, group_concat(qtde,'^') as qtde, " +
                "group_concat(price,'^') as price, group_concat(catg_id,'^') as catg_id , " +
                "group_concat((CASE WHEN catg_id = 0 THEN '" + getString(R.string.geral) + "' ELSE maintenance_catg.name END),'^') as catg_name " +
                "from maintenance join maintenance_parts on maintenance._id = maintenance_parts.maintenance_id " +
                "left join maintenance_catg on maintenance_catg._id = maintenance_parts.catg_id where car_id = ?  group by maintenance._id order by km desc";

        String lastDate = "";
        int headerId = 0;
        Cursor cursor;
        ArrayList<Maintenances> data = new ArrayList<>();
        cursor = database.rawQuery(SQL, new String[]{String.valueOf(defaultCar)});
        while (cursor.moveToNext()) {
            String year = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_YEAR));
            String month = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_MONTH));
            String day = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_DAY));
            if (!lastDate.equals(year + month))
                headerId = cursor.getPosition();
            long keyid = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
            ArrayList<MaintenanceParts> p = new ArrayList<>();
            String Wparts = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_NAME_RAW));
            String Wqtde = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_QTE));
            String Wprice = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_PRICE));
            String Wcateg = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_CATGID));
            String Wcat_name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_CATGNAME));
            String Wpart_id = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_ID));

            String[] parts = Wparts.split("\\^");
            String[] qtde = Wqtde.split("\\^");
            String[] price = Wprice.split("\\^");
            String[] categ = Wcateg.split("\\^");
            String[] cat_name = Wcat_name.split("\\^");
            String[] part_id = Wpart_id.split("\\^");

            TreeMap<Categories, ArrayList<MaintenanceParts>> ppp = new TreeMap<>();

            for (int n = 0; n < parts.length; n++) {
                Log.d(getClass().getSimpleName(), String.format("NUM %d - ==[%s]== ==[%s]== ==[%s]== ==[%s]== ==[%d]== ==[%s]==", n, parts[n], qtde[n], price[n], categ[n], 0, cat_name[n]));
                ArrayList<MaintenanceParts> mp = ppp.get(maintenanceAllCategories.get(Integer.valueOf(categ[n])));
                if (mp == null)
                    mp = new ArrayList<>();
                mp.add(new MaintenanceParts(parts[n], qtde[n], price[n], categ[n], maintenanceAllCategories.get(Integer.valueOf(categ[n])).getImage(), part_id[n], cat_name[n]));
                ppp.put(maintenanceAllCategories.get(Integer.valueOf(categ[n])), mp);
            }

            data.add(new Maintenances(
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_NAME)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_DATE)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_TIME)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_KM)),
                    keyid,
                    headerId,
                    year,
                    month,
                    day,
                    ppp));
            lastDate = year + month;
        }
        cursor.close();

        return data;
    }

    public Maintenances getOneMaintenance(long id) {
        ArrayList<Categories> maintenanceAllCategories = getCategories();
        TreeMap<Categories, ArrayList<MaintenanceParts>> ppp = new TreeMap<>();


        String SQL = "select distinct maintenance._id, " +
                "strftime('%d',created_date) as only_day," +
                "strftime('%m',created_date) as only_month, " +
                "strftime('%Y',created_date) as only_year, " +
                "strftime('%d/%m/%Y',created_date) as created_date, strftime('%H:%M', created_date) as created_time, " +
                " company_name,km,obs,car_id," +
                " group_concat( maintenance_parts._id , '^') as part_id, " +
                " group_concat(maintenance_parts.name,'^') as part_name, group_concat(qtde,'^') as qtde, " +
                "group_concat(price,'^') as price, group_concat(catg_id,'^') as catg_id , " +
                "group_concat((CASE WHEN catg_id = 0 THEN '" + getString(R.string.geral) + "' ELSE maintenance_catg.name END),'^') as catg_name " +
                "from maintenance join maintenance_parts on maintenance._id = maintenance_parts.maintenance_id " +
                "left join maintenance_catg on maintenance_catg._id = maintenance_parts.catg_id where maintenance._id = ?  group by maintenance._id;";

        String lastDate = "";
        int headerId = 0;
        Cursor cursor;
        cursor = database.rawQuery(SQL, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String year = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_YEAR));
        String month = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_MONTH));
        String day = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ONLY_DAY));
        if (!lastDate.equals(year + month))
            headerId = cursor.getPosition();
        long keyid = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
        ArrayList<MaintenanceParts> p = new ArrayList<>();
        String Wparts = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_NAME_RAW));
        String Wqtde = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_QTE));
        String Wprice = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_PRICE));
        String Wcateg = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_CATGID));
        String Wcat_name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_CATGNAME));
        String Wpart_id = cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_PARTS_ID));


        String[] parts = Wparts.split("\\^");
        String[] qtde = Wqtde.split("\\^");
        String[] price = Wprice.split("\\^");
        String[] categ = Wcateg.split("\\^");
        String[] cat_name = Wcat_name.split("\\^");
        String[] part_id = Wpart_id.split("\\^");


        for (int n = 0; n < parts.length; n++) {
            Log.d(getClass().getSimpleName(), String.format("NUM %d - ==[%s]== ==[%s]== ==[%s]== ==[%s]== ==[%d]== ==[%s]==", n, parts[n], qtde[n], price[n], categ[n], 0, cat_name[n]));
            ArrayList<MaintenanceParts> mp = ppp.get(maintenanceAllCategories.get(Integer.valueOf(categ[n])));
            if (mp == null)
                mp = new ArrayList<>();
            mp.add(new MaintenanceParts(parts[n], qtde[n], price[n], categ[n], maintenanceAllCategories.get(Integer.valueOf(categ[n])).getImage(), part_id[n], cat_name[n]));
            ppp.put(maintenanceAllCategories.get(Integer.valueOf(categ[n])), mp);
        }

        Maintenances data = new Maintenances(
                cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_NAME)),
                cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_DATE)),
                cursor.getString(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_TIME)),
                cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.MAINTENANCE_KM)),
                keyid,
                headerId,
                year,
                month,
                day,
                ppp);
        cursor.close();
        lastDate = year + month;

        return data;
    }

    public long[] getMaintenanceIntevalKM(String date, String carid, String thisid) {
        long[] ret = {0, 0};
        Cursor cursor;

        String dateTime = setDateTimeToDB(date);
        SaveShowCalculos calculos = new SaveShowCalculos(getInstance());

        cursor = database.rawQuery("select MAX(km) as km_min from maintenance where created_date <= ? and car_id = ? and _id != ?", new String[]{dateTime, carid, thisid});

        if (cursor.moveToFirst()) {
            ret[0] = calculos.getShowOdometroValue(cursor.getDouble(cursor.getColumnIndex("km_min")));
        }
        cursor.close();

        cursor = database.rawQuery("select MIN(km) as km_max from maintenance where created_date >= ? and car_id = ? and _id != ?", new String[]{dateTime, carid, thisid});

        if (cursor.moveToFirst()) {
            ret[1] = calculos.getShowOdometroValue(cursor.getDouble(cursor.getColumnIndex("km_max")));
        }
        cursor.close();

        Log.d(getClass().getSimpleName(), " Maintenance RET ==> " + ret[0] + " - " + ret[1]);

        if (ret[0] == ret[1]) {
            ret[1] = 0;
        }
        return ret;
    }

    public long createPlace(DetectedPlace place) {
        long placeid = getPlace(place.getPlaceId());
        if (placeid > 0) {
            return placeid;
        }
        ContentValues values = new ContentValues();
        long id = 0;
        List<Place.Type> places_type = place.getPlaces_type();
        String place_type = "Desconhecido";

        for (Place.Type type : places_type) {
            if (type == Place.Type.GAS_STATION) {
                place_type = "Posto";
            } else if (type == Place.Type.CAR_REPAIR) {
                place_type = "Oficina";
            } else if (type == Place.Type.CAR_WASH) {
                place_type = "Lava-rápido";
            }
        }
        LatLng coord = place.getCoord();

        values.put(SQLiteHelper.PLACE_ID, place.getPlaceId());
        values.put(SQLiteHelper.PLACE_LAT, coord.latitude);
        values.put(SQLiteHelper.PLACE_LONG, coord.longitude);
        values.put(SQLiteHelper.PLACE_NAME, place.getPlaceName());
        values.put(SQLiteHelper.PLACE_ADDR, place.getPlaceAddress());
        values.put(SQLiteHelper.PLACE_RAT, place.getPlaceRating());
        values.put(SQLiteHelper.PLACE_PRICE, place.getPlacePriceLevel());
        values.put(SQLiteHelper.PLACE_TYPE, place_type);

        try {
            id = database.insertOrThrow(SQLiteHelper.DATABASE_PLACE, null, values);
            backupManager.dataChanged();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(instance, R.string.nome_modelo_existente, Toast.LENGTH_LONG).show();
        }
        return id;
    }

    public DetectedPlace getPlaceMark(String id) {
        DetectedPlace mPlace = new DetectedPlace();
        Cursor cursor;
        cursor = database.rawQuery("select * from place where _id = ?", new String[]{id});
        Log.d(getClass().getSimpleName(), "Search for Place --> " + id);

        if (cursor.moveToFirst()) {
            List<Place.Type> ll = new ArrayList<>();
            String type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.PLACE_TYPE));
            switch (type) {
                case "Posto":
                    ll.add(Place.Type.GAS_STATION);
                    break;
                case "Oficina":
                    ll.add(Place.Type.CAR_REPAIR);
                    break;
                case "Lava-rápido":
                    ll.add(Place.Type.CAR_WASH);
                    break;
            }
            mPlace = new DetectedPlace(ll,
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.PLACE_LAT)),
                    cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.PLACE_LONG)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndex(SQLiteHelper.PLACE_ADDR)),
                    cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.PLACE_RAT)),
                    cursor.getInt(cursor.getColumnIndex(SQLiteHelper.PLACE_PRICE))
            );
            Log.d(getClass().getSimpleName(), "FOUND for Place --> " + mPlace.toString());
        }
        cursor.close();

        return mPlace;
    }

    public long getPlace(String id) {
        long ret = 0;
        Cursor cursor;

        if (id == null)
            return 0;

        cursor = database.rawQuery("select _id from place where place_id = ?", new String[]{id});

        if (cursor.moveToFirst()) {
            ret = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.KEY_ROWID));
        }
        cursor.close();
        return ret;
    }


    public void updateDescriptionsDB() {
        Log.d(getClass().getSimpleName(), "###################### UPDATE Descriptions DB #########################################");
        database.execSQL("UPDATE combustiveis set combustivel = '" + getApplicationContext().getString(R.string.gasolina) + "' where _id = 1");
        database.execSQL("UPDATE combustiveis set combustivel = '" + getApplicationContext().getString(R.string.Etanol) + "' where _id = 2");
        database.execSQL("UPDATE combustiveis set combustivel = '" + getApplicationContext().getString(R.string.gnv) + "' where _id = 3");
        database.execSQL("UPDATE combustiveis set combustivel = '" + getApplicationContext().getString(R.string.diesel) + "' where _id = 4");

        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.motor) + "' where _id = 1");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.suspensao) + "' where _id = 2");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.escapamento) + "' where _id = 3");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.rodas_pneu) + "' where _id = 4");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.estofamento) + "' where _id = 5");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.eletrico) + "' where _id = 6");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.eletronico) + "' where _id = 7");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.lataria) + "' where _id = 8");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.transmissao) + "' where _id = 9");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.freios) + "' where _id = 10");
        database.execSQL("UPDATE maintenance_catg set name = '" + getApplicationContext().getString(R.string.direcao) + "' where _id = 11");
    }

    public PieData reportQtdeByFuel(String ini, String end) {
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        List<Integer> colors = new ArrayList<>();
        int total = 0;
        Cursor cursor;
        String dini = setDateTimeToDB(ini + " 00:00");
        String dend = setDateTimeToDB(end + " 23:59");

        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(0);
        parser.setRoundingMode(RoundingMode.HALF_EVEN);

        long defaultCar = getDefaultCar();
        cursor = database.rawQuery("select count(comb_id) as comb_id from history where car_id = ? and created_date between ? and ?", new String[]{String.valueOf(defaultCar), dini, dend});

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COMB_ID));
        }
        cursor.close();

        cursor = database.rawQuery("select comb_id, combustivel, count(comb_id) as total from history join combustiveis on combustiveis._id = comb_id where car_id = ? and created_date between ? and ? group by comb_id", new String[]{String.valueOf(defaultCar), dini, dend});

        while (cursor.moveToNext()) {
            int color = getReportFuelColor(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COMB_ID)));
            float value = cursor.getFloat(cursor.getColumnIndex("total"));
            double percent = (value / total) * 100;

            xVals.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COMBUSTIVEL)));
            entries1.add(new Entry(value, cursor.getCount()));
            colors.add(color);
        }
        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setColors(colors);
        ds1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.PERCENT));
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);
        PieData pieData = new PieData(xVals, ds1);
        cursor.close();

        //seta a cor dos valores no grafico
        //pieData.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        if (entries1.size() == 0 && xVals.size() == 0) {
            return null;
        }

        return pieData;
    }

    public FuelReportBar reportFuelInterval(String ini, String end) {
        List<Fuel> ll = getAllFuel(ini, end);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> lts = new ArrayList<BarEntry>();
        ArrayList<BarEntry> price = new ArrayList<BarEntry>();
        ArrayList<BarEntry> km = new ArrayList<BarEntry>();
        ArrayList<BarEntry> pkm = new ArrayList<>();
        PreferencesProcessed preferences = new PreferencesProcessed(this);
        float media = 0;
        double total_km = 0;


        int count = 0;
        int km_count = 0;
        for (Fuel fv : ll) {
            xVals.add(fv.getCreated_date());
            lts.add(new BarEntry((float) fv.getvLitros(), count));
            price.add(new BarEntry((float) fv.getvTotalPrice(), count));
            km.add(new BarEntry((float) fv.getvDiff_km(), count));
            pkm.add(new BarEntry((float) fv.getvPriceByKm(), count));
            if (fv.getvDiff_km() > 0) {
                total_km += fv.getvDiff_km();
                km_count++;
            }
            count++;
        }
        if (km_count > 1) {
            media = (float) total_km / km_count;
        }
        BarDataSet set1 = new BarDataSet(lts, getString(R.string.Litros));
        set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.VOLUME));
        set1.setColor(Color.BLUE);

        BarDataSet set2 = new BarDataSet(price, getString(R.string.Preco));
        set2.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY));
        set2.setColor(Color.GREEN);

        BarDataSet set3 = new BarDataSet(km, getString(R.string.Distancia));
        set3.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.DISTANCE));
        set3.setColor(Color.LTGRAY);

        BarDataSet set4 = new BarDataSet(pkm, getString(R.string.PrecoKM, preferences.getOdometro()));
        set4.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY));
        set4.setColor(Color.CYAN);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);

        BarData data = new BarData(xVals, dataSets);
        data.setGroupSpace(40f);
        //seta a cor dos valores no grafico
        data.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        if (xVals.size() == 0 && lts.size() == 0 && price.size() == 0 && km.size() == 0 && pkm.size() == 0) {
            return new FuelReportBar(null, media);
        }

        return new FuelReportBar(data, media);
    }

    public LineData reportTravelDistance(String ini, String end) {
        List<Fuel> ll = getAllFuel(ini, end);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        PreferencesProcessed preferences = new PreferencesProcessed(this);


        int count = 0;
        for (Fuel fv : ll) {
            xVals.add(fv.getCreated_date());
            yVals.add(new Entry((float) fv.getvDiff_km(), count));
            count++;
        }

        LineDataSet set1 = new LineDataSet(yVals, getString(R.string.dist_em_km, preferences.getOdometro()));
        set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.DISTANCE));
        set1.setLineWidth(3f);
        set1.setCircleSize(5f);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setColor(Color.GRAY);
        set1.setCircleColor(ContextCompat.getColor(getInstance(), R.color.accent));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        if (xVals.size() == 0 && yVals.size() == 0) {
            return null;
            //return new LineData(new ArrayList<String>(),new ArrayList<ILineDataSet>());
        }
        LineData data = new LineData(xVals, dataSets);

        //seta a cor dos valores no grafico
        data.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        return data;
    }

    public LineData reportConsumo(String ini, String end) {
        List<Fuel> ll = getAllFuel(ini, end);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> consumo01 = new ArrayList<Entry>();
        ArrayList<Entry> consumo02 = new ArrayList<Entry>();
        ArrayList<Entry> consumo03 = new ArrayList<Entry>();
        ArrayList<Entry> consumo04 = new ArrayList<Entry>();


        int count = 0;
        for (Fuel fv : ll) {
            xVals.add(fv.getCreated_date());
            switch (fv.getComb_id()) {
                case 1:
                    consumo01.add(new Entry((float) fv.getvKml(), count));
                    break;
                case 2:
                    consumo02.add(new Entry((float) fv.getvKml(), count));
                    break;
                case 3:
                    consumo03.add(new Entry((float) fv.getvKml(), count));
                    break;
                case 4:
                    consumo04.add(new Entry((float) fv.getvKml(), count));
                    break;
            }
            count++;
        }

        LineDataSet set1 = new LineDataSet(consumo01, getString(R.string.ConsumoGasolina));
        set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.CONSUMO));
        set1.setLineWidth(3f);
        set1.setCircleSize(5f);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setColor(ContextCompat.getColor(getInstance(), R.color.gasolina));
        set1.setCircleColor(ContextCompat.getColor(getInstance(), R.color.gasolina));

        LineDataSet set2 = new LineDataSet(consumo02, getString(R.string.ConsumoEtanol));
        set2.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.CONSUMO));
        set2.setLineWidth(3f);
        set2.setCircleSize(5f);
        set2.setValueTextSize(9f);
        set2.setFillAlpha(65);
        set2.setColor(ContextCompat.getColor(getInstance(), R.color.Etanol));
        set2.setCircleColor(ContextCompat.getColor(getInstance(), R.color.Etanol));

        LineDataSet set3 = new LineDataSet(consumo03, getString(R.string.ConsumoGnv));
        set3.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.CONSUMO));
        set3.setLineWidth(3f);
        set3.setCircleSize(5f);
        set3.setValueTextSize(9f);
        set3.setFillAlpha(65);
        set3.setColor(ContextCompat.getColor(getInstance(), R.color.gnv));
        set3.setCircleColor(ContextCompat.getColor(getInstance(), R.color.gnv));

        LineDataSet set4 = new LineDataSet(consumo04, getString(R.string.ConsumoDiesel));
        set4.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.CONSUMO));
        set3.setLineWidth(3f);
        set3.setCircleSize(5f);
        set3.setValueTextSize(9f);
        set3.setFillAlpha(65);
        set3.setColor(ContextCompat.getColor(getInstance(), R.color.diesel));
        set3.setCircleColor(ContextCompat.getColor(getInstance(), R.color.diesel));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2); // add the datasets
        dataSets.add(set3); // add the datasets
        dataSets.add(set4); // add the datasets

        if (xVals.size() == 0 && consumo01.size() == 0 && consumo02.size() == 0 && consumo03.size() == 0 && consumo04.size() == 0) {
            return null;
        }

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        //seta a cor dos valores no grafico
        data.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        return data;
    }

    public LineData reportConsumoV2(String ini, String end) {
        List<Fuel> ll = getAllFuel(ini, end);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> consumo = new ArrayList<Entry>();
        ArrayList<Integer> colors = new ArrayList<>();

        int count = 0;
        for (Fuel fv : ll) {
            xVals.add(fv.getCreated_date());
            consumo.add(new Entry((float) fv.getvKml(), count));
            switch (fv.getComb_id()) {
                case 1:
                    colors.add(ContextCompat.getColor(getInstance(), R.color.gasolina));
                    break;
                case 2:
                    colors.add(ContextCompat.getColor(getInstance(), R.color.Etanol));
                    break;
                case 3:
                    colors.add(ContextCompat.getColor(getInstance(), R.color.gnv));
                    break;
                case 4:
                    colors.add(ContextCompat.getColor(getInstance(), R.color.diesel));
                    break;
            }
            count++;
        }

        LineDataSet set = new LineDataSet(consumo, getString(R.string.consumo));
        set.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.CONSUMO));
        set.setLineWidth(3f);
        set.setCircleSize(5f);
        set.setValueTextSize(9f);
        set.setFillAlpha(65);
        set.setColor(Color.GRAY);
        set.setCircleColors(colors);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set); // add the datasets

        if (xVals.size() == 0 && consumo.size() == 0) {
            return null;
        }

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        //seta a cor dos valores no grafico
        data.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        return data;
    }


    public LineData reportFuelVolume(String ini, String end) {
        List<Fuel> ll = getAllFuel(ini, end);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        PreferencesProcessed preferences = new PreferencesProcessed(this);

        int count = 0;
        for (Fuel fv : ll) {
            xVals.add(fv.getCreated_date());
            yVals.add(new Entry((float) fv.getvLitros(), count));
            count++;
        }

        LineDataSet set1 = new LineDataSet(yVals, getString(R.string.VolumeEmLitros, preferences.getVolumeTanque()));
        set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.VOLUME));
        set1.setLineWidth(3f);
        set1.setCircleSize(5f);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setColor(Color.CYAN);
        set1.setCircleColor(ContextCompat.getColor(getInstance(), R.color.accent));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        if (xVals.size() == 0 && yVals.size() == 0) {
            return null;
        }

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        //seta a cor dos valores no grafico
        data.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        return data;
    }

    public LineData reportFuelPrice(String ini, String end) {
        List<Fuel> ll = getAllFuel(ini, end);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> fuelprice = new ArrayList<Entry>();
        ArrayList<Entry> fuelpricetotal = new ArrayList<Entry>();
        ArrayList<Entry> fuelpricebykm = new ArrayList<Entry>();
        PreferencesProcessed preferences = new PreferencesProcessed(this);


        int count = 0;
        for (Fuel fv : ll) {
            xVals.add(fv.getCreated_date());
            fuelprice.add(new Entry((float) fv.getvPrice(), count));
            fuelpricetotal.add(new Entry((float) fv.getvTotalPrice(), count));
            fuelpricebykm.add(new Entry((float) fv.getvPriceByKm(), count));
            count++;
        }

        LineDataSet set1 = new LineDataSet(fuelprice, getString(R.string.PrecoUnitario));
        set1.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY_FULL));
        set1.setLineWidth(3f);
        set1.setCircleSize(5f);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setColor(ContextCompat.getColor(getInstance(), R.color.primary_dark));
        set1.setCircleColor(ContextCompat.getColor(getInstance(), R.color.primary_dark));

        LineDataSet set2 = new LineDataSet(fuelpricetotal, getString(R.string.PrecoTotal));
        set2.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY));
        set2.setLineWidth(3f);
        set2.setCircleSize(5f);
        set2.setValueTextSize(9f);
        set2.setFillAlpha(65);
        set2.setColor(ContextCompat.getColor(getInstance(), R.color.accent));
        set2.setCircleColor(ContextCompat.getColor(getInstance(), R.color.accent));

        LineDataSet set3 = new LineDataSet(fuelpricebykm, getString(R.string.PrecoKM, preferences.getOdometro()));
        set3.setValueFormatter(new ReportValueFormatter(ReportValueFormatter.types.MONEY));
        set3.setLineWidth(3f);
        set3.setCircleSize(5f);
        set3.setValueTextSize(9f);
        set3.setFillAlpha(65);
        set3.setColor(ContextCompat.getColor(getInstance(), R.color.custo));
        set3.setCircleColor(ContextCompat.getColor(getInstance(), R.color.custo));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2); // add the datasets
        dataSets.add(set3); // add the datasets

        if (xVals.size() == 0 && fuelprice.size() == 0 && fuelpricetotal.size() == 0) {
            return null;
        }

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        //seta a cor dos valores no grafico
        data.setValueTextColor(ContextCompat.getColor(this, R.color.secondary_text));

        return data;
    }

    public PriceAndKm getAllPriceAndKmFromCar() {
        long defaultCar = getDefaultCar();
        PriceAndKm priceAndKm = new PriceAndKm();

        Cursor cursor;

        cursor = database.rawQuery("select round(sum(litros * price_lt), 2) as total, round((sum(litros * price_lt)/ round((MAX(km) - MIN(km)), 2)), 2)  as km, round((sum(litros * price_lt) / Cast ((JulianDay(MAX(created_date)) - JulianDay(MIN(created_date))) As Integer)), 2) as days, sum(litros) as litros, round((MAX(km) - MIN(km)) / sum(litros) , 2) as mediakm, (MAX(km) - MIN(km)) as totalkm from history where car_id = " + defaultCar, null);
        if (cursor.moveToFirst()) {
            priceAndKm.setPrice(cursor.getDouble(cursor.getColumnIndex("total")));
            priceAndKm.setKm(cursor.getDouble(cursor.getColumnIndex("km")));
            priceAndKm.setDays(cursor.getDouble(cursor.getColumnIndex("days")));
            priceAndKm.setLitros(cursor.getDouble(cursor.getColumnIndex("litros")));
            priceAndKm.setMediakm(cursor.getDouble(cursor.getColumnIndex("mediakm")));
            priceAndKm.setTotalkm(cursor.getDouble(cursor.getColumnIndex("totalkm")));
        }
        cursor.close();

        return priceAndKm;

    }

    public boolean needGetNewModels(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String lastSHA256 = getLastSha256FromModels();
        JSONObject response = Requestor.requestJSON(requestQueue, "https://tuxviper.com.br/models/insert_models.sha256");

        try {
            setLastSha256FromModels(response.get("sha256").toString());
            Log.d(getClass().getSimpleName(), "############## checking Models sha256 ###########");
            if (response.get("sha256").equals(lastSHA256)) {
                return false;
            }
            Log.d(getClass().getSimpleName(), "############## needGetNewModels ###########");
            Log.d(getClass().getSimpleName(), response.toString());
            return true;
        } catch (JSONException e) {
            return false;
        } catch (NullPointerException e){
            return false;
        }
    }

    public JSONObject getAllModels(){
        return Requestor.requestJSON(requestQueue, "https://tuxviper.com.br/models/insert_models.sql");
    }

    public boolean updateAllModels(JSONArray jsonArray){
        database.beginTransaction();
        try {
            deleteAllCarsModels();
            for (int v = 0; v < jsonArray.length();v++) {
                String item = jsonArray.getString(v);
                database.execSQL(item);
            }
            database.setTransactionSuccessful();
        } catch (JSONException e) {
            Log.d(getClass().getSimpleName(), "MeuCarroApplication.updateAllModels() FAILED");
        }
        database.endTransaction();
        Log.d(getClass().getSimpleName(), "MeuCarroApplication.updateAllModels() OK");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buildNotificationChannel(){
        Log.d(getClass().getSimpleName(), "MeuCarroApplication.buildNotificationChannel()");
        String channelName = getString(R.string.app_name);
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }
}

