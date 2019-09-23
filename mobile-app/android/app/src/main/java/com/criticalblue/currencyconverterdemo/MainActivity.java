package com.criticalblue.currencyconverterdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.criticalblue.currencyconverterdemo.approov.ActivityView;
import com.criticalblue.currencyconverterdemo.approov.ApproovTokenSingleton;
import com.criticalblue.currencyconverterdemo.approov.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText fromCurrency;
    private EditText toCurrency;
    private EditText currencyValueToConvert;

    private TextView currencyConvertedValue;
    private TextView errorMessage;

    private static final String TAG = "CURRENCY_CONVERTER_DEMO";

    // In a production app this url should not be stored in the code.
    //private String apiBaseUrl = "https://free.currencyconverterapi.com/api/v6/convert?";
    private String apiBaseUrl = "https://currency-converter-demo.pdm.approov.io/v2";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ApproovTokenSingleton approov;


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

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

    public void setCurrencyConvertedValue(String value) {
        this.currencyConvertedValue.setText(value);
    }

    public void setErrorMessage(String message) {

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("APPROOV","MESSAGE: " + message);
                errorMessage.setText(message);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        approov = ApproovTokenSingleton.getInstance(this);

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
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getCurrencyConversion(String fromCurrency, String toCurrency, final String currencyValueToConvert) {

        // Building the url with the api key retrieved from the native C++ code with stringFromJNI().
        String url = this.apiBaseUrl + "/currency/convert/" + currencyValueToConvert + "/from/" + fromCurrency + "/to/" + toCurrency;

        makeApiRequest(url);
    }

    private void makeApiRequest(String url) {
        MainActivityView activityView = new MainActivityView();

        HttpRequest httpRequest = new VolleyHttpRequest(activityView, stringFromJNI());

        approov.fetchApproovTokenOnBackground(httpRequest, url, activityView);
        //approov.fetchApproovTokenAndWait(httpRequest, url, activityView);
    }

    class MainActivityView implements ActivityView {

        @Override
        public Context getContext() {
            return getApplicationContext();
        }

        @Override
        public void handleErrorMessage(String message) {
            setErrorMessage(message);
        }

        @Override
        public void handleResponse(JSONObject response) {

            if (response.length() <= 0) {
                Log.e(TAG, "API SERVER EMPTY RESPONSE.");
                setErrorMessage("Empty response from server... Please Check that the currency codes are correct !!!");
                return;
            }

            try {

                if (response.has("converted_value")) {
                    String currencyValueFormatted = response.getString("converted_value");
                    Log.i(TAG, "CURRENCY RATE: " + currencyValueFormatted);

                    setCurrencyConvertedValue(currencyValueFormatted);
                    return;
                }

                if (response.has("error")) {
                    String error = response.getString("error");
                    Log.e(TAG, error);
                    setErrorMessage(error);
                    return;
                }

                Log.e(TAG, "Unknown response from the API server.");
                setErrorMessage("Unknown response from the API server !!!");
                return;

            } catch (JSONException e) {

                Log.e(TAG, e.getMessage());

                // Don't return the exception message in a Production App, once it can leak sensitive
                // information
                setErrorMessage(e.getMessage());
                return;
            }
        }
    }
}
