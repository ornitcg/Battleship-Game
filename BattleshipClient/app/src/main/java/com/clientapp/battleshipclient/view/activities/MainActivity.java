package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.networking.EndpointResources;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EndpointResources.initializeEndpoints(this); //initialize the endpoints
        setPreferences(); //first set preferences

        setMusicService(AudioEnum.LOBBY_MUSIC); // set the music service

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainActivityMainId), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void initView() { //called here to add the set enter button
        super.initView();
        setEnterButton();
    }





    private void setPreferences() {
        PreferencesManager prefs = PreferencesManager.getInstance(this);
        prefs.setIsMusicMuted(false);
        prefs.setIsSoundsMuted(false);
        Log.d("DEBUG on Main Activity", "setPreferences: " + prefs.isMusicMuted());
        Log.d("DEBUG on Main Activity", "setPreferences: " + prefs.isSoundsMuted());
    }


    private void setEnterButton() {
        Button enterBtn = findViewById(R.id.enterBtnId);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG on Main Activity", "setEnterButton onClick: buttonSound is not null");
                AudioUtils.makeSound(MainActivity.this, AudioEnum.BUTTON);
                goToSignForm(v);
            }
        });
    }



    public void onStart() {
        super.onStart();
        Log.d("DEBUG on Main Activity", "onStart: " + prefs.isMusicMuted());
    }
    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }

    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(MainActivity.this); // mute the music
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(MainActivity.this); // mute the music
    }


    public void onDestroy() {
        super.onDestroy();
//        stopService(new Intent(this, PlaybackService.class));
    }
}