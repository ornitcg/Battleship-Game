package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.model.CurrentStats;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.logic.LeaderboardAndStatsLogic;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.fragments.LeaderBoardFragment;
import com.clientapp.battleshipclient.view.fragments.StatsFragment;

import java.util.ArrayList;


/*
 *  This class represents the LeaderboardAndStatsActivity of the application
 *  It is the activity that displays the leaderboard and the stats of the player
 *  It is launched when the leaderboard button is clicked in the lobby
 */
public class LeaderboardAndStatsActivity extends BaseActivity {

    private User currPlayer;
    private ArrayList<User> topUsersList = new ArrayList<>();
    private LeaderboardAndStatsLogic leaderBoardAndStatsLogic;
    CurrentStats currentStats;
    Button leaderBoardBtn;
    Button statsBtn;


    /*
     *  Overrides the onCreate method from the BaseActivity class
     *  This method is called when the activity is first created
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard_stats);

        initiallizeButtons();


        Intent intent = this.getIntent();
        currPlayer = (User) intent.getSerializableExtra("currentPlayer");
        leaderBoardAndStatsLogic = new LeaderboardAndStatsLogic(this, currPlayer);
        replaceMusic(AudioEnum.LEADERBOARD_MUSIC);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTag), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setLeaderBoardButton();
        setMyStatsButton();

        if (savedInstanceState == null) {
            leaderBoardAndStatsLogic.getTopUsers(new LeaderboardAndStatsLogic.DataCallback() {
                @Override
                public void onDataRetrieved(ArrayList<User> topUsersList, CurrentStats currentStats) {
                    setUsersData(topUsersList, currentStats);
                    displayLeaderBoardFragment();
                }

                @Override
                public void onError(Exception e) {
                    Log.d("DEBUG LeaderboardAndStatsActivity", "onError: " + e.getMessage());

                }
            });

            displayLeaderBoardFragment();
        }
    }


    /*
    *  This method initializes the buttons in the LeaderboardAndStatsActivity
    *  It sets the Leaderboard button as selected and the My Stats button as not selected
    * */
    private void initiallizeButtons() {
        leaderBoardBtn = findViewById(R.id.LeaderBoardBtnId);
        statsBtn = findViewById(R.id.statsBtnId);
        leaderBoardBtn.setBackgroundResource(R.drawable.button_fragment_selected);
        statsBtn.setBackgroundResource(R.drawable.button_fragment);
    }

    /*
     *  This method sets the data of the users in the LeaderboardAndStatsActivity
     *  It is called when the data is retrieved from the server
     *  It sets the top users list and the current stats of the player
     *  It logs the top users list
     * */
    public void setUsersData(@NonNull ArrayList<User> topUsers, @NonNull CurrentStats currentStats) {
        this.topUsersList = topUsers;
        this.currentStats = currentStats;
        Log.d("DEBUG LeaderboardAndStatsActivity", "setUsersData: " + topUsers.toString());
    }

    /*
     *  This method sets the onClickListener for the Leaderboard button in the LeaderboardAndStatsActivity
     *  When the Leaderboard button is clicked, the sound_button sound is played and the LeaderboardFragment is displayed
     *  The Leaderboard button is highlighted
     * */
    private void setLeaderBoardButton() {
        leaderBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(LeaderboardAndStatsActivity.this, AudioEnum.BUTTON);
                //change button background
                leaderBoardBtn.setBackgroundResource(R.drawable.button_fragment_selected);
                statsBtn.setBackgroundResource(R.drawable.button_fragment);
                displayLeaderBoardFragment();
            }
        });
    }


    /*
     *  This method sets the onClickListener for the My Stats button in the LeaderboardAndStatsActivity
     *  When the My Stats button is clicked, the sound_button sound is played and the StatsFragment is displayed
     *  The My Stats button is highlighted
     * */
    private void setMyStatsButton() {
        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(LeaderboardAndStatsActivity.this, AudioEnum.BUTTON);
                statsBtn.setBackgroundResource(R.drawable.button_fragment_selected);
                leaderBoardBtn.setBackgroundResource(R.drawable.button_fragment);
                displayMyStatsFragment(currentStats);
            }
        });
    }

    /*
     *  This method displays the LeaderboardFragment in the LeaderboardAndStatsActivity
     *  It is called when the Leaderboard button is clicked
     *  The LeaderboardFragment is created with the top users list and displayed in the fragment container
     * */
    private void displayLeaderBoardFragment() {
        LeaderBoardFragment leaderBoardFragment = LeaderBoardFragment.newInstance(new ArrayList<>(topUsersList));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerId, leaderBoardFragment).addToBackStack(null).commit();
    }


    /*
     *  This method displays the StatsFragment in the LeaderboardAndStatsActivity
     *  It is called when the My Stats button is clicked
     *  The StatsFragment is created with the current stats of the player and displayed in the fragment container
     * */
    private void displayMyStatsFragment(CurrentStats currentStats) {
        StatsFragment statsFragment = StatsFragment.newInstance(currentStats);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerId, statsFragment).addToBackStack(null).commit();
    }


    /*
     *  This method sets the onClickListener for the X button in the LeaderboardAndStatsActivity
     *  When the X button is clicked, the sound_button sound is played and the current activity is finished
     * */
    @Override
    protected void setActivityXbutton() {
        ImageButton xbutton = (ImageButton) findViewById(R.id.XActivityButtonId);
        xbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG BaseActivity", "onClick: exitBtn");
                AudioUtils.makeSound(LeaderboardAndStatsActivity.this, AudioEnum.BUTTON);
                //finish current activity
                finish();
            }
        });
    }


    /* Overrides the setYesNoLayout method from the BaseActivity class
     * This method sets nothing for the layout of the YesNoLayout that does not exist in the InstructionsActivity
     * */
    @Override
    protected void setYesNoLayout() {
        //do nothing
    }

    /*
     * Override the onPause method
     * This method is called when the activity is visible to the user
     * */
    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }


    /*
     *  Override the onResume method
     *  This method is called when the activity is resumed
     * */
    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(LeaderboardAndStatsActivity.this); // mute the music
    }


}