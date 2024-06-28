package com.clientapp.battleshipclient.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.Services.PlaybackService;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.utils.PreferencesManager;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;
import com.clientapp.battleshipclient.view.view_utils.ExtrasEnum;
import com.clientapp.battleshipclient.view.view_utils.SwipeGestureListener;

import lombok.Data;



/*
*
* This class represents the base activity of the application
* It is the parent class of all the activities in the application
* It contains the reusable views and methods that are used in the extending activities
*/
@Data
public class BaseActivity extends AppCompatActivity implements SwipeGestureListener {
    private ImageButton toggleMusicBtn;
    private ImageButton toggleSoundsBtn;
    private ImageButton xActivityBtn;
    private View yesNoLayout = null;
    protected PreferencesManager prefs;
    protected User currentPlayer;
    protected boolean brutalDestroy = true;
    protected boolean allowBackNavigation = false;


    /*
    *  Overrides the onCreate method to initialize the preferences manager
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this); // enable edge to edge screen display
        prefs = PreferencesManager.getInstance(this);
    }


    /*
    *  Overrides the onStart method to initialize the views
    * */
    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    /*
    *  Initializes the reusable views used in the extending activities
    * */
    protected void initView() { // CAN OVERRIDE THIS TO INITIALIZE CHILD VIEWS
        initializeMusicToggleButton();  //call methods from base activity
        initializeSoundsToggleButton();  //call methods from base activity
        setToggleSoundsButtonView(); //call methods from base activity
        setToggleMusicButtonView(); //call methods from base activity
        setSoundsToggleButtonClickListener(this); //call methods from base activity
        setMusicToggleButtonClickListener(this); //call methods from base activity
        setYesNoLayout();
        setActivityXbutton();

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.baseActivityMainId), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }


    /*
     *  Sets the current player field from the intent
     *  @param intent the intent that started the activity
     */
    public void setUserFromIntent(Intent intent) {
        if (intent != null) {
            setCurrentPlayer((User) intent.getSerializableExtra(ExtrasEnum.CURRENT_PLAYER.getName()));
            Log.d("DEBUG BaseActivity", "setUserFromIntent: " + getCurrentPlayer().getId() + " " + getCurrentPlayer().getName());
        } else Log.d("DEBUG BaseActivity", "setUserFromIntent: intent is null");
    }


    /*
     *  Sets the image for the button that toggles the sounds on and off
     * */
    protected void setToggleSoundsButtonView() {
        if (prefs.isSoundsMuted()) {
            toggleSoundsBtn.setImageResource(R.drawable.icon_sounds_off);
        } else {
            toggleSoundsBtn.setImageResource(R.drawable.icon_sounds_on);
        }
    }


    /*
     *  Sets the image for the button that toggles the music on and off
     * */
    protected void setToggleMusicButtonView() {
        if (prefs.isMusicMuted()) {
            toggleMusicBtn.setImageResource(R.drawable.icon_music_off);
        } else {
            toggleMusicBtn.setImageResource(R.drawable.icon_music_on);
        }
    }


    /*
     *  Sets click listener for the button that toggles the *music* on and off
     *
     * */
    protected void setMusicToggleButtonClickListener(Context context) {
        toggleMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG BaseActivity", "onClick: toggleMusicBtn");
                AudioUtils.toggleMusic(context); // mute the music
                setToggleMusicButtonView();
            }
        });
    }


    /*
     *  Sets click listener for the button that toggles the *sounds* on and off
     * */
    protected void setSoundsToggleButtonClickListener(Context context) {
        toggleSoundsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log clicked
                Log.d("DEBUG BaseActivity", "onClick: toggleSoundsBtn");
                AudioUtils.toggleSounds(context); // mute the sounds
                setToggleSoundsButtonView();
            }
        });
    }


    /*
    *  Starts the music service
    *  @param musicEnvironment the music to be played
    * */
    protected void setMusicService(AudioEnum musicEnvironment) {
        Intent intent = new Intent(this, PlaybackService.class);
        intent.putExtra(ExtrasEnum.MUSIC_NAME.getName(), musicEnvironment);
        startService(intent);  //call for playback to play music
    }


    /*
    *  Replaces the music service
    * @param audioEnum the music to be played
    * */
    protected void replaceMusic(AudioEnum audioEnum) {
        setMusicService(audioEnum);
    }


    /*
    *  Sets the exit button for the activity
    * */
    protected void setActivityXbutton() {
        xActivityBtn = findViewById(R.id.XActivityButtonId);
        if (xActivityBtn != null) {
            xActivityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG BaseActivity", "onClick: exitBtn");
                    displayExitLayout();
                    AudioUtils.makeSound(BaseActivity.this, AudioEnum.BUTTON);
                }
            });
        } else
            Log.d("DEBUG BaseActivity", "setActivityXbutton: xActivityBtn is null");
    }


    /*
    *  Sets the layout that displays the exit question
    * */
    protected void setYesNoLayout() {
        yesNoLayout = findViewById(R.id.yesNoLayoutId);
        if (yesNoLayout != null) {
            Log.d("DEBUG BaseActivity", "setExitLayout: " + yesNoLayout);
            TextView question = findViewById(R.id.questionTextId);
            if (question != null)
                question.setText(ClientMessages.EXIT_QUESTION);
            setNoButton();
            setYesButton();
        }
    }


    /*
    *  Displays the exit layout
    * */
    protected void displayExitLayout() {
        Log.d("DEBUG BaseActivity", "displayExitLayout: ");
        if (yesNoLayout != null && yesNoLayout.getVisibility() == View.GONE) {
            yesNoLayout.setVisibility(View.VISIBLE);
        }
    }


    /*
    *  Sets the yes button for the exit layout
    * */
    private void setYesButton() {
        Button yesButton = findViewById(R.id.yesButtonId);
        if (yesButton != null) {
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG BaseActivity", "onClick: yesButton");
                    exit();
                }
            });
        }
    } //the button that appears when the exit button is clicked


    /*
    *  Sets the no button for the exit layout
    * */
    private void setNoButton() {
        Button noButton = findViewById(R.id.noButtonId);
        if (noButton != null) {
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG BaseActivity", "onClick: noButton");
                    yesNoLayout.setVisibility(View.GONE);
                }
            });
        } else
            Log.d("DEBUG BaseActivity", "setNoButton failed");
    } //the button that appears when the exit button is clicked



    /*
    * Exits the activity
    * Stops the music service on exit
    * this is a general exit method to be overridden by child activities
    * if necessary
    * */
    protected void exit() {
        Log.d("myDEBUG BaseActivity", "exit  ");
        finishAffinity();
        stopService(new Intent(this, PlaybackService.class));
        Log.d("myDEBUG BaseActivity", "stops background music service");
        System.exit(0);
    }


    /*
     *  Initialize the music toggle button
     * */
    protected void initializeMusicToggleButton() {
        toggleMusicBtn = findViewById(R.id.toggleMusicBtnId);
        if (toggleMusicBtn != null) {
            boolean isMusicMuted = prefs.isMusicMuted();
            Log.d("DEBUG BaseActivity", "initializeMusicToggleButton: is music muted " + isMusicMuted);
            prefs.setIsMusicMuted(isMusicMuted); //because we want the music to turn on by default
        } else Log.d("DEBUG BaseActivity", "initializeMusicToggleButton: toggleMusicBtn is null");


    }


    /*
     * Initialize the sounds toggle button
     * */
    protected void initializeSoundsToggleButton() {
        toggleSoundsBtn = findViewById(R.id.toggleSoundsBtnId);
        if (toggleSoundsBtn != null) {
            Log.d("DEBUG BaseActivity", "initializeSoundsToggleButton: " + toggleSoundsBtn);
            boolean isSoundsMuted = prefs.isSoundsMuted(); //TODO: check if this is necessary
        }
    }


    /*
     *  navigates to the menu activity
     * */
    public void goToMenuActivity(User currentPlayer, Boolean shouldReplaceMusic) {
        Intent intent = new Intent(this, MenuActivity.class);
        if (intent != null) {
            intent.putExtra(ExtrasEnum.CURRENT_PLAYER.getName(), currentPlayer);
            if (shouldReplaceMusic) {
                intent.putExtra(ExtrasEnum.SHOULD_RESUME_MUSIC.getName(), true);
            }
            this.startActivity(intent);
        } else Log.d("DEBUG BaseActivity", "goToMenuActivity: intent is null");


    }

    /*
    *  Overrides the onSwipeLeft method
    * */
    @Override
    public void onSwipeLeft() {
        displayExitLayout();
    }

    /*
    *  Overrides the onSwipeRight method
    * */
    @Override
    public void onSwipeRight() {
        displayExitLayout();
    }


    /*
    *  Overrides the onBackPressed method
    *  If the back navigation is allowed, the activity is finished
    *  If the back navigation is not allowed, the exit layout is displayed
    * */
    @Override
    public void onBackPressed() {
        if (allowBackNavigation) {
            super.onBackPressed();
        } else {
            displayExitLayout();
        }
    }
}



