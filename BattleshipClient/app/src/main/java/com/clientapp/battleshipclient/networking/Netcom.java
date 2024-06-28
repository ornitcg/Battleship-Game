package com.clientapp.battleshipclient.networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Netcom {
    private static Netcom instance;
    private static RequestQueue requestQueue;
    private static Context context;


    /*
     *  Constructor for Netcom
     * */
    private Netcom(Context context) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
        requestQueue = getRequestQueue();
    }


    /*
     *  Singleton pattern to ensure only one instance of Netcom is created
     *  and to provide a global point of access to it.
     * */
    public static synchronized Netcom getInstance(Context context) {
        if (instance == null) {
            instance = new Netcom(context);
        }
        return instance;
    }


    /*
     *  Method to get the request queue
     *  If request queue is null, create a new request queue
     * */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }


    /*
     *  Method to add a request to the request queue
     *  @param request: the request to be added to the request queue
     * */
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }




}
