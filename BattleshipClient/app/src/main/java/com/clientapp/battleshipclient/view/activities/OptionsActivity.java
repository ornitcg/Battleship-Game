package com.clientapp.battleshipclient.view.activities;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class OptionsActivity extends BaseActivity {

    private String currPlayerUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        setupMusicToggleButton(this);
        Intent intent = this.getIntent();
        currPlayerUserId = intent.getStringExtra("currPlayerUserId");
//        AudioUtils.resumeMusicState(this); // keep the music state

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }


    public void goToGameRooms(View v) {
        Intent intent = new Intent(OptionsActivity.this, PlaceYourShips.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("userId", currPlayerUserId);
//        startActivity(intent);
        startActivity(intent);

    }

    public void goToLeaderBoard(View v) {
        Intent intent = new Intent(OptionsActivity.this, LeaderboardAndStats.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("userId", currPlayerUserId);
        startActivity(intent);
    }

//    public void quitGame(View v) {
//        Toast.makeText(this, "Goodbye!", Toast.LENGTH_SHORT).show();
//        System.exit(0);
//    }


    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( OptionsActivity.this); // mute the music
    }

}