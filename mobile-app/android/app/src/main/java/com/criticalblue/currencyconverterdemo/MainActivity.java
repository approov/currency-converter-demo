package com.criticalblue.currencyconverterdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.json.JSONException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText fromCurrency;
    private EditText toCurrency;
    private EditText currencyValueToConvert;

    private TextView currencyConvertedValue;
    private TextView errorMessage;

    private RequestQueue requestQueue;

    private static final String LOG_TAG = "CURRENCY_CONVERTER_DEMO";

    // In a production app this url should not be stored in the code.
    private String apiBaseUrl = "https://free.currencyconverterapi.com/api/v6/convert?";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void convertCurrency(View v) {

        clearError();

        clearCurrencyConvertedValue();

        getCurrencyConversion(
            fromCurrency.getText().toString(),
            toCurrency.getText().toString(),
            currencyValueToConvert.getText().toString()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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

    private void getCurrencyConversion(String fromCurrency, String toCurrency, final String currencyValueToConvert) {

        final String currencyQuery = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();

        // Building the url with the api key retrieved from the native C++ code with stringFromJNI().
        String url = this.apiBaseUrl + "q=" + currencyQuery + "&compact=ultra&apiKey=" + stringFromJNI();

        makeApiRequest(currencyValueToConvert, currencyQuery, url);
    }

    private void makeApiRequest(final String currencyValueToConvert, final String currencyQuery, String url) {

        Log.e(LOG_TAG, "API URL: " + url);

        JsonObjectRequest currencyConversionRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    handleResponse(response, currencyValueToConvert, currencyQuery);
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(LOG_TAG, error.toString());
                    setError(error.toString());
                }
            }
        );

        VolleyQueueSingleton.getInstance(this).addToRequestQueue(currencyConversionRequest);
    }

    private void handleResponse(JSONObject response, String currencyValueToConvert, String currencyQuery) {

        if (response.length() <= 0) {

            Log.e(LOG_TAG, "API SERVER EMPTY RESPONSE.");

            this.setError("Empty response from server... Please Check that the currency codes are correct !!!");

            return;
        }

        try {

            String currencyRate = response.get(currencyQuery).toString();
            Log.e(LOG_TAG, "CURRENCY RATE: " + currencyRate);

            handleCurrencyConversion(currencyValueToConvert, currencyRate, currencyQuery);

        } catch (JSONException e) {

            Log.e(LOG_TAG, e.getMessage());
            this.setError(e.getMessage());
        }
    }

    private void handleCurrencyConversion(String currencyValue, String currencyRate, String currencyQuery) {

        Double convertedValue = stringNumberToDouble(currencyValue) * stringNumberToDouble(currencyRate);
        Log.e(LOG_TAG, "CONVERTED VALUE: " + convertedValue);

        String currencyCode = currencyQuery.split("_")[1];
        Log.e(LOG_TAG, "CURRENCY CODE: " + currencyCode);

        String currencyValueFormatted = formatAsCurrency(convertedValue, currencyCode);
        Log.e(LOG_TAG, "CURRENCY VALUE FORMATTED: " + currencyValueFormatted);

        this.currencyConvertedValue.setText(currencyValueFormatted);
    }

    private String formatAsCurrency(Double convertedValue, String currencyCode) {

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());
        currency.setCurrency(Currency.getInstance(currencyCode));

        return currency.format(convertedValue);
    }

    private Double stringNumberToDouble(String number) {

        try {

            return NumberFormat.getNumberInstance().parse(number)
                    .doubleValue();

        } catch (ParseException exception) {

            Log.e(LOG_TAG, exception.getMessage());

            this.setError(exception.getMessage());

            return null;
        }
    }

    private void setError(String message) {
        this.errorMessage.setText(message);
    }
}
