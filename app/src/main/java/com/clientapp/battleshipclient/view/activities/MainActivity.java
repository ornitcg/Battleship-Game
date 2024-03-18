package com.clientapp.battleshipclient.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.clientapp.battleshipclient.PlaybackService;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    private boolean isMuted; // Initial state
    private Button startbtn;
    private ImageButton toogleMuteBtn;
    private MediaPlayer buttonSound;

    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = new PreferencesManager(this);
        prefs.setMusicMuted(false); //because we want the music to turn on by default
        // before onClick for faster response
        buttonSound = MediaPlayer.create(MainActivity.this,R.raw.enter_button_sound);
        toogleMuteBtn = findViewById(R.id.muteBtnId);
        startService(new Intent(this, PlaybackService.class));  //call for playback to play music


        toogleMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.toggleMusic( MainActivity.this ); // mute the music
            }
        });  // end onClick listener


        //        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttonSound.start();
//                goToSignForm(v);
//            }
//        });
    }


    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, SignUpSignInActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        startActivity(intent);
    }


    public void onUserLeaveHint() {
        super.onUserLeaveHint();
//        AudioUtils.pauseMusic(this);
        Toast.makeText(this, "on user leave hint", Toast.LENGTH_SHORT).show();


    }

    public void onPause() {
        super.onPause();
//        AudioUtils.pauseMusic(this);
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        // Register the receiver
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

        Intent playIntent = new Intent(MainActivity.this, PlaybackService.class);
        AudioUtils.keepMusicState( MainActivity.this); // mute the music

    }

}