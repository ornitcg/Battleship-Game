package com.clientapp.battleshipclient.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class PlaybackService extends Service {
    public static final String ACTION_PLAY = "com.example.action.PLAY";
    public static final String ACTION_PAUSE = "com.example.action.PAUSE";
    private MediaPlayer mediaPlayer;
    private AudioEnum currentMusicEnum;

    public PlaybackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        AudioEnum newMusicEnum = (AudioEnum) intent.getSerializableExtra("musicName");
        if (newMusicEnum != null)
            if (newMusicEnum != currentMusicEnum) {
                currentMusicEnum = newMusicEnum;
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
//        Log.d("DEBUG PlaybackService", "onStartCommand: " + currentMusicEnum);
        int resId = AudioUtils.getMusicResId(currentMusicEnum);
//        Log.d("DEBUG PlaybackService", "onStartCommand: " + resId);

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.setLooping(true);
        }
        if (intent != null && intent.getAction() != null) {
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

    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}