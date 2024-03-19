package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;

public class SignUpSignInActivity extends BaseActivity {

    private boolean isMuted; // Initial state
    private ImageButton toogleMuteBtn;
    private PreferencesManager prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);
        setupMusicToggleButton(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toogleMuteBtn = findViewById(R.id.muteBtnId);
        prefs = new PreferencesManager(this);
        AudioUtils.keepMusicState(this); // keep the music state


        toogleMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuted=!isMuted;
                prefs.setMusicMuted(!isMuted);
                AudioUtils.toggleMusic( SignUpSignInActivity.this); // mute the music
            }
        });

    }


    public void goToOptionsActivity(View v) {
        Intent intent = new Intent(SignUpSignInActivity.this, OptionsActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        startActivity(intent);
    }


    public void onRestart() {
        super.onRestart();
        AudioUtils.keepMusicState( SignUpSignInActivity.this); // mute the music
    }

//    public void onDestroy() {
//        super.onDestroy();
//        AudioUtils.pauseMusic(this);
//    }

    public void onStop() {
        super.onStop();
            AudioUtils.pauseMusic( SignUpSignInActivity.this); // mute the music
    }
    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic( SignUpSignInActivity.this); // mute the music

    }
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        AudioUtils.keepMusicState( SignUpSignInActivity.this); // mute the music

    }

    public void onResume() {
        super.onResume();
        AudioUtils.keepMusicState( SignUpSignInActivity.this); // mute the music
    }



}