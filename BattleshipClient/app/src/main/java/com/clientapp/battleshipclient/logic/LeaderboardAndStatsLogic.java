package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.util.Log;

import com.clientapp.battleshipclient.model.CurrentStats;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.networking.UserNW;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;


/*
*  This class is responsible for the leaderboard and stats logic
* It sends requests to the server to get the top users scores and the current player stats
* It also contains the current player stats and the top users list
* */
public class LeaderboardAndStatsLogic {
    private final User currPlayer;
    private final Context context;

    @Getter
    private ArrayList<User> topUsersList = new ArrayList<>();
    private CurrentStats currentStats;


    /*
    *  Constructor for the LeaderboardAndStatsLogic class
    *  @param context the context of the activity
    *  @param currPlayer the current player
    * */
    public LeaderboardAndStatsLogic(Context context, User currPlayer) {
        this.currPlayer = currPlayer;
        this.context = context;
    }

    /*
    *  Sends a request to the server to get the top 10 users scores
    *  @param dataCallback the callback to be called after the request is done
    * */
    public void getTopUsers(DataCallback dataCallback) {
        UserNW.getScores(context, currPlayer, new ICallbacks<JSONObject>() {
            @Override
            public void onResponseSuccess(JSONObject response) {
                Log.d("myDEBUG LeaderboardAndStatsLogic", "getTopUsers whole Response: " + response);
                try {
                    JSONObject value = response.getJSONObject("value");
                    Log.d("myDEBUG LeaderboardAndStatsLogic", "getTopUsers Response value: " + value);
                    JSONArray topUsers = value.getJSONArray("bestScoreUsers");
                    Log.d("myDEBUG LeaderboardAndStatsLogic", "getTopUsers Response topUsers: " + topUsers);
                    int rank = value.getInt("rank");
                    int userBestScore = value.getInt("userBestScore");
                    int numWins = value.getInt("numWins");
                    int totalGames = value.getInt("totalGames");

                    populateList(topUsers);

                    currentStats = new CurrentStats( currPlayer , rank, userBestScore, numWins, totalGames);
                    Log.d("myDEBUG LeaderboardAndStatsLogic", "getTopUsers Response: " + topUsersList.toString());
                    dataCallback.onDataRetrieved(topUsersList, currentStats);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                Log.d("myDEBUG LeaderboardAndStatsLogic", "getTopUsers Error: " + e);
            }

        });

    }


    /*
    *  Populates the top users list with the users from the JSONArray
    *  @param topUsers the JSONArray of the top users
    * */
    private void populateList(JSONArray topUsers) {
        Log.d("myDEBUG LeaderboardAndStatsLogic", "populateList: " + topUsers.toString());
        //loop on the array and add each user to the list
        try {
            // Loop over the JSONArray
            for (int i = 0; i < topUsers.length(); i++) {
                JSONObject userObject = topUsers.getJSONObject(i);
                Log.d("myDEBUG LeaderboardAndStatsLogic", "userObject: " + userObject.toString());
                String id = userObject.getString("id");
                String name = userObject.getString("name");
                int score = userObject.getInt("bestScore");
                Log.d("myDEBUG LeaderboardAndStatsLogic", "userObject in details: " + id + " " + name + " " + score);
                topUsersList.add(new User(id, name, "", score));
            }
            Log.d("myDEBUG LeaderboardAndStatsLogic", "populateList: " + topUsersList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    *  Callback interface for the data retrieval
    * It contains the methods to be called after the data is retrieved or an error occurred
    * */
    public interface DataCallback {
        void onDataRetrieved(ArrayList<User> topUsersList, CurrentStats currentStats);
        void onError(Exception e);
    }
}
