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
import com.clientapp.battleshipclient.utils.AudioUtils;

public class MainActivity extends AppCompatActivity {

    private boolean isMuted = true; // Initial state
    private Button startbtn;
    private ImageButton muteButton ;
    private MediaPlayer buttonSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // before onClick for faster response
        buttonSound = MediaPlayer.create(MainActivity.this,R.raw.enter_button_sound);
        muteButton = findViewById(R.id.muteBtnId);

//        buttonSound = MediaPlayer.create(this,R.raw.sign_in_button);
        startService(new Intent(this, PlaybackService.class));  //call for playback to play music
//        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttonSound.start();
//                goToSignForm(v);
//            }
//        });
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuted=!isMuted;
                AudioUtils.muteUnmute( MainActivity.this, muteButton,isMuted ); // mute the music
            }
        });
    }


    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, SignUpSignInActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("isMuted", isMuted);
        startActivity(intent);
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