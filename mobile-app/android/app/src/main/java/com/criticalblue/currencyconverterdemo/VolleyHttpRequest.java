package com.criticalblue.currencyconverterdemo;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.criticalblue.currencyconverterdemo.approov.ActivityView;
import com.criticalblue.currencyconverterdemo.approov.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class VolleyHttpRequest implements HttpRequest {

    private static final String TAG = "VOLLEY_HTTP_REQUEST";
    private final ActivityView activityView;
    private final String apiKey;
    private final Context context;

    public VolleyHttpRequest(ActivityView activityView, String apiKey) {
        this.context = activityView.getContext();
        this.activityView = activityView;
        this.apiKey = apiKey;
    }

    public void get(String url, String approovToken) {

        Log.i(TAG, "API URL: " + url);

        JsonObjectRequest currencyConversionRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        activityView.handleResponse(response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.toString();
                        Log.e(TAG, message);

                        NetworkResponse networkResponse = error.networkResponse;

                        if (networkResponse != null) {
                            int statusCode = networkResponse.statusCode;
                            activityView.handleErrorMessage("API server status code response: " + statusCode);
                            return;
                        }

                        activityView.handleErrorMessage(message);
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();

                Log.i(TAG, "API KEY: " + apiKey);
                headers.put("Api-Key", apiKey);

                Log.i(TAG, "APPROOV TOKEN: " + approovToken);
                headers.put("Approov-Token", approovToken);

                return headers;
            }
        };

        URL urlParser = null;
        try {
            urlParser = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String apiBaseUrl = "https://" + urlParser.getAuthority();
        Log.i(TAG, "API BASE URL: " + apiBaseUrl);

        // We don't want Volley to retry requests that return an http status code that represents an
        // error, like a 401.
        currencyConversionRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // The singleton instance returned contains the custom ApproovPinningHostnameVerifier, that
        // will be responsible to perform the certificate pinning after a succesefull hostname verification.
        VolleyQueueSingleton.getInstance(context, apiBaseUrl).addToRequestQueue(currencyConversionRequest, context);
    }
}
