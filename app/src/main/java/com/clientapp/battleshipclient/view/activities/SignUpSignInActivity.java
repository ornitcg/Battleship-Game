package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class SignUpSignInActivity extends BaseActivity {




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


    }


    public void goToOptionsActivity(View v) {
        Intent intent = new Intent(SignUpSignInActivity.this, OptionsActivity.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        startActivity(intent);
    }



    public void onPause() {
        super.onPause();
        Toast.makeText(this, "on pause SIGN", Toast.LENGTH_SHORT).show();
        AudioUtils.pauseMusic( SignUpSignInActivity.this); // mute the music

    }


    public void onResume() {
        super.onResume();
       // Toast.makeText(this, "on resume SIGN", Toast.LENGTH_SHORT).show();
        AudioUtils.resumeMusicState( SignUpSignInActivity.this); // mute the music
    }



}