package com.criticalblue.currencyconverterdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.datatheorem.android.trustkit.TrustKit;

import java.net.MalformedURLException;
import java.net.URL;

// @link https://developer.android.com/training/volley/requestqueue#singleton
public class VolleyQueueSingleton {

    private static VolleyQueueSingleton instance;
    private final String baseUrl;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;
    private static final String LOG_TAG = "VOLLEY QUEUE SINGLETON";

    private VolleyQueueSingleton(Context context, String baseUrl) {
        this.ctx = context;
        this.baseUrl = baseUrl;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized VolleyQueueSingleton getInstance(Context context, String baseUrl) {
        if (instance == null) {
            instance = new VolleyQueueSingleton(context, baseUrl);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {

        if (requestQueue == null) {

//            TrustKit.initializeWithNetworkSecurityConfiguration(this.ctx);

            String serverHostname = null;

            try {
                URL url = new URL(baseUrl);
                serverHostname = url.getHost();
                Log.i(LOG_TAG, "Server Hostname: " + serverHostname);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

//            requestQueue = Volley.newRequestQueue(this.ctx, new HurlStack(null, TrustKit.getInstance().getSSLSocketFactory(serverHostname)));
            requestQueue = Volley.newRequestQueue(this.ctx);
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setShouldCache(false);
        this.requestQueue.getCache().clear();
        this.requestQueue.add(req);
    }
}
