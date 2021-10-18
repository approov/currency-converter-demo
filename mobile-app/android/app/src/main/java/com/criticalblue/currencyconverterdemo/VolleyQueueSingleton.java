package com.criticalblue.currencyconverterdemo;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyQueueSingleton {
    private static Context appContext;

    private static RequestQueue requestQueue;

    public static synchronized void initialize(Context context) {
        appContext = context;
    }

    public static synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(appContext);
        }
        return requestQueue;
    }
}
