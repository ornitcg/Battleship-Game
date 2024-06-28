package com.clientapp.battleshipclient.logic;


public interface ICallbacks<T> {
    void onResponseSuccess(T response);

    //    void onFailure(T response);
    void onError(Exception e);
}