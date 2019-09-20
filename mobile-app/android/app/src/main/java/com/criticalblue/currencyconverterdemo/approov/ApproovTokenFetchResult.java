package com.criticalblue.currencyconverterdemo.approov;

import android.util.Log;

import com.criticalblue.approovsdk.Approov;

import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.BAD_URL;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.INTERNAL_ERROR;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.MISSING_LIB_DEPENDENCY;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.MITM_DETECTED;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.NO_APPROOV_SERVICE;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.NO_NETWORK;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.NO_NETWORK_PERMISSION;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.POOR_NETWORK;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.SUCCESS;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.UNKNOWN_URL;
import static com.criticalblue.approovsdk.Approov.TokenFetchStatus.UNPROTECTED_URL;

class ApproovTokenFetchResult {

    private static final String TAG = "APPROOV";
    private ApproovDynamicConfig approovConfig;

    public ApproovTokenFetchResult(ApproovDynamicConfig approovConfig) {
        this.approovConfig = approovConfig;
    }

    public String handle(Approov.TokenFetchResult approovResult) throws ApproovTokenFetchResultTemporaryException, ApproovTokenFetchResultFatalException {

        if (approovResult.isConfigChanged()) {
            approovConfig.saveApproovConfigUpdate();
        }

        if (isStatusOk(approovResult)) {
            return approovResult.getToken();
        }

        throw new ApproovTokenFetchResultFatalException("Unable to fetch the Approov token due to: " + approovResult.getStatus());
    }

    private boolean isStatusOk(Approov.TokenFetchResult approovResult) throws ApproovTokenFetchResultTemporaryException {

        Approov.TokenFetchStatus status = approovResult.getStatus();

        switch (status) {
            case SUCCESS:
                Log.i(TAG, SUCCESS.toString());
                return true;
            case NO_APPROOV_SERVICE:
                Log.e(TAG, NO_APPROOV_SERVICE.toString());
                // We may want to add retry logic, once this should be a temporary error.
                throw new ApproovTokenFetchResultTemporaryException("Temporary error. Please retry!");
            case NO_NETWORK:
                Log.e(TAG, NO_NETWORK.toString());
                // We may want to add retry logic, once this should be a temporary error.
                throw new ApproovTokenFetchResultTemporaryException("Temporary error: No Network available. Please retry when you got network signal again!");
            case POOR_NETWORK:
                Log.e(TAG, POOR_NETWORK.toString());
                // We may want to add retry logic, once this should be a temporary error.

                throw new ApproovTokenFetchResultTemporaryException("Temporary error: Poor Network signal. Please retry when you got a better network signal!");
            case MITM_DETECTED:
                Log.e(TAG, MITM_DETECTED.toString());
                // We may want to add retry logic, once this could be a temporary error.
                throw new ApproovTokenFetchResultTemporaryException("Temporary error: Man in the Middle Attack detected. If in a public wifi, disconnect and retry again with mobile data!");
            case BAD_URL:
                Log.e(TAG, BAD_URL.toString());
                return false;
            case UNKNOWN_URL:
                Log.e(TAG, UNKNOWN_URL.toString());
                return false;
            case UNPROTECTED_URL:
                Log.e(TAG, UNPROTECTED_URL.toString());
                return false;
            case NO_NETWORK_PERMISSION:
                Log.e(TAG, NO_NETWORK_PERMISSION.toString());
                return false;
            case MISSING_LIB_DEPENDENCY:
                Log.e(TAG, MISSING_LIB_DEPENDENCY.toString());
                return false;
            case INTERNAL_ERROR:
                Log.e(TAG, INTERNAL_ERROR.toString());
                return false;
            default:
                // There has been some error event that should be reported
                Log.e(TAG, "UNKNOWN ERROR OCCURRED FOR APPROOV RESULT STATUS: " + status);
        }

        return false;
    }
}
