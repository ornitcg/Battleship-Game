package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.NoConnectionError;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.networking.GameLifecycleNW;
import com.clientapp.battleshipclient.networking.ICallbacks;
import com.clientapp.battleshipclient.view.activities.MenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuLogic {
    /*
    this class intermediates between the MenuActivity and the networking section in GameLifecycleNW
    */

    private MenuActivity menuActivity;
    private User currPlayer;
    private static final int CREATE_GAME_MAX_TRIES = 20;
    private static int retryCreateGameCounter = 0;

    /* Constructor */
    public MenuLogic(MenuActivity optionsActivity, User currPlayer) {
        this.menuActivity = optionsActivity;
        this.currPlayer = currPlayer;
    }

    /* sets the onClick listener for the Start Playing button in the MenuActivity
    * including sending request to server to get the gameId after matching opponent
    * managing the response using callback methods*/
    public void setStartPlayingListener(Context context, Button startPlayingButton) {
        startPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayingButton.setClickable(false);
                Log.d("myDEBUG MenuLogic", "onClick: startPlayingButton");
                menuActivity.displayWaitingForOpponent();
                menuActivity.disableClickableButtons();
                startPlaying(context);
            }//onClick
        }); //setOnClickListener
    }//setStartPlayingListener

    private void startPlaying(Context context) {
        Log.d("myDEBUG MenuLogic", "******************************************* START_PLAYING ***: ");
        GameLifecycleNW.postCreateGame(context,  currPlayer.getId() ,new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {//callback method for the response
                menuActivity.cancelRunnable();
//                retryCreateGameCounter = 0;
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    Log.d("myDEBUG MenuLogic getGameId", "onResponse: " + jsonResponse);
                } catch (JSONException e) {
                    Log.e("myDEBUG MenuLogic getGameId", "onResponse error catch: " + e);
                    //TODO
                }
                String gameId = jsonResponse.optString("value");
                String message = jsonResponse.optString("message");
                Log.d("myDEBUG MenuLogic", "onResponse: " + gameId + " " + message);

                if (gameId != null) {
                    //postdelay navigation to anoher activity
                    ((MenuActivity)context).goToArrangeGameBoardActivity(gameId);
                }
                Log.d("myDEBUG response to gameId in callback", "setStartPlayingListener onResponse: " + response);
            }

            @Override
            public void onError(Exception e) {//callback method for the error
                if (e instanceof NoConnectionError) {
                    menuActivity.viewChangeOnRequestCanceled();
                }
//                Log.e("myDEBUG MenuLogic", "setStartPlayingListener onError: " + e);
//                if (retryCreateGameCounter < CREATE_GAME_MAX_TRIES) {
//                    retryCreateGameCounter++;
//                    //log retries
//                    Log.d("myDEBUG MenuLogic", "retry number: " + retryCreateGameCounter);
//                    startPlaying(context);
//                } else {
//                    menuActivity.cancelRunnable();
//                    menuActivity.viewChangeOnRequestCanceled();
//                    Log.e("myDEBUG MenuLogic", "cancelRunnable setStartPlayingListener onError: " + e);
//                }

                Log.e("myDEBUG MenuLogic ", "retry setStartPlayingListener onError: " + e);
            }//onError
        });//getGameId
    }


    /* calls the networking section so cancel the game request*/
    public void cancelRequests(Context context) {
        GameLifecycleNW.cancelRequests(context);
        GameLifecycleNW.notifyGameCanceled(context, currPlayer.getId(), new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                ((MenuActivity) context).onResume();
            }

            @Override
            public void onError(Exception e) {
                quitGame();
            }
        });
    }

    private void quitGame() {
//        gameNW.quitGame(new ICallbacks<String>() {
//            @Override
//            public void onResponse(String response) {
//                navigateToOptionsActivity();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                navigateToOptionsActivity();
//            }
//        });
    }


}

