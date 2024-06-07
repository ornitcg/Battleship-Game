package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.clientapp.battleshipclient.networking.NWutils.CustomRetryPolicy;
import com.clientapp.battleshipclient.view.activities.GameActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GameLifecycleNW {




    /**
     * This method sets a no-retry policy for sending requests.
     * being used on all requests to avoid multiple requests being sent to the server
     */
    public static DefaultRetryPolicy noRetryPolicy() {
        // Customize these values based on your specific needs
        int socketTimeout = 0; // 10 seconds timeout
        int maxTries = 0;          // Set to 1 to disable retries
        float backoffMultiplier = 0; // backoff multiplier

        return new DefaultRetryPolicy(socketTimeout, maxTries, backoffMultiplier);
    }





    /**
     * This method sends a request to the server to get a game id
     * called when user wants to start a new game and waits for an opponent match
     * response is handles using callback methods
     */
    public static void postCreateGame(Context context, String currPlayerId, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();
        String endpoint = EndpointResources.getGameIdEndPoint;
        String finalUrl = "";
        try {
            String queryParam = "userId=" + URLEncoder.encode(currPlayerId, "UTF-8");
            finalUrl = endpoint + "?" + queryParam;
            Log.d("myDEBUG GameLifecycleNW URL", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("myDEBUG GameLifecycleNW in request", "createGame onResponse: " + response);
                callback.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        request.setRetryPolicy(new CustomRetryPolicy(0, 0, 0f, "postCreateGame"));
        Log.d("myDEBUG GameLifecycleNW", "createGame request sent");
        Log.d("myDEBUG GameLifecycleNW", "Retry Policy Set: MaxNumRetries = " + request.getRetryPolicy().getCurrentRetryCount());

        requestQueue.add(request);
        Log.d("myDEBUG GameLifecycleNW", requestQueue.toString());
    }







    /**
     * Cancels all requests from queue
     */
    public static void cancelRequests(Context context) {
        Netcom.getInstance(context).getRequestQueue().cancelAll(context);
    }





    /**
     * This method sends a request to the server to get the board id
     * called when user wants to start a new game and waits for an opponent match
     * response is handles using callback methods
     */
    public static void createBoard(Context context, JSONObject gameBoardJSON, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();
//        Log.d("myDEBUG GameLifecycleNW in getBoardId", "gameBoardJSON: " + gameBoardJSON);
        String endpoint = EndpointResources.postCreateBoardEndpoint;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, gameBoardJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("myDEBUG GameLifecycleNW in getBoardId", "onResponse: " + response);
                        callback.onResponseSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("myDEBUG GameLifecycleNW in getBoardId", "onErrorResponse: " + error.getMessage());
                callback.onError(error);
            }
        });
        //log request
//        Log.d("myDEBUG GameLifecycleNW in getBoardId", "Request: " + request);
        request.setRetryPolicy(noRetryPolicy());
        requestQueue.add(request);
    }





    public static void pauseGame(GameActivity context, String gameId, String currPlayerUserId, ICallbacks<String> iCallbacks) {
    }





    /**
     * This method sends a request to the server to cancel the game
     * called when user wants to cancel the game while waiting for opponent match
     * response is handles  by the logic layer using callback methods
     */
    public static void notifyGameCanceled(Context context, String currPlayerId, ICallbacks<String> callbacks) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();

        //create a delete request to  server
        Log.d("DEBUG GameLifecycleNW", "in notifyGameCanceled");
        String endpoint = EndpointResources.deleteGameEndpoint;
        String finalUrl = endpoint + currPlayerId;
        Log.d("DEBUG GameLifecycleNW in notifyGameCanceled", "Final URL with query params: " + finalUrl);
        StringRequest request = new StringRequest(Request.Method.DELETE, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG GameLifecycleNW in notifyGameCanceled", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG GameLifecycleNW in notifyGameCanceled", "onError: " + error);
            }
        });
        request.setRetryPolicy(noRetryPolicy());
        requestQueue.add(request);
    }


    public static void notifyGamePaused(Context context, String gameId, ICallbacks<String> iCallbacks) {
        String endpoint = EndpointResources.putPauseGameEndpoint;
        String finalUrl = endpoint + gameId;

        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("myDEBUG GameLifecycleNW in notifyGamePaused", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myDEBUG GameLifecycleNW in notifyGamePaused", "onError: " + error);
                iCallbacks.onError(error);
            }
        });

        request.setRetryPolicy(noRetryPolicy());
        Netcom.getInstance(context).getRequestQueue().add(request);

    }

    public static void notifyGameResumed(Context context, String gameId, ICallbacks<String> iCallbacks) {
        String endpoint = EndpointResources.putResumeGameEndpoint;
        String finalUrl = endpoint + gameId;

        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("myDEBUG GameLifecycleNW in notifyGamePaused", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myDEBUG GameLifecycleNW in notifyGamePaused", "onError: " + error);
                iCallbacks.onError(error);
            }
        });

        request.setRetryPolicy(noRetryPolicy());
        Netcom.getInstance(context).getRequestQueue().add(request);

    }


    /*
     * This method sends a request to the server to end the game
     * called when user wants to quit the game
     * or in special cases as in bettery too low
     * response is handles  by the logic layer using callback methods
     */
    public static void notifyGameEnded(Context context, String gameId, ICallbacks<String> iCallbacks) {

        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();

        String endpoint = EndpointResources.putEndGameEndpoint;
        String finalUrl = endpoint + gameId;
        Log.d("myDEBUG GameLifecycleNW in notifyGameEnd", "Final URL with path param: " + finalUrl);

        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("myDEBUG GameLifecycleNW in notifyGameEnd", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myDEBUG GameLifecycleNW in notifyGameEnd", "onError: " + error);
                iCallbacks.onError(error);
            }
        });
        request.setRetryPolicy(noRetryPolicy());
        requestQueue.add(request);
    }






    /*
     *  This method sends a request to the server to keep the game alive
     *  called frequently by a handler to notify the server that player is still in the game
     *  used only on player's turn
     * */
    public static void keepGameAlive(Context context, String gameId, ICallbacks<String> iCallbacks) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();

        String endpoint = EndpointResources.putKeepGameAliveEndpoint;
        String finalUrl = endpoint + gameId;
        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("myDEBUG GameLifecycleNW in keepGameAlive", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myDEBUG GameLifecycleNW in keepGameAlive", "onError: " + error);
                iCallbacks.onError(error);
            }
        });

        request.setRetryPolicy(noRetryPolicy());
        requestQueue.add(request);

    }
}
