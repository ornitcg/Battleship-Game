package com.clientapp.battleshipclient.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.PlaybackService;
import com.clientapp.battleshipclient.R;

public class MainActivity extends AppCompatActivity {

    private boolean isMuted = false; // Initial state
    private Button startbtn;
    private MediaPlayer buttonSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startbtn = findViewById(R.id.enterBtnId);

        // before onClick for faster response
        buttonSound = MediaPlayer.create(MainActivity.this,R.raw.enter_button_sound);


//        buttonSound = MediaPlayer.create(this,R.raw.sign_in_button);
        startService(new Intent(this, PlaybackService.class));  //call for playback to play music
//        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttonSound.start();
//                goToSignForm(v);
//            }
//        });
    }

//    public void goToSignForm(View v) {
//        Intent intent = new Intent(MainActivity.this, SignUpSignInActivity.class);
//        startActivity(intent);
//    }
    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, SignUpSignInActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        startActivity(intent);
    }


    public void muteUnmute(View v) {
        // Toggle the mute state
        isMuted = !isMuted;

        //save mute state across app
        SharedPreferences prefs = getSharedPreferences("appSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isMuted", isMuted); // add the mute state to preferences "appSettings" file

        //request mute change
        ImageButton muteButton = (ImageButton) v; //casting for v
        Intent muteOrUnmute = new Intent();
        if (isMuted) {
            muteOrUnmute.setAction(PlaybackService.ACTION_PAUSE); // sets the intent's action to be PlaybackService.ACTION_PAUSE
            muteButton.setImageResource(R.drawable.music_off_icon); // replace with your mute icon

        }
        else {
            muteOrUnmute.setAction(PlaybackService.ACTION_PLAY); // sets the intent's action to be PlaybackService.ACTION_PLAY
            muteButton.setImageResource(R.drawable.music_icon);

        }


//
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