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
    private boolean musicStarted = false;
    private boolean musicPlaying = false;
    public PlaybackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if  (mediaPlayer == null )
            mediaPlayer = MediaPlayer.create(this, R.raw.main_activity_music);

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
//        return super.onStartCommand(intent, flags, startId);
    }

    private final BroadcastReceiver muteOrPlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mediaPlayer != null && intent !=null) {
                String action = intent.getAction();

                if (action.equals(ACTION_PAUSE)) { // Check for the 'mute' extra value
                    mediaPlayer.setVolume(0, 0); // Mute
                } else {
                    mediaPlayer.setVolume(1, 1); // Unmute
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // Register the receiver for both play and pause actions
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        registerReceiver(muteOrPlayReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        unregisterReceiver(muteOrPlayReceiver);
    }

}