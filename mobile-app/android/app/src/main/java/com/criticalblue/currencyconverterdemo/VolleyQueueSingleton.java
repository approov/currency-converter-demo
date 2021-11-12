package com.criticalblue.currencyconverterdemo;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.Volley;

import io.approov.service.volley.ApproovService;

public class VolleyQueueSingleton {
    private static Context appContext;

    private static RequestQueue requestQueue;
    private static ApproovService approovService;

    public static synchronized void initialize(Context context) {
        appContext = context;
        approovService = new ApproovService(appContext, BuildConfig.APPROOV_CONFIG_STRING);
    }

    public static synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            BaseHttpStack httpStack = approovService.getBaseHttpStack();
            requestQueue = Volley.newRequestQueue(appContext, httpStack);
        }

        return requestQueue;
    }
}
