package com.clientapp.battleshipclient.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clientapp.battleshipclient.R;

public class SignUpSignInActivity extends AppCompatActivity {

    private boolean playBackOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);

    }


    public void goToOptionsActivity(View v) {
        Intent intent = new Intent(SignUpSignInActivity.this, OptionsActivity.class);
        startActivity(intent);
    }
}