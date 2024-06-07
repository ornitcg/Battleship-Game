package com.clientapp.battleshipclient.view.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.Services.PlaybackService;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;
import com.clientapp.battleshipclient.view.UI_utils.SwipeGestureListener;

import lombok.Getter;
import lombok.Setter;

public class BaseActivity extends AppCompatActivity implements SwipeGestureListener {

    private ImageButton toggleMusicBtn;
    @Getter
    private ImageButton toggleSoundsBtn;
    @Getter
    private ImageButton xbutton;
    private View yesNoLayout = null;
    protected PreferencesManager prefs;
    @Getter
    @Setter
    protected User currentPlayer;
    @Getter
    @Setter
    protected boolean brutalDestroy = true;
    @Getter
    @Setter
    protected boolean allowBackNavigation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this); // enable edge to edge screen display
        prefs = PreferencesManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    protected void initView() { // CAN OVERRIDE THIS TO INITIALIZE CHILD VIEWS
        initializeMusicToggleButton();  //call methods from base activity
        initializeSoundsToggleButton();  //call methods from base activity
        setToggleSoundsButton(); //call methods from base activity
        setToggleMusicButton(); //call methods from base activity
        setSoundsToggleButtonClickListener(this); //call methods from base activity
        setMusicToggleButtonClickListener(this); //call methods from base activity
        setYesNoLayout();
        setXbutton();

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.baseActivityMainId), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }


    public void setUserFromIntent(Intent intent) {
        setCurrentPlayer((User) intent.getSerializableExtra("currentPlayer"));
        Log.d("DEBUG BaseActivity", "setUserFromIntent: " + getCurrentPlayer().getId() + " " + getCurrentPlayer().getName());
    }


    protected void setToggleSoundsButton() {
        if (prefs.isSoundsMuted()) {
            toggleSoundsBtn.setImageResource(R.drawable.icon_sounds_off);
        } else {
            toggleSoundsBtn.setImageResource(R.drawable.icon_sounds_on);
        }
    }

    protected void setToggleMusicButton() {
        if (prefs.isMusicMuted()) {
            toggleMusicBtn.setImageResource(R.drawable.icon_music_off);
        } else {
            toggleMusicBtn.setImageResource(R.drawable.icon_music_on);
        }
    }

    protected void setMusicToggleButtonClickListener(Context context) {
        toggleMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG BaseActivity", "onClick: toggleMusicBtn");
                AudioUtils.toggleMusic(context); // mute the music
                setToggleMusicButton();
            }
        });
    }

    protected void setSoundsToggleButtonClickListener(Context context) {
        toggleSoundsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log clicked
                Log.d("DEBUG BaseActivity", "onClick: toggleSoundsBtn");
                AudioUtils.toggleSounds(context); // mute the sounds
                setToggleSoundsButton();
            }
        });
    }

    protected void setMusicService(AudioEnum musicEnvironment) {
        Intent intent = new Intent(this, PlaybackService.class);
        intent.putExtra("musicName", musicEnvironment);
        startService(intent);  //call for playback to play music
    }


    protected void replaceMusic(AudioEnum audioEnum) {
        setMusicService(audioEnum);
    }

    protected void setXbutton() {
        xbutton = (ImageButton) findViewById(R.id.XbuttonId);
        xbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG BaseActivity", "onClick: exitBtn");
                displayExitLayout();
                MediaPlayer sound = MediaPlayer.create(BaseActivity.this, R.raw.sound_button);
                AudioUtils.playSound(BaseActivity.this, sound);
            }
        });
    }

    protected void setYesNoLayout() {
        yesNoLayout = findViewById(R.id.yesNoLayoutId);
        Log.d("DEBUG BaseActivity", "setExitLayout: " + yesNoLayout);
        TextView question = findViewById(R.id.questionTextId);
        if (question != null)
            question.setText("Exit Game?");
        setNoButton();
        setYesButton();
    }

    protected void displayExitLayout() {
        Log.d("DEBUG BaseActivity", "displayExitLayout: ");
        if (yesNoLayout != null && yesNoLayout.getVisibility() == View.GONE) {
            yesNoLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setYesButton() {
        Button yesButton = (Button) findViewById(R.id.yesButtonId);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG BaseActivity", "onClick: yesButton");
                exit();
            }
        });
    } //the button that appears when the exit button is clicked

    private void setNoButton() {
        Button noButton = (Button) findViewById(R.id.noButtonId);
        try {
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG BaseActivity", "onClick: noButton");
                    yesNoLayout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.d("DEBUG BaseActivity", "setNoButton failed : " + e);
        }
    } //the button that appears when the exit button is clicked


    protected void exit() {
        Log.d("myDEBUG BaseActivity", "exit  ");
        finishAffinity();
        System.exit(0);
    }


    /*
     *  Initialize the music toggle button
     * */
    protected void initializeMusicToggleButton() {
        toggleMusicBtn = findViewById(R.id.toggleMusicBtnId);
        boolean isMusicMuted = prefs.isMusicMuted();
        Log.d("DEBUG BaseActivity", "initializeMusicToggleButton: is music muted " + isMusicMuted);
        prefs.setIsMusicMuted(isMusicMuted); //because we want the music to turn on by default
    }


    /*
     * Initialize the sounds toggle button
     * */
    protected void initializeSoundsToggleButton() {
        toggleSoundsBtn = findViewById(R.id.toggleSoundsBtnId);
        Log.d("DEBUG BaseActivity", "initializeSoundsToggleButton: " + toggleSoundsBtn);
        boolean isSoundsMuted = prefs.isSoundsMuted();
    }


    /*
     *  navigates to the menu activity
     * */
    public void goToMenuActivity(User currentPlayer, Boolean shouldReplaceMusic) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("currentPlayer", currentPlayer);
        if (shouldReplaceMusic) {
            intent.putExtra("shouldResumeMusic", true);
        }
        this.startActivity(intent);
    }


    @Override
    public void onSwipeLeft() {
        displayExitLayout();
    }

    @Override
    public void onSwipeRight() {
        displayExitLayout();
    }


    @Override
    public void onBackPressed() {
        if (allowBackNavigation) {
            super.onBackPressed();
        } else {
            displayExitLayout();
        }
    }
}



