package com.alexwbaule.flexprofile.services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.broadcasts.AlarmCheckModels;
import com.alexwbaule.flexprofile.tasks.UpdateModelsAssets;


public class UpdateAssets extends Service {
    private static final String CLASSNAME = "UpdateAssets";
    private static final int ID_SERVICE = 2;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private Notification notification;
    private NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Log.d(CLASSNAME, "*********** SERVICE CREATED ***************");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(CLASSNAME, "*********** SERVICE STARTED ***************");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = createNotification();
            startForeground(ID_SERVICE, notification);
        }
        new UpdateModelsAssets().execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nm.cancel(ID_SERVICE);
        Log.d(CLASSNAME, "*********** SERVICE DESTROYED ***************");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @TargetApi(Build.VERSION_CODES.N)
    private Notification createNotification(){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MeuCarroApplication.NOTIFICATION_CHANNEL_ID);
        return notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_menu_car)
                .setContentTitle(getString(R.string.update_models))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }
}
