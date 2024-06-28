package com.clientapp.battleshipclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/*
*  This class is used to manage the preferences of the app
*  It is used to store the settings of the app
*  */
public class PreferencesManager {
    private static PreferencesManager instance;
    private SharedPreferences prefs;

    /*
    *  Constructor
    * */
    private PreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("appSettings", Context.MODE_PRIVATE);
    }


    /*
    *  Singleton pattern
    *  This method is used to get the instance of the class
    * It is used to make sure that only one instance of the class is created
    * */
    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }


    /*
    *  Getters and setters for the preferences
    * */
    public boolean isMusicMuted() {
        return prefs.getBoolean("isMusicMuted", false);
    }

    /*
    *  Getters and setters for the preferences
    * */
    public boolean isSoundsMuted() {
        return prefs.getBoolean("isSoundsMuted", false);
    }

    /*
    *  Getters and setters for the preferences
    * */
    public void setIsMusicMuted(boolean isMuted) {
        prefs.edit().putBoolean("isMusicMuted", isMuted).apply();
        Log.d("DEBUG PreferencesManager", "setMusicMuted: " + isMuted);
    }

    /*
     * Getters and setters for the preferences
     */
    public float getVolume() {
        if (prefs.getBoolean("isSoundsMuted", false)) {
            return 0f;
        }
        return 1f;
    }

    /*
    *  Getters and setters for the preferences
    *  */
    public void setIsSoundsMuted(boolean isMuted) {
        prefs.edit().putBoolean("isSoundsMuted", isMuted).apply();
        Log.d("DEBUG PreferencesManager", "setSoundsMuted: " + isMuted);
    }


}
