package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.networking.GameLifecycleNW;
import com.clientapp.battleshipclient.networking.NWutils.RequestEnum;
import com.clientapp.battleshipclient.networking.NWutils.ServerStrings;
import com.clientapp.battleshipclient.networking.Netcom;
import com.clientapp.battleshipclient.view.activities.MenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

/*
 *  this class intermediates between the MenuActivity and the networking section in GameLifecycleNW
 *  */
public class MenuLogic {

    private Context context;
    private User currPlayer;

    /**
     *  constructor
     */
    public MenuLogic(Context context, User currentPlayer) {
        this.context = context;
        this.currPlayer = currentPlayer;
    }


    /* calls the networking section to start the game request*/
    public void startPlaying() {
        Log.d("myDEBUG MenuLogic", "******************************************* START_PLAYING ***: ");
        GameLogic.isGameInProgress = true;
        GameLifecycleNW.createGame(context, currPlayer.getId(), new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {//callback method for the response
                ((MenuActivity) context).cancelRunnable();
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    Log.d("myDEBUG MenuLogic getGameId", "onResponse: " + jsonResponse);
                } catch (JSONException e) {
                    Log.e("myDEBUG MenuLogic getGameId", "onResponse error catch: " + e);
                }
                String gameId = jsonResponse.optString(ServerStrings.VALUE);
                String message = jsonResponse.optString(ServerStrings.MESSAGE);
                Log.d("myDEBUG MenuLogic", "onResponse: " + gameId + " " + message);

                if (gameId != null) {
                    ((MenuActivity) context).goToPlacementActivity(gameId);
                }
            }

            @Override
            public void onError(Exception e) {//callback method for the error
                if (e instanceof NoConnectionError) {
                    ((MenuActivity) context).viewChangeOnRequestCanceled();
                }
                Log.e("myDEBUG MenuLogic ", "retry setStartPlayingListener onError: " + e);
            }//onError
        });//getGameId
    }


    /* calls the networking section so cancel the game request*/
    public void cancelCreateGame() {
        Netcom.getInstance(context).getRequestQueue().cancelAll(RequestEnum.CREATE_GAME.name());
        Log.d("myDEBUG MenuLogic", "cancelAll on CREATE_GAME ");
        GameLifecycleNW.notifyGameCanceled(context, currPlayer.getId(), new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                ((MenuActivity) context).onResume();
            }

            @Override
            public void onError(Exception e) {
                //TODO quitGame
            }
        });
    }


}

