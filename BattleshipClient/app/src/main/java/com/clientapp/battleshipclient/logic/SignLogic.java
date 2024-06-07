package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.util.Log;

import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.networking.ICallbacks;
import com.clientapp.battleshipclient.networking.UserNW;
import com.clientapp.battleshipclient.view.activities.SignActivity;

public class SignLogic {

    public static final int MAX_NAME_LENGTH = 15;
    public static final int MIN_NAME_LENGTH = 3;

    public static final int MAX_PASSWORD_LENGTH = 8;
    public static final int MIN_PASSWORD_LENGTH = 6;




    /**
     * Enum to represent the state of the authentication process
     * some of the states are used for debugging purposes
     */
    public enum AuthState {
        SIGNUP_SUCCEDED, SIGNIN_SUCCEDED, VALID,
        USER_EXISTS, INVALID_USERNAME, PENDING, WRONG_PASSWORD, USER_DOESNT_EXIST, INVALID_PASSWORD, CONNECTION_ERROR
    }

    public void signUp(Context context, User currentPlayer) {
        Log.d("DEBUG signLogic", "signUp: " + currentPlayer.getName());
        UserNW userNW = new UserNW(context, currentPlayer);
        AuthState validationState = getAuthState(context);
        if (validationState == AuthState.VALID) {
            userNW.signUp(currentPlayer, new ICallbacks<AuthState>() {
                @Override
                public void onResponseSuccess(SignLogic.AuthState state) {
                    Log.d("DEBUG signup", "signUpSuccess: " + state);
                    if (state == AuthState.SIGNUP_SUCCEDED) {
                        Log.d("DEBUG signup", "signUpSuccess: " + currentPlayer.getId());
                        ((SignActivity) context).setCurrentPlayer(currentPlayer);
                        ((SignActivity) context).goToMenuActivity(currentPlayer, false);
                    } else ((SignActivity) context).authFailureMsg(state);
                }

                @Override
                public void onError(Exception e) {
                    ((SignActivity) context).authFailureMsg(AuthState.CONNECTION_ERROR);
                    Log.d("DEBUG signup", "signUpError: Something Went Wrong" + e.getMessage());
                }
            });
        } else ((SignActivity) context).authFailureMsg(validationState);
    }


    public void signIn(Context context, User currentPlayer) {
        UserNW userNW = new UserNW(context, currentPlayer);
        AuthState validationState = getAuthState(context);
        if (validationState == AuthState.VALID) {
            userNW.signIn(currentPlayer, new ICallbacks<AuthState>() {
                @Override
                public void onResponseSuccess(SignLogic.AuthState state) {
                    Log.d("DEBUG signup", "signUpSuccess: " + state);
                    if (state == AuthState.SIGNIN_SUCCEDED) {
                        ((SignActivity) context).setCurrentPlayer(currentPlayer);
                        ((SignActivity) context).goToMenuActivity(currentPlayer, false);
                    } else ((SignActivity) context).authFailureMsg(state);
                }

                @Override
                public void onError(Exception e) {
                    ((SignActivity) context).authFailureMsg(AuthState.CONNECTION_ERROR);
                    Log.d("DEBUG signup", "signUpError: " + e.getMessage());
                }
            });
        } else ((SignActivity) context).authFailureMsg(validationState);
    }

    public static AuthState getAuthState(Context context) {

        if (!((SignActivity) context).isUsernameValid())
            return AuthState.INVALID_USERNAME;

        if (!((SignActivity) context).isPasswordValid())
            return AuthState.INVALID_PASSWORD;
//        Log.d("DEBUG User", "isValidUser: " + AuthenticationState.VALID);
        return AuthState.VALID;
    }
}
