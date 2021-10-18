package com.criticalblue.currencyconverterdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText fromCurrency;
    private EditText toCurrency;
    private EditText currencyValueToConvert;

    private TextView currencyConvertedValue;
    private TextView errorMessage;

    private static final String LOG_TAG = "CURRENCY_CONVERTER_DEMO";

    // In a production app this url should not be stored in the code.
    //private String apiBaseUrl = "https://free.currencyconverterapi.com/api/v6/convert?";
    private String apiBaseUrl = "https://currency-converter.demo.approov.io";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("currencyconverterdemo");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getApiKey();

    public void convertCurrency(View v) {

        clearError();

        clearCurrencyConvertedValue();

        closeKeyboard();

        getCurrencyConversion(
            fromCurrency.getText().toString(),
            toCurrency.getText().toString(),
            currencyValueToConvert.getText().toString()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        VolleyQueueSingleton.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        this.fromCurrency = (AutoCompleteTextView) findViewById(R.id.from_currency_input);
        this.fromCurrency.setSelection(this.fromCurrency.getText().length());

        this.toCurrency = (AutoCompleteTextView) findViewById(R.id.to_currency_input);
        this.toCurrency.setSelection(this.toCurrency.getText().length());

        this.currencyValueToConvert = (AutoCompleteTextView) findViewById(R.id.currency_value_to_convert);
        this.currencyValueToConvert.setSelection(this.currencyValueToConvert.getText().length());

        this.currencyConvertedValue = (TextView) findViewById(R.id.currency_converted_value);

        this.errorMessage = (TextView) findViewById(R.id.error_message);
    }

    private void clearError() {
        this.errorMessage.setText("");
    }

    private void clearCurrencyConvertedValue() {
        this.currencyConvertedValue.setText("");
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(this.INPUT_METHOD_SERVICE);

        View focusedView = getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getCurrencyConversion(String fromCurrency, String toCurrency, final String currencyValueToConvert) {

        final String currencyQuery = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();

        // Building the url with the api key retrieved from the native C++ code with stringFromJNI().
        //String url = this.apiBaseUrl + "q=" + currencyQuery + "&compact=ultra&apiKey=" + stringFromJNI();
        String url = this.apiBaseUrl + "/currency/convert/" + currencyValueToConvert + "/from/" + fromCurrency + "/to/" + toCurrency;

        makeApiRequest(url);
    }

    private void makeApiRequest(String url) {

        Log.e(LOG_TAG, "API URL: " + url);

        JsonObjectRequest currencyConversionRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    handleResponse(response);
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG, error.toString());
                    setError(error.toString());
                }
            }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Api-Key", getApiKey());
                Log.i(LOG_TAG, "API KEY: " + getApiKey());
                return headers;
            }
        };

        VolleyQueueSingleton.getRequestQueue().add(currencyConversionRequest);
    }

    private void handleResponse(JSONObject response) {

        if (response.length() <= 0) {

            Log.e(LOG_TAG, "API SERVER EMPTY RESPONSE.");

            this.setError("Empty response from server... Please Check that the currency codes are correct !!!");

            return;
        }

        try {

            if (response.has("converted_value")) {
                String currencyValueFormatted = response.getString("converted_value");
                Log.i(LOG_TAG, "CURRENCY RATE: " + currencyValueFormatted);
                this.currencyConvertedValue.setText(currencyValueFormatted);
                return;
            }

            if (response.has("error")) {
                String error = response.getString("error");
                Log.e(LOG_TAG, error);
                this.setError(error);
                return;
            }

            Log.e(LOG_TAG, "Unknown response from the API server.");
            this.setError("Unknown response from the API server !!!");

        } catch (JSONException e) {

            Log.e(LOG_TAG, e.getMessage());

            // Don't return the exception message in a Production App, once it can leak sensitive
            // information
            this.setError(e.getMessage());
        }
    }

    private void setError(String message) {
        this.errorMessage.setText(message);
    }
}
