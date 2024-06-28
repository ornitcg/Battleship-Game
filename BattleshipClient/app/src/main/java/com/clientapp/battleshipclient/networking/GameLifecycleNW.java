package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.clientapp.battleshipclient.logic.ICallbacks;
import com.clientapp.battleshipclient.networking.NWutils.CustomRetryPolicy;
import com.clientapp.battleshipclient.networking.NWutils.EndpointResources;
import com.clientapp.battleshipclient.networking.NWutils.RequestEnum;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/*
 * This class is responsible for sending requests to the server to manage the game lifecycle
 * It sends requests to create a game, get a board, cancel a game, end a game, keep the game alive
 */
public class GameLifecycleNW {


    /**
     * This method sets a no-retry policy for sending requests.
     * being used on all requests to avoid multiple requests being sent to the server
     */
    public static CustomRetryPolicy noRetryPolicy(Object tag) {
        int socketTimeout = 5000;
        int maxTries = 0;          // Set to 0 to disable retries
        float backoffMultiplier = 0; // backoff multiplier
        return new CustomRetryPolicy(socketTimeout, maxTries, backoffMultiplier, tag );
    }


    /**
     * This method sends a request to the server to get a game id
     * called when user wants to start a new game and waits for an opponent match
     * response is handles using callback methods
     */
    public static void createGame(Context context, String currPlayerId, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();
        requestQueue.cancelAll(RequestEnum.CREATE_GAME.getName());
        String endpoint = EndpointResources.getGameIdEndPoint;
        String finalUrl = "";
        try {
            String queryParam = "userId=" + URLEncoder.encode(currPlayerId, "UTF-8");
            finalUrl = endpoint + "?" + queryParam;
            Log.d("nwDEBUG GameLifecycleNW URL", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("nwDEBUG GameLifecycleNW in request", "createGame onResponse: " + response);
                callback.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        request.setRetryPolicy(noRetryPolicy(RequestEnum.CREATE_GAME.getName()) );
        requestQueue.add(request);
        Log.d("nwDEBUG GameLifecycleNW in request", "CREATE_GAME Request added: " + request);
    }


    /**
     * This method sends a request to the server to get the board id
     * called when user wants to start a new game and waits for an opponent match
     * response is handles using callback methods
     */
    public static void createBoard(Context context, JSONObject gameBoardJSON, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();
        Log.d("myDEBUG GameLifecycleNW in getBoardId", "gameBoardJSON: " + gameBoardJSON);
        String endpoint = EndpointResources.postCreateBoardEndpoint;
        Log.d("nwDEBUG finalURL GameLifecycleNW in getBoardId", "finalURL Endpoint: " + endpoint);
        Log.d("myDEBUG getBoardId", "requesting create board");
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
                Log.e("myDEBUG createBoard error", "onErrorResponse: " + error.getMessage());
                if (error.networkResponse != null) {
                    Log.e("myDEBUG createBoard error", String.valueOf(error.networkResponse.statusCode));
                    Log.e("myDEBUG createBoard error", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                }else {
                    Log.e("myDEBUG createBoard error", error.toString());
                    if (error.getCause() != null) {
                        Log.e("myDEBUG createBoard error", error.getCause().toString());
                    }
                }
                callback.onError(error);
            }
        });
        //log request
//        Log.d("myDEBUG GameLifecycleNW in getBoardId", "Request: " + request);
        request.setRetryPolicy(new CustomRetryPolicy(5000, 0, 0f, RequestEnum.CREATE_BOARD.getName()));
        requestQueue.add(request);
        Log.d("myDEBUG GameLifecycleNW in getBoardId", "CREATE_BOARD Request added to queue");
    }


    /**
     * This method sends a request to the server to cancel the game
     * called when user wants to cancel the game while waiting for opponent match
     * response is handles  by the logic layer using callback methods
     */
    public static void notifyGameCanceled(Context context, String currPlayerId, ICallbacks<String> callbacks) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();

        //create a delete request to  server
        Log.d("nwDEBUG GameLifecycleNW", "in notifyGameCanceled");
        String endpoint = EndpointResources.deleteGameEndpoint;
        String finalUrl = endpoint + currPlayerId;
        Log.d("nwDEBUG GameLifecycleNW in notifyGameCanceled", "Final URL with query params: " + finalUrl);
        StringRequest request = new StringRequest(Request.Method.DELETE, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("nwDEBUG GameLifecycleNW in notifyGameCanceled", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("nwDEBUG GameLifecycleNW in notifyGameCanceled", "onError: " + error);
            }
        });
        Log.d("nwDEBUG  GameLifecycleNW in notifyGameCanceled", "Request: " + request);
        request.setRetryPolicy(noRetryPolicy(RequestEnum.CANCEL_MATCH.getName()));
        requestQueue.add(request);
        Log.d("nwDEBUG myDEBUG   GameLifecycleNW in notifyGameCanceled", "CANCEL_MATCH Request added to queue");
    }


    /*
    *  This method sends a request to the server to notify that the game is paused
    *  called when user pauses the game
    *  response is handled  by the logic layer using callback methods
    * */
    public static void notifyGamePaused(Context context, String gameId, ICallbacks<String> iCallbacks) {
        String endpoint = EndpointResources.putPauseGameEndpoint;
        String finalUrl = endpoint + gameId;

        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGamePaused", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGamePaused", "onError: " + error);
                iCallbacks.onError(error);
            }
        });
        request.setRetryPolicy(noRetryPolicy(RequestEnum.PAUSE_GAME.getName()));
        Netcom.getInstance(context).getRequestQueue().add(request);
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGamePaused", "PAUSE_GAME Request added to queue");
    }


    /*
    *  This method sends a request to the server to notify that the game is resumed
    *  called when user resumes the game
    * */
    public static void notifyGameResumed(Context context, String gameId, ICallbacks<String> iCallbacks) {
        String endpoint = EndpointResources.putResumeGameEndpoint;
        String finalUrl = endpoint + gameId;

        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGamePaused", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myDEBUG GameLifecycleNW in notifyGamePaused", "onError: " + error);
                iCallbacks.onError(error);
            }
        });
        request.setRetryPolicy(noRetryPolicy(RequestEnum.RESUME_GAME.getName()));
        Netcom.getInstance(context).getRequestQueue().add(request);
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGamePaused", "RESUME_GAME Request added to queue");
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
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGameEnd", "Final URL with path param: " + finalUrl);

        StringRequest request = new StringRequest(Request.Method.PUT, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGameEnd", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGameEnd", "onError: " + error);
                iCallbacks.onError(error);
            }
        });
        request.setRetryPolicy(noRetryPolicy(RequestEnum.END_GAME.getName()));
        requestQueue.add(request);
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in notifyGameEnd", "END_GAME Request added to queue");
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
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in keepGameAlive", "onResponse: " + response);
                iCallbacks.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in keepGameAlive", "onError: " + error);
                iCallbacks.onError(error);
            }
        });
        request.setRetryPolicy(noRetryPolicy(RequestEnum.KEEP_GAME_ALIVE.getName()));
        requestQueue.add(request);
    }
}
