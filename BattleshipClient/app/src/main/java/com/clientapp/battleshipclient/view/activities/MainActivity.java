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
import com.clientapp.battleshipclient.networking.NWutils.EndpointResources;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;


/*
 *  This class represents the main activity of the application
 *  It is the first activity that is launched when the application is started
 * */
public class MainActivity extends BaseActivity {

    /*
     *  This method is called when the activity is first created
     *  It is called after the onStart method
     *  It is called after the onRestoreInstanceState method
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EndpointResources.initializeEndpoints(""); //initialize the endpoints
        setPreferences(); //first set preferences

        setMusicService(AudioEnum.LOBBY_MUSIC); // set the music service

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainActivityMainId), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /*
     *  This method is called to initialize the view
     *  It is called in the onCreate method
     * */
    @Override
    protected void initView() { //called here to add the set enter button
        super.initView();
        setEnterButton();
    }


    /*
     *  This method is called to set the preferences
     * */
    private void setPreferences() {
        PreferencesManager prefs = PreferencesManager.getInstance(this);
        prefs.setIsMusicMuted(false);
        prefs.setIsSoundsMuted(false);
        Log.d("DEBUG on Main Activity", "setPreferences: " + prefs.isMusicMuted());
        Log.d("DEBUG on Main Activity", "setPreferences: " + prefs.isSoundsMuted());
    }


    /*
     *  This method is called to set the enter button
     * */
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


    /*
     *  Navigate to the sign form
     * */
    public void goToSignForm(View v) {
        Intent intent = new Intent(MainActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }


    /*
     *  This method is called when the activity is visible to the user
     *  It is called after the onCreate method
     * */
    @Override
    public void onStart() {
        super.onStart();
        Log.d("DEBUG on Main Activity", "onStart: " + prefs.isMusicMuted());
    }

    /*
     *  Override the onPause method
     * */
    @Override
    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(MainActivity.this); // mute the music
    }


    /*
     *  Override the onResume method
     * */
    @Override
    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(MainActivity.this); // mute the music
    }


}