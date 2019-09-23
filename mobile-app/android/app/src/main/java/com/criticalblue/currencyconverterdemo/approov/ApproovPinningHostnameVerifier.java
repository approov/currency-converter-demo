package com.criticalblue.currencyconverterdemo.approov;

import android.util.Log;

import com.criticalblue.approovsdk.Approov;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import okio.ByteString;

public final class ApproovPinningHostnameVerifier implements HostnameVerifier {

    // The HostnameVerifier you would normally be using
    private final HostnameVerifier delegate;

    private static final String TAG = "APPROOV";


    /**
     * Construct a ApproovPinningHostnameVerifier which delegates
     * the initial verify to a user defined HostnameVerifier before
     * applying pinning on top.
     *
     * @param delegate is the HostnameVerifier to apply before the custom pinning
     */
    public ApproovPinningHostnameVerifier(HostnameVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        Log.i(TAG, "PIN VERIFICATION");

        // check the delegate function first and only proceed if it passes
        if (delegate.verify(hostname, session)) try {
            // extract the set of valid pins for the hostname
            Set<String> hostPins = new HashSet<>();
            Map<String, List<String>> pins = Approov.getPins("public-key-sha256");
            for (Map.Entry<String, List<String>> entry: pins.entrySet()) {
                if (entry.getKey().equals(hostname)) {
                    for (String pin: entry.getValue()) {
                        Log.i(TAG, "PIN: " + pin);
                        hostPins.add(pin);
                    }
                }
            }

            // if there are no pins then we accept any certificate
            if (hostPins.isEmpty()) {
                return true;
            }

            // check to see if any of the pins are in the certificate chain
            for (Certificate cert: session.getPeerCertificates()) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    ByteString digest = ByteString.of(x509Cert.getPublicKey().getEncoded()).sha256();
                    String hash = digest.base64();

                    if (hostPins.contains(hash)) {
                        Log.i(TAG, "VALID PIN: " + hash);
                        return true;
                    }

                    Log.i(TAG, "INVALID PIN: " + hash);
                }
            }

            // the connection is rejected
            return false;
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}