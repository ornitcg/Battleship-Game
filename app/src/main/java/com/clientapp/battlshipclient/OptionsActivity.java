package com.clientapp.battlshipclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }


    public void goToGameRooms(View v) {
        Intent intent = new Intent(OptionsActivity.this, GameRooms.class);
        startActivity(intent);
    }
}