package com.clientapp.battleshipclient.view.activities;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clientapp.battleshipclient.Services.PlaybackService;
import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.utils.AudioUtils;

public class MainActivity extends BaseActivity {

    private Button startbtn;
    private MediaPlayer buttonSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSound = MediaPlayer.create(MainActivity.this,R.raw.enter_button_sound);
        InitializeMusicToggleButton(this);  //call methods from base activity
        setupMusicToggleButton(this);
        startService(new Intent(this, PlaybackService.class));  //call for playback to play music


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //        startbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttonSound.start();
//                goToSignForm(v);
//            }
//        });
    }






    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, PlaceYourShips.class);
        intent.putExtra("buttonSound", R.raw.enter_button_sound);
        intent.putExtra("currPlayerUserId", "12345"); // TODO *** for DEBUG , delete this line

        startActivity(intent);
    }



    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PlaybackService.class));
    }


    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic( MainActivity.this); // mute the music
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState( MainActivity.this); // mute the music
    }


}