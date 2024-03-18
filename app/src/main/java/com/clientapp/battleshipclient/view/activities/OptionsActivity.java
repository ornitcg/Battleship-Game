package com.clientapp.battleshipclient.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;

public class OptionsActivity extends AppCompatActivity {

    private boolean isMuted ; // Initial state

    private ImageButton toogleMuteBtn;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        toogleMuteBtn = findViewById(R.id.muteBtnId);
        prefs = new PreferencesManager(this);
        AudioUtils.keepMusicState(this); // keep the music state


        toogleMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuted=!isMuted;
                prefs.setMusicMuted(!isMuted);
                AudioUtils.toggleMusic( OptionsActivity.this); // mute the music
            }
        });
    }


    public void goToGameRooms(View v) {
        Intent intent = new Intent(OptionsActivity.this, GameRooms.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("isMuted", isMuted);
        startActivity(intent);
    }
}