package com.clientapp.battleshipclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesManager {
    private static PreferencesManager instance;
    private SharedPreferences prefs;
    private static Context context;

    private PreferencesManager(Context context) {
//        this.prefs = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
        prefs = context.getApplicationContext().getSharedPreferences("appSettings", Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public boolean isMusicMuted() {
        return prefs.getBoolean("isMusicMuted", false);
    }
    public boolean isSoundsMuted() {
        return prefs.getBoolean("isSoundsMuted", false);
    }

    public void setIsMusicMuted(boolean isMuted) {
        prefs.edit().putBoolean("isMusicMuted", isMuted).apply();
        Log.d("DEBUG PreferencesManager", "setMusicMuted: " + isMuted);
    }

    public float getVolume() {
        if (prefs.getBoolean("isSoundsMuted", false)) {
            return 0f;
        }
        return 1f;
    }

    public void setIsSoundsMuted(boolean isMuted) {
        prefs.edit().putBoolean("isSoundsMuted", isMuted).apply();
        Log.d("DEBUG PreferencesManager", "setSoundsMuted: " + isMuted);
    }


}
