package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clientapp.battleshipclient.logic.AuthStateEnum;
import com.clientapp.battleshipclient.logic.AuthTypeEnum;
import com.clientapp.battleshipclient.logic.ICallbacks;
import com.clientapp.battleshipclient.logic.JsonHelper;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.networking.NWutils.EndpointResources;
import com.clientapp.battleshipclient.networking.NWutils.ServerStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Data;

/*
*  This class is responsible for the user networking
*  It sends requests to the server to sign up, sign in, get top users scores and keep the user signed in
*  It also contains the user authentication state
*/
@Data
public class UserNW {


    /*Sends a request to the server to get the top 10 users scores
     * @param context the context of the activity
     * @param currPlayer the user object
     * @param callback the callback to be called after the request is done
     */
    public static void getScores(Context context, User currPlayer, ICallbacks<JSONObject> callback) {
        String endpoint = EndpointResources.getScoresEndpoint;

        String finalUrl = "";
        try {
            String param1 = "userId=" + URLEncoder.encode(currPlayer.getId(), "UTF-8") + "&";
            String param2 = "numBestScores=" + 10;

            finalUrl = endpoint + "?" + param1 + param2;
            Log.d("nwDEBUG myDEBUG UserNW URL", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    Log.d("nwDEBUG myDEBUG UserNW", "getTopUsersScore Response: " + response);


                    callback.onResponseSuccess(response);
                },
                error -> {
                    Log.d("nwDEBUG UserNW", "getTopUsersScore Error: " + error);
                    callback.onError(error);
                }
        );
        Netcom.getInstance(context).addToRequestQueue(request);
        Log.d("nwDEBUG myDEBUG  UserNW", "getTopUsersScore Request added to queue");
    }

    /*
     * Sends a request to the server to keep the user signed in
     * @param context the context of the activity
     * @param currPlayerId the user id
     * */
    public static void keepUserAlive(Context context, String currPlayerId) {
        String endpoint = EndpointResources.putKeepUserAliveEndpoint;

        String finalUrl = endpoint + currPlayerId;
        Log.d("nwDEBUG myDEBUG  UserNW URL", "Final URL with query params: " + finalUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, finalUrl, null,
                response -> {
                    Log.d("nwDEBUG myDEBUG  UserNW", "keepUserAlive Response: " + response);
                },
                error -> {
//                    Log.d("nwDEBUG myDEBUG  UserNW", "keepUserAlive Error: " + error);
                }
        );

        Netcom.getInstance(context).addToRequestQueue(request);


    }


    /*
     * Sends a sign up or sign in request to the server
     *  @param context the context of the activity
     * @param currentPlayer the user object
     * @param mission the type of the request
     * @param callback the callback to be called after the request is done
     */
    public static void signUser(Context context, User currentPlayer, AuthTypeEnum mission, ICallbacks<AuthStateEnum> callback) {
        String endpoint = "";
        if (mission == AuthTypeEnum.SIGN_UP) {
            endpoint = EndpointResources.postSignUpEndpoint;
        } else if (mission == AuthTypeEnum.SIGN_IN) {
            endpoint = EndpointResources.postSignInEndpoint;
        }
        Log.d("nwDEBUG myDEBUG  ", mission + ": " + endpoint);

        JSONObject signData = JsonHelper.createSignJson(currentPlayer);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, signData,
                (JSONObject response) -> {
                    boolean success = false;
                    try {
                        Log.d("nwDEBUG myDEBUG   signUser ", "sign Response: " + response);
                        success = response.getBoolean(ServerStrings.SUCCEEDED);
                        if (success) {
                            currentPlayer.setId(response.getString(ServerStrings.VALUE));
                            Log.d("nwDEBUG myDEBUG   signUser ", "User ID: " + currentPlayer);
                            callback.onResponseSuccess(AuthStateEnum.SUCCEEDED);
                        } else {
                            String msg = response.getString(ServerStrings.MESSAGE);

                            // replace theswitch statement with if-else
                            if (msg.equals(ServerStrings.ENTITY_DOESNT_EXIST)) {
                                callback.onResponseSuccess(AuthStateEnum.USER_DOESNT_EXIST);
                            } else if (msg.equals(ServerStrings.USER_EXISTS)) {
                                callback.onResponseSuccess(AuthStateEnum.USER_EXISTS);
                            } else if (msg.equals(ServerStrings.WRONG_PASSWORD_FOR_USER + currentPlayer.getName())) {
                                callback.onResponseSuccess(AuthStateEnum.WRONG_PASSWORD);
                            } else if (msg.equals(ServerStrings.USER_SIGNED_IN)) {
                                callback.onResponseSuccess(AuthStateEnum.USER_ALREADY_SIGNED_IN);
                            }

                        }
                    } catch (JSONException e) {
                        Log.d("nwDEBUG myDEBUG   signUser ", "catch : " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("nwDEBUG myDEBUG   signUser", "SignUser Error: " + error);
                    callback.onError(error);
                }
        );

        Netcom.getInstance(context).addToRequestQueue(request);
        Log.d("nwDEBUG myDEBUG   signUser", "SignUser Request added to queue");
    }
}
