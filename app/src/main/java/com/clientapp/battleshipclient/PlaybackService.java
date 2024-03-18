package com.clientapp.battleshipclient;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlaybackService extends Service {
    public static final String ACTION_PLAY = "com.example.action.PLAY";
    public static final String ACTION_PAUSE = "com.example.action.PAUSE";
    private MediaPlayer mediaPlayer;
    public PlaybackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if  (mediaPlayer == null ) {
            mediaPlayer = MediaPlayer.create(this, R.raw.main_activity_music);
            mediaPlayer.setLooping(true);
        }

        if (intent != null  && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_PLAY)) {
                // Code to start playback
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else if (action.equals(ACTION_PAUSE)) {
                // Code to pause playback
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        }
        return START_STICKY;
    }

//
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}