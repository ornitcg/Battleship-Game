package com.clientapp.battleshipclient.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.model.CurrentStats;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;
import com.clientapp.battleshipclient.view.view_utils.ExtrasEnum;


/*
 *  This class represents the Stats Fragment
 * It is used to display the user's statistics
 * It is used to display the user's rank, number of games played, number of games won and the user's best score
 * */
public class StatsFragment extends Fragment {

    private CurrentStats currentStats;


    /*
     *  This method is called to create the view
     *  It is called after the onCreateView method
     * */
    public static StatsFragment newInstance(CurrentStats currenStats) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ExtrasEnum.CURRENT_STATS.getName(), currenStats);
        fragment.setArguments(args);
        return fragment;
    }


    /*
     *  This method is called to create the view
     *  It is called after the newInstance method
     * */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false); // Replace with your fragment layout

        if (getArguments() != null) {
            currentStats = (CurrentStats) getArguments().getSerializable(ExtrasEnum.CURRENT_STATS.getName());
            Log.d("myDEBUG StatsFragment", "onCreateView: " + currentStats.toString());
        }
        if (currentStats == null) {
            currentStats = new CurrentStats(null, 0, 0, 0, 0); // Initialize to avoid null pointer exception
        }
        displayName(view);
        fillCardViews(view);
        return view;
    }


    /*
     *  This method is called to display the user's name
     *  It is called in the onCreateView method
     * */
    private void displayName(View view) {
        TextView name = view.findViewById(R.id.myNameId);
        name.setText(currentStats.getUser().getName());
    }


    /*
     *  Creates and Add a new CardView for each item in the list
     * */
    private void fillCardViews(View view) {
        if (currentStats == null) {
            return;
        }
        CardView myRank = view.findViewById(R.id.myRankId);
        CardView gamesPlayed = view.findViewById(R.id.gamesPlayedId);
        CardView gamesWon = view.findViewById(R.id.gamesWonId);
        CardView myScore = view.findViewById(R.id.myScoreId);

        ((TextView) myRank.findViewById(R.id.statId)).setText(ClientMessages.RANK);
        ((TextView) myRank.findViewById(R.id.scoreId)).setText(currentStats.getRank() + "");
        ((ImageView) myRank.findViewById(R.id.imageRightId)).setImageResource(R.drawable.icon_leaderboard_rank);
        ((ImageView) myRank.findViewById(R.id.imageLeftId)).setImageResource(R.drawable.icon_leaderboard_rank);


        ((TextView) gamesPlayed.findViewById(R.id.statId)).setText(ClientMessages.GAMES_PLAYED);
        ((TextView) gamesPlayed.findViewById(R.id.scoreId)).setText(currentStats.getNumGames() + "");
        ((ImageView) gamesPlayed.findViewById(R.id.imageRightId)).setImageResource(R.drawable.icon_game);
        ((ImageView) gamesPlayed.findViewById(R.id.imageLeftId)).setImageResource(R.drawable.icon_game);


        ((TextView) gamesWon.findViewById(R.id.statId)).setText(ClientMessages.GAMES_WON);
        ((TextView) gamesWon.findViewById(R.id.scoreId)).setText(currentStats.getNumWins() + "");
        ((ImageView) gamesWon.findViewById(R.id.imageRightId)).setImageResource(R.drawable.icon_trophy);
        ((ImageView) gamesWon.findViewById(R.id.imageLeftId)).setImageResource(R.drawable.icon_trophy);


        ((TextView) myScore.findViewById(R.id.statId)).setText(ClientMessages.SCORE_STAT);
        ((TextView) myScore.findViewById(R.id.scoreId)).setText(currentStats.getBestScore() + "");
        ((ImageView) myScore.findViewById(R.id.imageRightId)).setImageResource(R.drawable.icon_leaderboard_medal);
        ((ImageView) myScore.findViewById(R.id.imageLeftId)).setImageResource(R.drawable.icon_leaderboard_medal);

    }
}
