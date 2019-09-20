package com.criticalblue.currencyconverterdemo.approov;

import android.content.Context;
import android.util.Log;

import com.criticalblue.approovsdk.Approov;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ApproovDynamicConfig {

    private static final String TAG = "APPROOV_CONFIG";
    private final Context context;

    public ApproovDynamicConfig(Context context) {
        this.context = context;
    }

    public String readInitialConfig() {

        String initialConfig = readFile("approov-initial.config");

        if (initialConfig == null) {
            throw new RuntimeException("Initial Appoov config is missing or is invalid.");
        }

        return initialConfig;
    }

    public String readDynamicConfig() {
        return readFile("approov-dynamic.config");
    }

    private String readFile(String filename) {
        String config = null;

        Log.i(TAG, "File to read: " + filename);

        try {
            InputStream stream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            config = reader.readLine();
            reader.close();
        } catch (IOException e) {
            // we can log this but it is not fatal as the app will receive a new update if the
            // stored one is corrupted in some way
            Log.i(TAG, "Reading Approov configuration for " + filename + " failed: " + e.getMessage());
        }

        return config;
    }

    /**
     * Saves an update to the Approov configuration to local configuration of the app. This should
     * be called after every Approov token fetch where isConfigChanged() is set. It saves a new
     * configuration received from the Approov server to the local app storage so that it is
     * available on app startup on the next launch.
     */
    public void saveApproovConfigUpdate() {
        String updateConfig = Approov.fetchConfig();
        if (updateConfig == null)
            Log.e(TAG, "Could not get dynamic Approov configuration");
        else {
            try {
                FileOutputStream outputStream = context.getApplicationContext().openFileOutput("approov-dynamic.config", Context.MODE_PRIVATE);
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(updateConfig);
                printStream.close();
            } catch (IOException e) {
                // we can log this but it is not fatal as the app will receive a new update if the
                // stored one is corrupted in some way
                Log.e(TAG, "Cannot write Approov dynamic configuration: " + e.getMessage());
                return;
            }
            Log.i(TAG, "Wrote dynamic Approov configuration");
        }
    }
}
