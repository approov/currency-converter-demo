package com.criticalblue.currencyconverterdemo.approov;

import com.criticalblue.approovsdk.Approov;

class ApproovProtectedApiRequest {

    private final ApproovDynamicConfig approovConfig;
    private ActivityView activityView;
    private HttpRequest httpRequest;

    public ApproovProtectedApiRequest(HttpRequest httpRequest, ApproovDynamicConfig approovConfig, ActivityView activityView) {
        this.httpRequest = httpRequest;
        this.approovConfig = approovConfig;
        this.activityView = activityView;
    }

    public void make(Approov.TokenFetchResult approovTokenFetchResult, String url) {
        String approovToken;

        try {
            approovToken = (new ApproovTokenFetchResult(approovConfig)).handle(approovTokenFetchResult);
        } catch (ApproovTokenFetchResultTemporaryException e) {
            e.printStackTrace();

            activityView.handleErrorMessage(e.getMessage());
            return;
        } catch (ApproovTokenFetchResultFatalException e) {
            e.printStackTrace();

            activityView.handleErrorMessage("Unable to continue due to an internal error. Please report.");
            return;
        }

        httpRequest.get(url, approovToken);
    }
}
