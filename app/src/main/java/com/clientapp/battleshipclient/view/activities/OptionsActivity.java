package com.clientapp.battleshipclient.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.clientapp.battleshipclient.R;

public class OptionsActivity extends AppCompatActivity {

    private boolean isMuted ; // Initial state

    private ImageButton muteButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }


    public void goToGameRooms(View v) {
        Intent intent = new Intent(OptionsActivity.this, GameRooms.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("isMuted", isMuted);
        startActivity(intent);
    }
}