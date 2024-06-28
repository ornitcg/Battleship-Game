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
import com.clientapp.battleshipclient.networking.NWutils.EndpointResources;
import com.clientapp.battleshipclient.networking.NWutils.RequestEnum;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/*
* This class is responsible for the game actions such as getting the game state and board
* and posting an attack
* It uses the Volley library to make the network requests
* */
public class GameActionNW {

    /**
    *  Method to get a game body that contains all the information about the game
    * used for constantly updating the game state and board
    * @param context: the context of the activity
    * @param gameId: the id of the game
    * @param callback: the callback to be executed after the request
    * */
    public static void getGame(Context context, String gameId, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue(); //TODO check if null context here is OK
        Log.d("nwDEBUG GameLifecycleNW in game request", "gameId: " + gameId);
        String endpoint = EndpointResources.getGameEndpoint;
        String finalUrl = endpoint + gameId;
        Log.d("nwDEBUG GameLifecycleNW in game request", "finalUrl: " + finalUrl);
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
        request.setRetryPolicy(GameLifecycleNW.noRetryPolicy(RequestEnum.GET_GAME.getName()));
        requestQueue.add(request);
        Log.d("nwDEBUG GameLifecycleNW in game request", "GET_GAME Request added to queue");
    }


    /**
    * Method to post an attack
    * @param context: the context of the activity
    * @param gameId: the id of the game
    * @param currPlayerId: the id of the current player
    * @param position: the position of the attack
    * @param callback: the callback to be executed after the request
    * */
    public static void postAttack(Context context, String gameId, String currPlayerId, int position, ICallbacks<String> callback) {
        RequestQueue requestQueue = Netcom.getInstance(context).getRequestQueue();
        String endpoint = EndpointResources.postAttackEndpoint;
        String finalUrl = "";
        try {
            String param1 = "userId=" + URLEncoder.encode(currPlayerId, "UTF-8") + "&";
            String param2 = "gameId=" + URLEncoder.encode(gameId, "UTF-8") + "&";
            String param3 = "position=" + URLEncoder.encode(String.valueOf(position), "UTF-8");
            finalUrl = endpoint + "?" + param1 + param2 + param3;
            Log.d("nwDEBUG myDEBUG  postAttack", "Final URL with query params: " + finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in postAttack ", "onResponse: " + response);
                callback.onResponseSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        request.setRetryPolicy(GameLifecycleNW.noRetryPolicy(RequestEnum.POST_ATTACK.getName()));
        requestQueue.add(request);
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in postAttack", "POST_ATTACK Request added to queue");
    }


    /**
    *  Method to get the board of the game
    *  @param boardId: the id of the board
    *  @param callback: the callback to be executed after the request
    * */
    public static void getBoard(String boardId, ICallbacks<String> callback) {
        String endpoint = EndpointResources.getCurrentBoard;
        String finalUrl = endpoint + boardId;
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in getBoard", "Final URL with query params: " + finalUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("nwDEBUG myDEBUG  GameLifecycleNW in getBoard", "onResponse: " + response);
                callback.onResponseSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("nwDEBUG myDEBUG  GameLifecycleNW in getBoard", "onErrorResponse: " + error.getMessage());
                callback.onError(error);
            }
        });
        request.setRetryPolicy(GameLifecycleNW.noRetryPolicy(RequestEnum.GET_BOARD.getName()));
        Netcom.getInstance(null).addToRequestQueue(request);
        Log.d("nwDEBUG myDEBUG  GameLifecycleNW in getBoard", "GET_BOARD Request added to queue");
    }


}
