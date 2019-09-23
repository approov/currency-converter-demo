package com.criticalblue.currencyconverterdemo.approov;

import android.content.Context;

import org.json.JSONObject;

public interface ActivityView {
    Context getContext();

    void handleErrorMessage(String message);

    void handleResponse(JSONObject response);
}
