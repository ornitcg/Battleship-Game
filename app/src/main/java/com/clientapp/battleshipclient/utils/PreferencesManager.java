package com.clientapp.battleshipclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    SharedPreferences prefs;

    public PreferencesManager(Context context) {
        this.prefs = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
    }

    public boolean isMusicMuted() {
        boolean isMuted = prefs.getBoolean("isMuted", false);
        return prefs.getBoolean("isMuted", isMuted);
    }

    public void setMusicMuted(boolean isMuted) {
        prefs.edit().putBoolean("isMuted", isMuted).apply();
    }

//    public boolean isResumedFromNavigation() {
//        return prefs.getBoolean("isNavigation", false);
//    }
//
//    public void setResumeType(boolean isNavigation) {
//        prefs.edit().putBoolean("isNavigation", isNavigation).apply();
//    }

}
