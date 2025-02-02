package com.alexwbaule.flexprofile.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.services.UpdateAssets;

public class OnBootComplete extends BroadcastReceiver {
    private static final String CLASSNAME = "OnBootComplete";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(CLASSNAME, "*********** ONBOOT COMPLETED ***************");
            MeuCarroApplication.getInstance().StartAlarm();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(new Intent(context, UpdateAssets.class));
            }else {
                context.startForegroundService(new Intent(context, UpdateAssets.class));
            }
        }
    }
}
