package com.alexwbaule.flexprofile.network;


import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleySingleton {
    private static VolleySingleton sInstance=null;
    private RequestQueue mRequestQueue;

    private VolleySingleton(){
        mRequestQueue=Volley.newRequestQueue(MeuCarroApplication.getInstance());
    }

    public static VolleySingleton getInstance(){
        if(sInstance==null)
        {
            sInstance=new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
}