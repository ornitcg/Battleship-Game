package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.networking.UserNW;
import com.clientapp.battleshipclient.view.activities.SignActivity;

public class SignLogic {

    public static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    public static final int MAX_NAME_LENGTH = 15;
    public static final int MIN_NAME_LENGTH = 3;

    public static final int MAX_PASSWORD_LENGTH = 8;
    public static final int MIN_PASSWORD_LENGTH = 4;
    private static final int KEEP_USER_ALIVE_DELAY_MILLIS = 5000;

    public static Runnable keepUserAlivetask;
    public static Handler keepUserHandler = new Handler();



    public void signUser(Context context, AuthTypeEnum mission, User currentPlayer) {
        Log.d("DEBUG signLogic", "signUser: " + currentPlayer.getName());
        AuthStateEnum validationState = getAuthState(context);
        if (validationState == AuthStateEnum.VALID) {
            UserNW.signUser(context, currentPlayer, mission, new ICallbacks<AuthStateEnum>() {
                @Override
                public void onResponseSuccess(AuthStateEnum state) {
                    setKeepUserAliveRunnable(context, currentPlayer.getId());
                    Log.d("myDEBUG signUser", "signLogic signUser Success: " + state);
                    if (state == AuthStateEnum.SUCCEEDED) {
                        Log.d("myDEBUG signUser", "signLogic signUser Success: " + currentPlayer.getId());
                        keepUserHandler.postDelayed(keepUserAlivetask, KEEP_USER_ALIVE_DELAY_MILLIS);
                        ((SignActivity) context).setCurrentPlayer(currentPlayer);
                        ((SignActivity) context).goToMenuActivity(currentPlayer, false);
                    } else ((SignActivity) context).displayAuthStateMsg(state);
                }

                @Override
                public void onError(Exception e) {
                    ((SignActivity) context).displayAuthStateMsg(AuthStateEnum.CONNECTION_ERROR);
                    Log.d("DEBUG signup", "signUpError: Something Went Wrong" + e.getMessage());
                }
            });
        } else ((SignActivity) context).displayAuthStateMsg(validationState);
    }



    public void setKeepUserAliveRunnable(Context context, String currenPlayerId){
        keepUserAlivetask = new Runnable(){
            @Override
            public void run() {
                UserNW.keepUserAlive( context , currenPlayerId );                // Reschedule this Runnable to run again after keepAlivePostDelayMillis
                keepUserHandler.postDelayed(this, KEEP_USER_ALIVE_DELAY_MILLIS);
                //keep the user alive
            }

        };

    }




    public static AuthStateEnum getAuthState(Context context) {

        if (!((SignActivity) context).isUsernameValid())
            return AuthStateEnum.INVALID_USERNAME;

        if (!((SignActivity) context).isPasswordValid())
            return AuthStateEnum.INVALID_PASSWORD;
//        Log.d("DEBUG User", "isValidUser: " + AuthenticationState.VALID);
        return AuthStateEnum.VALID;
    }
}
