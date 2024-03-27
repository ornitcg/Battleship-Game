package com.clientapp.battleshipclient.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;

public class BaseActivity extends AppCompatActivity {

    private boolean isMuted; // Initial state
    private ImageButton toogleMuteBtn;
    private PreferencesManager prefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    protected void setupMusicToggleButton(Context context) {
        toogleMuteBtn = findViewById(R.id.muteBtnId);
        prefs = new PreferencesManager(context);
        AudioUtils.resumeMusicState(context); // keep the music state

        toogleMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuted=!isMuted;
                prefs.setMusicMuted(!isMuted);
                AudioUtils.toggleMusic( BaseActivity.this); // mute the music
            }
        });
    }

    protected void InitializeMusicToggleButton(Context context) {
        toogleMuteBtn = findViewById(R.id.muteBtnId);
        prefs = new PreferencesManager(context);
        prefs.setMusicMuted(false); //because we want the music to turn on by default
    }






}