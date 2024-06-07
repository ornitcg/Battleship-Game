package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.clientapp.battleshipclient.networking.NWutils.CustomRetryPolicy;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GameActionNW {
    private static final int getAttackTimeout = 100;


    public static void getGame(Context context, String gameId, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();

        //log game id
        Log.d("DEBUG GameLifecycleNW in game request", "gameId: " + gameId);
        String endpoint = EndpointResources.getGameEndpoint;
        String finalUrl = endpoint + gameId;
        Log.d("DEBUG GameLifecycleNW in game request", "finalUrl: " + finalUrl);
        StringRequest request = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        request.setRetryPolicy(new CustomRetryPolicy(1000, 20, 1.2f, "getGame retry"));
        requestQueue.add(request);
    }


    public static void postAttack(Context context, String gameId, String currPlayerId, int position, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();
        String endpoint = EndpointResources.postAttackEndpoint;
        String finalUrl = "";
        try {
            String param1 = "userId=" + URLEncoder.encode(currPlayerId, "UTF-8") + "&";
            String param2 = "gameId=" + URLEncoder.encode(gameId, "UTF-8") + "&";
            String param3 = "position=" + URLEncoder.encode(String.valueOf(position), "UTF-8");
            finalUrl = endpoint + "?" + param1 + param2 + param3;
            Log.d("DEBUG postAttack", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG GameLifecycleNW in postAttack ", "onResponse: " + response);
                callback.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        Log.d("myDEBUG GameLifecycleNW ", "postAttack request: " + request.toString());
        request.setRetryPolicy(GameLifecycleNW.noRetryPolicy());
        requestQueue.add(request);
        Log.d("DEBUG gameNW", requestQueue.toString());
    }


    /**
     * This method sends a GET request to the server to get the attack by opponent.
     * called when user wants to get the attack.
     * response is handles using callback methods by the logic layer.
     */
    public static void getAttack(Context context, String currPlayerId, ICallbacks<String> callbacks) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();

        Log.d("DEBUG GameLifecycleNW", " in getAttack: ");
        String endpoint = EndpointResources.getAttackEndpoint;
        String finalUrl = "";
        try {
//            String param1 = "gameId=" + URLEncoder.encode(gameId, "UTF-8");
            String param1 = "userId=" + URLEncoder.encode(currPlayerId, "UTF-8");

            finalUrl = endpoint + "?" + param1;
            Log.d("DEBUG GameLifecycleNW in getAttack", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG GameLifecycleNW in getAttack", "onResponse: " + response);
                callbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG GameLifecycleNW in getAttack", "onError: " + error);

                callbacks.onError(error);
            }
        });

//        request.setRetryPolicy(GameLifecycleNW.noRetryPolicy());
        request.setRetryPolicy(new CustomRetryPolicy(100, 30, 1.1f, "getAttack"));

        requestQueue.add(request);
    }


    public static void getBoard(String boardId, ICallbacks<String> callback) {
        String endpoint = EndpointResources.getCurrentBoard;
        String finalUrl = endpoint + boardId;
        Log.d("DEBUG GameLifecycleNW in getBoard", "Final URL with query params: " + finalUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("DEBUG GameLifecycleNW in getBoard", "onResponse: " + response);
                callback.onResponseSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DEBUG GameLifecycleNW in getBoard", "onErrorResponse: " + error.getMessage());
                callback.onError(error);
            }
        });

        request.setRetryPolicy(new CustomRetryPolicy(1000, 10, 1.2f, "getBoard retry"));
        Netcom.getInstance(null).addToRequestQueue(request);
    }


}
