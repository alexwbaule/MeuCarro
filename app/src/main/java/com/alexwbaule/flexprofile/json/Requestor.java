package com.alexwbaule.flexprofile.json;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Requestor {
    private static final String CLASSNAME = "Requestor";

    public static JSONObject requestJSON(RequestQueue requestQueue, String url) {
        JSONObject response = null;
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, requestFuture, requestFuture);
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d(CLASSNAME, "InterruptedException " + e.getMessage());
        } catch (ExecutionException e) {
            Log.d(CLASSNAME, "ExecutionException " + e.getMessage());
        } catch (TimeoutException e) {
            Log.d(CLASSNAME, "TimeoutException " + e.getMessage());
        }
        return response;
    }
}

