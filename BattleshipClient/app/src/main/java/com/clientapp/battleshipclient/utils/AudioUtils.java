package com.clientapp.battleshipclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.Services.PlaybackService;
import com.clientapp.battleshipclient.R;

public class AudioUtils {

    public static void toggleMusic(Context context) {
        //save mute state across app
        PreferencesManager prefs = new PreferencesManager(context);
        Activity currActivity = (Activity) context;
        ImageButton toggleMuteButton = currActivity.findViewById(R.id.muteBtnId);

        boolean isMuted = prefs.isMusicMuted();
        isMuted = !isMuted;
        prefs.setMusicMuted(isMuted);

        //request mute change
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        if (isMuted) {
            serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
//            Toast.makeText(context, "toggle off", Toast.LENGTH_SHORT).show();

            toggleMuteButton.setImageResource(R.drawable.music_off_icon); // replace with your mute icon
//            Toast.makeText(context, "toggle on", Toast.LENGTH_SHORT).show();

        }
        else {
            serviceIntent.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
            toggleMuteButton.setImageResource(R.drawable.music_icon);
        }
        context.startService(serviceIntent);
    }



    public static void resumeMusicState(Context context) {
        PreferencesManager prefs = new PreferencesManager(context);
        Activity currActivity = (Activity) context;
        ImageButton toggleMuteButton = currActivity.findViewById(R.id.muteBtnId);
        boolean isMuted = prefs.isMusicMuted();

        //request mute change
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        if (isMuted) {
            serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
//            Toast.makeText(context, "keep off", Toast.LENGTH_SHORT).show();

            toggleMuteButton.setImageResource(R.drawable.music_off_icon); // replace with your mute icon

        }
        else {
            serviceIntent.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
//            Toast.makeText(context, "keep on", Toast.LENGTH_SHORT).show();

            toggleMuteButton.setImageResource(R.drawable.music_icon);

        }
        context.startService(serviceIntent);
    }


    public static void turnOnMusic(Context context) {
        Activity currActivity = (Activity) context;
        ImageButton toggleMuteButton = currActivity.findViewById(R.id.muteBtnId);
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        serviceIntent.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
        toggleMuteButton.setImageResource(R.drawable.music_icon);
        context.startService(serviceIntent);
    }

    public static void pauseMusic(Context context) { //no change of button
        Activity currActivity = (Activity) context;
        ImageButton toggleMuteButton = currActivity.findViewById(R.id.muteBtnId);
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
//        Toast.makeText(context, "pauseMusic" + ((Activity) context).getLocalClassName(), Toast.LENGTH_SHORT).show();
        context.startService(serviceIntent);
    }

}
