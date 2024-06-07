package com.clientapp.battleshipclient.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.data.CurrentStats;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.logic.LeaderboardAndStatsLogic;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.fragments.LeaderBoardFragment;
import com.clientapp.battleshipclient.view.fragments.StatsFragment;

import java.util.ArrayList;

public class LeaderboardAndStatsActivity extends BaseActivity {

    private User currPlayer;
    private ArrayList<User> topUsersList = new ArrayList<>();
    private LeaderboardAndStatsLogic leaderBoardAndStatsLogic;
    CurrentStats currentStats ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard_stats);

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
            leaderBoardAndStatsLogic.getTopUsers(new LeaderboardAndStatsLogic.DataCallback(){
                @Override
                public void onDataRetrieved(ArrayList<User> topUsersList, CurrentStats currentStats) {
                    setUsersData( topUsersList, currentStats);
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







    public void setUsersData(ArrayList<User> topUsers, CurrentStats currentStats) {
        this.topUsersList = topUsers;
        this.currentStats = currentStats;
        Log.d("DEBUG LeaderboardAndStatsActivity", "setUsersData: " + topUsers.toString());
    }





    private void setLeaderBoardButton() {
        Button leaderBoardBtn = findViewById(R.id.LeaderBoardBtnId);
        leaderBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(LeaderboardAndStatsActivity.this, AudioEnum.BUTTON);
                displayLeaderBoardFragment();
            }
        });
    }

    private void setMyStatsButton() {
        Button statsBtn = findViewById(R.id.statsBtnId);
        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioUtils.makeSound(LeaderboardAndStatsActivity.this, AudioEnum.BUTTON);
                displayMyStatsFragment(currentStats);
            }
        });
    }


    private void displayLeaderBoardFragment() {
        LeaderBoardFragment leaderBoardFragment = LeaderBoardFragment.newInstance(new ArrayList<>(topUsersList));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerId, leaderBoardFragment).addToBackStack(null).commit();
    }

    private void displayMyStatsFragment(CurrentStats currentStats) {
        StatsFragment statsFragment = StatsFragment.newInstance( currentStats); //TODO
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerId, statsFragment).addToBackStack(null).commit();
    }








    /*
     *  This method sets the onClickListener for the X button in the LeaderboardAndStatsActivity
     *  When the X button is clicked, the sound_button sound is played and the current activity is finished
     * */
    @Override
    protected void setXbutton() {
        ImageButton xbutton = (ImageButton) findViewById(R.id.XbuttonId);
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


    public void onPause() {
        super.onPause();
        AudioUtils.pauseMusic(this);
    }

    public void onResume() {
        super.onResume();
        AudioUtils.resumeMusicState(LeaderboardAndStatsActivity.this); // mute the music
    }


}