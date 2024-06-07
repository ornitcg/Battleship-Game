package com.clientapp.battleshipclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.Services.PlaybackService;

public class AudioUtils {


    public static AudioEnum currentMusic = null;

    public static int getMusicResId(AudioEnum musicEnum) {
        currentMusic = musicEnum;
        switch (musicEnum) {
            case LOBBY_MUSIC:
                return R.raw.main_activity_music;
            case ARRANGE_GAMEBOARD:
                return R.raw.getting_ready_music;
            case GAME_MUSIC:
                return R.raw.game_music;
            case WIN_MUSIC:
                return R.raw.sound_win;
            case LOSE_MUSIC:
                return R.raw.sound_lost2;
            case LEADERBOARD_MUSIC:
                return R.raw.leaderboard;
            default:
                return R.raw.main_activity_music;
        }

    }

//    public static enum AudioEnum {
//        GETTING_READY_MUSIC, GAME_MUSIC, WIN_MUSIC, LOSE_MUSIC, LEADERBOARD_MUSIC, LOBBY_MUSIC
//    }

    ;

    public static void toggleMusic(Context context) {
        //save mute state across app
        PreferencesManager prefs = PreferencesManager.getInstance(context);
        ImageButton toggleMusicButton = ((Activity) context).findViewById(R.id.toggleMusicBtnId);
        boolean isMuted = prefs.isMusicMuted();
        isMuted = !isMuted;
        prefs.setIsMusicMuted(isMuted);
        //request mute change
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        if (isMuted) {
            serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
            Log.d("myDEBUG AudioUtils", "toggleMusic: " + "Muted");
        } else {
            serviceIntent.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
            Log.d("myDEBUG AudioUtils", "toggleMusic: " + "Unmuted");
        }
        context.startService(serviceIntent);
    }

    public static void toggleSounds(Context context) {
        PreferencesManager prefs = PreferencesManager.getInstance(context);
        boolean isMuted = prefs.isSoundsMuted();
        isMuted = !isMuted;
        prefs.setIsSoundsMuted(isMuted);
        Log.d("myDEBUG AudioUtils", "toggleSounds: " + isMuted);
    }

    public static void resumeMusicState(Context context) {
        PreferencesManager prefs = PreferencesManager.getInstance(context);
        Activity currActivity = (Activity) context;
        ImageButton toggleMuteButton = currActivity.findViewById(R.id.toggleMusicBtnId);
        boolean isMuted = prefs.isMusicMuted();

        //request mute change
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        if (isMuted) {
            serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
            toggleMuteButton.setImageResource(R.drawable.icon_music_off); // replace with your mute icon
        } else {
            serviceIntent.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
            toggleMuteButton.setImageResource(R.drawable.icon_music_on);
        }
        context.startService(serviceIntent);
    }

    public static void pauseMusic(Context context) { //no change of button
        Activity currActivity = (Activity) context;
        Intent serviceIntent = new Intent(context, PlaybackService.class);
        serviceIntent.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
        context.startService(serviceIntent);
    }

    public static void playSound(Context context, MediaPlayer sound) {
        PreferencesManager prefs = PreferencesManager.getInstance(context);
        sound.setVolume(prefs.getVolume(), prefs.getVolume());
        Log.d("myDEBUG AudioUtils", "playSound: " + prefs.getVolume());
        if (sound != null) {
            sound.start();
            sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer sound) {
                    sound.release();
                    Log.d("myDEBUG AudioUtils", "onCompletion: " + "sound released");
                }
            });
        }
    }

    public static void makeSound(Context context, AudioEnum soundCase) {
        Log.d("myDEBUG makeSound", "makeSound: soundCase: " + soundCase);
        MediaPlayer sound = null;
        switch (soundCase) {
            case HIT:
                sound = MediaPlayer.create(context, R.raw.sound_hit);
                break;
            case MISS:
                sound = MediaPlayer.create(context, R.raw.sound_miss);
                break;
            case SUNK:
                sound = MediaPlayer.create(context, R.raw.sound_sunk);
                break;
            case WIN:
                sound = MediaPlayer.create(context, R.raw.sound_win);
                break;
            case LOSE_SOUND:
                sound = MediaPlayer.create(context, R.raw.sound_lost2);
                break;
            case GAME_OVER:
                sound = MediaPlayer.create(context, R.raw.sound_game_over);
                break;
            case BUTTON:
                sound = MediaPlayer.create(context, R.raw.sound_button);
                break;
        }
        AudioUtils.playSound(context, sound);
        if (sound == null) {
            Log.d("myDEBUG AudioUtils", "sound is null");
        }

    }//end makeSound
}
