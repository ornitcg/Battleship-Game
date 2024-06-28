package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.logic.MenuLogic;
import com.clientapp.battleshipclient.logic.SignLogic;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;

/*
 * This class is the activity for the Options screen
 * contains the buttons for the user to choose from
 * and the logic for the buttons
 */
public class MenuActivity extends BaseActivity {
    private static final long WAITING_VIEW_TIMEOUT_MILLIS = 40000; // 1 minute
    private MenuLogic menuLogic;
    private LinearLayout waitingView;
    private final Handler cancelMatchRequestTimeoutHandler = new Handler();
    private Runnable cancelRequestTimeoutRunnable;
    private Button startPlayingButton;
    private Button leaderBoardButton;
    private Button instructionsButton;
    private Button quitButton;
    private Button signOutButton;


    /*
     *  This method is called when the activity is created
     *  and sets the view and the buttons
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = this.getIntent();
        setUserFromIntent(intent);
        String currentPlayerId = getCurrentPlayer().getId();
        Log.d("myDEBUG MenuActivity", "onCreate: " + currentPlayerId);
        //log all properties of user
        Log.d("myDEBUG MenuActivity", "onCreate: " + getCurrentPlayer().toString());

        menuLogic = new MenuLogic(this, getCurrentPlayer());
        setWaitingView();
        setButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /*
     * Override method from BaseActivity
     * in addition disables the X button since it is not needed in this screen
     */
    @Override
    protected void initView() {
        super.initView();
        getXActivityBtn().setVisibility(View.GONE);
    }


    /*
     * This method sets the view showing on waiting for opponent match
     * and sets the cancel button listener
     */
    private void setWaitingView() {
        waitingView = findViewById(R.id.waitingForGameId);
        Button cancelStartPlayingButton = findViewById(R.id.CancelBtnId);
        setCancelButtonListener(cancelStartPlayingButton);
    }


    /*
     * This method calls for methods that set the
     * buttons for the user to choose from
     */
    private void setButtons() {
        setStartPlayingButton();
        setWatchLeaderBoardButton();
        setInstructionsButton();
        setSignOutButton();
        setQuitButton();
    }


    /*
     *  This method sets the listener for the sign out button
     *  and calls for the method that signs out the user
     * */
    private void setSignOutButton() {
        signOutButton = findViewById(R.id.signOutBtnId);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(MenuActivity.this, AudioEnum.BUTTON);
                signOut();
            }
        });
    }


    /*
     *  This method is called when the user wants to sign out
     *  and navigates to the sign activity
     * */
    private void signOut() {
        SignLogic.keepUserHandler.removeCallbacks(SignLogic.keepUserAlivetask);
        goToSignActivity();
    }


    /*
     *  This method navigates to the sign activity
     *  and finishes the current activity
     * */
    private void goToSignActivity() {
        Intent intent = new Intent(MenuActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }


    /*
     *  This method disables the clickable buttons
     *  when the user is waiting for an opponent match
     * */
    public void disableClickableButtons() {
        startPlayingButton.setClickable(false);
        leaderBoardButton.setClickable(false);
        instructionsButton.setClickable(false);
        signOutButton.setClickable(false);
        quitButton.setClickable(false);
    }


    /*
     *  This method enables the clickable buttons
     * by resetting the buttons onclick listeners
     * */
    private void setWatchLeaderBoardButton() {
        leaderBoardButton = findViewById(R.id.goToLeaderboardBtnId);
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer sound = MediaPlayer.create(MenuActivity.this, R.raw.sound_button);
                AudioUtils.playSound(MenuActivity.this, sound);
                goToLeaderBoard(v);
            }
        });
    }


    /*
     * This method sets the listener for the start playing button
     * and calls for the method that starts search for an opponent match
     */
    private void setStartPlayingButton() {
        startPlayingButton = findViewById(R.id.startPlayingBtnId);
        startPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(MenuActivity.this, AudioEnum.BUTTON);
                startPlayingButton.setClickable(false);
                Log.d("myDEBUG MenuLogic", "onClick: startPlayingButton");
                displayWaitingForOpponent();
                disableClickableButtons();
                menuLogic.startPlaying();
            }
        });
    }


    /*
     * This method cancels the runnable that was set
     * for the waiting view
     */
    public void cancelRunnable() {
        cancelMatchRequestTimeoutHandler.removeCallbacks(cancelRequestTimeoutRunnable);
    }


    /*
     * This method sets the listener for the instructions button
     * and calls for the method that displays the game instructions
     */
    private void setInstructionsButton() {
        instructionsButton = findViewById(R.id.instructionsBtnId);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(MenuActivity.this, AudioEnum.BUTTON);
                Intent intent = new Intent(MenuActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });
    }


    /*
     * This method sets the listener for the quit button
     * and calls for the method that displays the exit layout
     */
    private void setQuitButton() {
        quitButton = findViewById(R.id.quitBtnId);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer sound = MediaPlayer.create(MenuActivity.this, R.raw.sound_button);
                AudioUtils.playSound(MenuActivity.this, sound);
                displayExitLayout();
            }
        });
    }


    /*
     * This method sets the listener for the cancel button
     * and calls for the method that cancels the requests
     */
    private void setCancelButtonListener(Button cancelPlayingButton) {
        cancelPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLogic.cancelCreateGame(); //cancels the requests on client and server
                viewChangeOnRequestCanceled();
            }
        });
    }


    /*
     * This method is called when the request is canceled
     * and hides the waiting view
     */
    public void viewChangeOnRequestCanceled() {//TODO change this method
        waitingView.setVisibility(View.GONE);
        setButtons();
        cancelRunnable();
    }


    /*
     * This method is called when the an opponent match was found
     * and starts the placement activity
     */
    public void goToPlacementActivity(String gameId) {
        Intent intent = new Intent(MenuActivity.this, PlacementActivity.class);
        intent.putExtra("currentPlayer", getCurrentPlayer());
        intent.putExtra("gameId", gameId);
        Log.d("myDEBUG MenuActivity", "goToPlacementActivity: " + gameId);
        startActivity(intent);
        finishAffinity();
    }


    /*
     *  navigates to the leaderboard
     * */
    public void goToLeaderBoard(View v) {
        Intent intent = new Intent(MenuActivity.this, LeaderboardAndStatsActivity.class);
        intent.putExtra("currentPlayer", getCurrentPlayer());
        startActivity(intent);
    }


    /*
     * This method is called when the user wants start a game
     * and displays the waiting view
     */
    public void displayWaitingForOpponent() {
        waitingView.setVisibility(View.VISIBLE);
        TextView waitingText = findViewById(R.id.waitingTextViewId);
        waitingText.setText(ClientMessages.WAITING_FOR_OPPONENT);
        Log.d("myDEBUG MenuActivity", "displayWaitingForOpponent: ");
        //setRunnable
        cancelRequestTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                viewChangeOnRequestCanceled();
                menuLogic.cancelCreateGame();
                displayNoMatchFound();
            }
        };
        //run the Runnable after WAITING_VIEW_TIMEOUT_MILLIS
        cancelMatchRequestTimeoutHandler.postDelayed(cancelRequestTimeoutRunnable, WAITING_VIEW_TIMEOUT_MILLIS);

    }


    /*
     *  This method is called when no match was found
     * */
    private void displayNoMatchFound() { // TODO make this a util method
        Log.d("myDEBUG MenuActivity", "displayNoMatchFound: ");
        waitingView.setVisibility(View.VISIBLE);
        TextView waitingText = findViewById(R.id.waitingTextViewId);
        waitingText.setText("NO MATCH FOUND!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                waitingView.setVisibility(View.GONE);
            }
        }, 2000);

    }


    /*
     *  This method is called when the activity is stopped
     * */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
    *  This method is called when the activity is paused
    * */
    @Override
    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    /*
    *  This method is called when the activity is resumed
    * */
    @Override
    public void onResume() {
        super.onResume();
        waitingView.setVisibility(View.GONE);
        setButtons();
        if (AudioUtils.currentMusic != AudioEnum.LOBBY_MUSIC)
            replaceMusic(AudioEnum.LOBBY_MUSIC);
        AudioUtils.resumeMusicState(MenuActivity.this); // mute the music
    }


}//