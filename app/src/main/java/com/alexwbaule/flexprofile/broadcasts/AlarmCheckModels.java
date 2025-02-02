package com.alexwbaule.flexprofile.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.services.UpdateAssets;

public class AlarmCheckModels extends BroadcastReceiver {
    private static final String CLASSNAME = "AlarmManager";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(CLASSNAME, "*********** AlarmCheckModels Received "+intent.getAction()+"***************");
        if (intent.getAction().equals(MeuCarroApplication.ACTION_UPDATE_INTENT)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(new Intent(context, UpdateAssets.class));
            } else {
                context.startForegroundService(new Intent(context, UpdateAssets.class));
            }
        }
    }
}
