package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class GameRooms extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rooms);
        setupMusicToggleButton(this);

        RecyclerView roomsList = findViewById(R.id.roomsRecyclerViewId);
        //create here a list instead of getting from the server


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void goToPlaceYourShips(View v) {
        Intent intent = new Intent(GameRooms.this, PlaceYourShips.class);
//        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        startActivity(intent);
    }



    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( GameRooms.this); // mute the music
    }
}