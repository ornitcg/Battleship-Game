package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.logic.JsonHelper;
import com.clientapp.battleshipclient.logic.SignLogic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Data;

@Data
public class UserNW {
    private RequestQueue requestQueue;
    private User user = null;
    private SignLogic.AuthState authenticationState = SignLogic.AuthState.PENDING;
    Context context;
    // gets users list from server

    public UserNW(Context context, User user) {
        this.user = user;
        this.context = context;
        this.requestQueue = Netcom.getInstance(context).getRequestQueue();
    }

    public static void getScores(Context context, User currPlayer, ICallbacks<JSONObject> callback) {
        String endpoint = EndpointResources.getScoresEndpoint;

        String finalUrl = "";
        try {
            String param1 = "userId=" + URLEncoder.encode(currPlayer.getId(), "UTF-8") + "&";
            String param2 = "numBestScores=" + 10;

            finalUrl = endpoint + "?" + param1 +   param2;
            Log.d("myDEBUG UserNW URL", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    Log.d("myDEBUG UserNW", "getTopUsersScore Response: " + response);


                    callback.onResponseSuccess(response);
                },
                error -> {
                    Log.d("myDEBUG UserNW", "getTopUsersScore Error: " + error);
                    callback.onError(error);
                }
        );

        Netcom.getInstance(context).addToRequestQueue(request);
    }


    /*
    *  Sign up user request
    *
    * */
    public void signUp(User user, ICallbacks<SignLogic.AuthState> callback) {
        String endpoint = EndpointResources.postSignUpEndpoint;

        JSONObject signData = JsonHelper.createSignJson(user);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, signData,
                response -> { //check response
                    boolean success = false;
                    try {
                        Log.d("myDEBUG signUp", "Response: " + response);
                        success = response.getBoolean("succeeded");
                        if (success) {
                            user.setId(response.getString("value"));
                            Log.d("myDEBUG signUp", "User ID: " + user);
                            callback.onResponseSuccess(SignLogic.AuthState.SIGNUP_SUCCEDED);
                        } else {
                            callback.onResponseSuccess(SignLogic.AuthState.USER_EXISTS);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> callback.onError(error)
        );
        addToRequestQueue(request);
    }


    /*
    *  Sign in user request
    * */
    public void signIn(User user, ICallbacks<SignLogic.AuthState> callback) {
        String endpoint = EndpointResources.postSignInEndpoint;
        Log.d("DEBUG", "signIn: " + endpoint);

        JSONObject signData = JsonHelper.createSignJson(user);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, signData,
                response -> {
                    boolean success = false;
                    try {
                        Log.d("DEBUG sign in", "sign in Response: " + response);
                        success = response.getBoolean("succeeded");
                        if (success) {
                            user.setId(response.getString("value"));
                            Log.d("DEBUG sign in", "User ID: " + user);
                            callback.onResponseSuccess(SignLogic.AuthState.SIGNIN_SUCCEDED);

                        } else {
                            String msg = response.getString("msg");
                            if (msg.equals("Entity doesn't exist"))
                                callback.onResponseSuccess(SignLogic.AuthState.USER_DOESNT_EXIST);
                            else if (msg.equals("Wrong password for user " + user.getName()))
                                callback.onResponseSuccess(SignLogic.AuthState.WRONG_PASSWORD);
                        }
                    } catch (JSONException e) {
                        Log.d("DEBUG sign in", "catch : " + response);
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Log.d("DEBUG", "Error: " + error);
                    callback.onError(error);
                }
        );

        addToRequestQueue(request);
    }





    private void addToRequestQueue(JsonObjectRequest request) {
        Netcom.getInstance(context).addToRequestQueue(request);
    }






}
