package com.clientapp.battleshipclient.networking;


public interface ICallbacks<T> {
    void onResponseSuccess(T response);

    //    void onFailure(T response);
    void onError(Exception e);
}