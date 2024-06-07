package com.clientapp.battleshipclient.networking.NWutils;


import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;

public class CustomRetryPolicy extends DefaultRetryPolicy {
    private String tag = "CustomRetryPolicy";


    public CustomRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier, String tag) {
        super(initialTimeoutMs, maxNumRetries, backoffMultiplier);
        this.tag = tag;
    }

    @Override
    public void retry(VolleyError error) throws VolleyError {
//        if (error instanceof TimeoutError) {
//            // If it's a timeout error, throw an exception to prevent a retry
//            throw new VolleyError("Request timed out and will not be retried");
//        }
        Log.e("myDEBUG CustomRetryPolicy on "+ tag , "Retrying due to error: " + error.getMessage());

        // For other errors, call the parent class's retry method
        super.retry(error);
    }
}