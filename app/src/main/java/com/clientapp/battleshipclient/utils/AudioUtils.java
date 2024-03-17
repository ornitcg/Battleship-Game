package com.clientapp.battleshipclient.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.PlaybackService;
import com.clientapp.battleshipclient.R;

public class AudioUtils {

    public static void muteUnmute( Context context,View v, boolean isMuted) {
        //save mute state across app
        SharedPreferences prefs = context.getSharedPreferences("appSettings", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isMuted", isMuted); // add the mute state to preferences "appSettings" file
        editor.apply();

        //request mute change
        ImageButton muteButton = (ImageButton) v; //casting for v
//        Intent muteOrUnmute = new Intent();
        Intent serviceIntent = new Intent(context, PlaybackService.class);

        if (isMuted) {
            serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
            muteButton.setImageResource(R.drawable.music_off_icon); // replace with your mute icon

        }
        else {
            serviceIntent.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
            muteButton.setImageResource(R.drawable.music_icon);

        }


//
        context.sendBroadcast(serviceIntent);
    }
}
