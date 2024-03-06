package com.clientapp.battlshipclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private boolean isMuted = false; // Initial state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, PlaybackService.class));  //call for playback to play music
    }

    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, SignUpSignInActivity.class);
        startActivity(intent);
    }


    public void muteUnmute(View v) {
        // Toggle the mute state
        isMuted = !isMuted;
        Intent muteOrUnmute = new Intent();
        if (isMuted) {
            muteOrUnmute.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
        }
        else {
            muteOrUnmute.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
        }
//        Intent intent = new Intent(PlaybackService.ACTION_MUTE_UNMUTE); // sets the intent's action to be PlaybackService.ACTION_MUTE_UNMUTE
//        intent.putExtra("mute", isMuted);
        sendBroadcast(muteOrUnmute);
    }

    public void onPause() {
        // Register the receiver
        super.onPause();
        Intent pauseIntent = new Intent(MainActivity.this, PlaybackService.class);
        pauseIntent.setAction(PlaybackService.ACTION_PAUSE);
        startService(pauseIntent);
    }
    public void onResume() {
        // Register the receiver
        super.onResume();
        Intent playIntent = new Intent(MainActivity.this, PlaybackService.class);
        playIntent.setAction(PlaybackService.ACTION_PLAY);
        startService(playIntent);
    }

}