package com.criticalblue.currencyconverterdemo.approov;

import android.content.Context;
import android.util.Log;

import com.criticalblue.approovsdk.Approov;

import java.net.MalformedURLException;
import java.net.URL;

public class ApproovTokenSingleton {

    private static ApproovTokenSingleton instance;
    private static final String TAG = "APPROOV";
    private final ApproovDynamicConfig approovConfig;

    private ApproovTokenSingleton(Context context) {
        this.approovConfig = new ApproovDynamicConfig(context);
        initializeSDK(context);
    }

    public static synchronized ApproovTokenSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new ApproovTokenSingleton(context);
        }
        return instance;
    }

    private void initializeSDK(Context context) {
        try {
            Approov.initialize(context.getApplicationContext(), approovConfig.readInitialConfig(), approovConfig.readDynamicConfig(), null);
        } catch (IllegalArgumentException e) {
            // this should be fatal if the SDK cannot be initialized as all subsequent attempts
            // to use the SDK will fail
            Log.e(TAG, "Approov initialization failed: " + e.getMessage());
            throw new RuntimeException("Approov Initialization failed.");
        }
    }

    public ApproovDynamicConfig getConfig() {
        return approovConfig;
    }

    public void fetchApproovTokenAndWait(HttpRequest httpRequest, String url, ActivityView activityView) {

        Log.i(TAG, "fetchApproovTokenAndWait -> url: " + url);

        Approov.TokenFetchResult approovTokenFetchResult = Approov.fetchApproovTokenAndWait(url);

        ApproovProtectedApiRequest apiRequest = new ApproovProtectedApiRequest(httpRequest, getConfig(), activityView);

        apiRequest.make(approovTokenFetchResult, url);
    }

    public void fetchApproovTokenOnBackground(HttpRequest httpRequest, String url, ActivityView activityView) {

        String apiBaseUrl = extractBaseUrl(url, activityView);

        if (apiBaseUrl == null) {
            return;
        }

        ApproovProtectedApiRequest apiRequest = new ApproovProtectedApiRequest(httpRequest, getConfig(), activityView);

        ApproovCallbackHandler approovCallback = new ApproovCallbackHandler(apiRequest, url);

        Approov.fetchApproovToken(approovCallback, apiBaseUrl);
    }

    private String extractBaseUrl(String url, ActivityView activityView) {
        try {
            URL urlParser = new URL(url);
            String apiBaseUrl = "https://" + urlParser.getAuthority();
            Log.i(TAG, "API BASE URL: " + apiBaseUrl);
            return apiBaseUrl;
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());

            activityView.handleErrorMessage("Fatal error. Please report!");
            return null;
        }
    }

    class ApproovCallbackHandler implements Approov.TokenFetchCallback {

        private final ApproovProtectedApiRequest apiRequest;
        private final String url;

        public ApproovCallbackHandler(ApproovProtectedApiRequest apiRequest, String url) {
            this.apiRequest = apiRequest;
            this.url = url;
        }

        @Override
        public void approovCallback(Approov.TokenFetchResult approovTokenFetchResult) {
            apiRequest.make(approovTokenFetchResult, url);
        }
    }
}
