package com.alexwbaule.flexprofile.tasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.services.UpdateAssets;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateModelsAssets extends AsyncTask<Void, Void, Boolean> {
    private static final String CLASSNAME = "UpdateModelsAssets";
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(CLASSNAME, "*********** UpdateModelsAssets doInBackground STARTED ***************");

        if (rootapp.needGetNewModels()){
            JSONObject models = rootapp.getAllModels();
            try {
                rootapp.updateAllModels(models.getJSONArray("inserts"));
            } catch (JSONException e) {
                return false;
            }
        }
        Log.d(CLASSNAME, "*********** UpdateModelsAssets Finished ***************");
        rootapp.stopService(new Intent(rootapp, UpdateAssets.class));
        return true;
    }
}
