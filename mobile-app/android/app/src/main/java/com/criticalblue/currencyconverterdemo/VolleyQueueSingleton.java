package com.criticalblue.currencyconverterdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.criticalblue.currencyconverterdemo.approov.ApproovPinningHostnameVerifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

// @link https://developer.android.com/training/volley/requestqueue#singleton
public class VolleyQueueSingleton {

    private static VolleyQueueSingleton instance;
    private final String baseUrl;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static final String TAG = "VOLLEY_QUEUE_SINGLETON";

    private VolleyQueueSingleton(Context context, String baseUrl) {
        this.baseUrl = baseUrl;
        this.requestQueue = getRequestQueue(context);

        imageLoader = new ImageLoader(requestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
    }

    public static synchronized VolleyQueueSingleton getInstance(Context context, String baseUrl) {
        if (instance == null) {
            instance = new VolleyQueueSingleton(context, baseUrl);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(Context context) {

        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(context, new HurlStack() {

                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {

                    ApproovPinningHostnameVerifier pinningHostnameVerifier = new ApproovPinningHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setHostnameVerifier(pinningHostnameVerifier);

                    return urlConnection;
                }
            });
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
