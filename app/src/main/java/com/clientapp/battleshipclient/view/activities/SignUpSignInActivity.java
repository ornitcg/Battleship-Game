package com.clientapp.battleshipclient.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.PlaybackService;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class SignUpSignInActivity extends AppCompatActivity {

    private boolean isMuted ; // Initial state

    private ImageButton muteButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);
        muteButton = findViewById(R.id.muteBtnId);
        SharedPreferences prefs = getSharedPreferences("appSettings", MODE_PRIVATE);
        isMuted = prefs.getBoolean("isMuted", true);


//        if (!isMuted) {
//            Intent playIntent = new Intent(this, PlaybackService.class);
//            playIntent.setAction(PlaybackService.ACTION_PLAY);
//            startService(playIntent);
//        }

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuted=!isMuted;

                AudioUtils.muteUnmute( SignUpSignInActivity.this, muteButton, isMuted ); // mute the music
            }
        });

    }



    public void goToOptionsActivity(View v) {
        Intent intent = new Intent(SignUpSignInActivity.this, OptionsActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("isMuted", isMuted);
        startActivity(intent);
    }
}