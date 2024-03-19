package com.clientapp.battleshipclient.view.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;

public class LeaderboardAndStats extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_active_play);
        setupMusicToggleButton(this);
        AudioUtils.keepMusicState(this); // keep the music state




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onDestroy() {
        super.onDestroy();
        AudioUtils.pauseMusic(this);
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        AudioUtils.keepMusicState( LeaderboardAndStats.this); // mute the music
    }
}