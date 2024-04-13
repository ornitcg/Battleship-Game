package com.clientapp.battleshipclient.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clientapp.battleshipclient.logic.GameBoard;
import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.logic.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class GameNW {

    private GameBoard currPlayerBoard;
    private GameBoard opponentBoard;
    private String opponentID;
    private String currPlayerId;

    private String postCreateBoardEndpoint = "http://localhost:8080/createBoard"; // where i send a board to the server, when user clicks on "I'm ready" button


    public GameNW(String opponentID) {
        this.opponentID = opponentID;
        this.currPlayerId = currPlayerId;
    }

    public void setBoard(String currPlayerId, ArrayList<Tile> tilesList) {
        // get the board of user from server
    }
    // get the board of the current player from server
    // get the board of the opponent from server
    // send the move to the server
    // get the result of the move from the server

    public void getBoard(User user) {
        // get the board of user from server
    }

    public void sendMove(User user, int position) {
        // send the move to the server
    }

    public void getMoveResult(User user) {
        // get the result of the move from the server
    }

    public void postCreateBoard(String userId, ArrayList<Tile> tilesList) {
        // connect to server and  send user name and password that were approved
        // analyse server response for success/failure
    }

    public void sendTilesDataToServer(Context context, String userId, ArrayList<Tile> tilesList) {
        Netcom netcom = Netcom.getInstance( context);
        String url = "http://localhost:8080/createBoard"; // Your server endpoint

        Gson gson = new Gson();
        String gameBoardJSON = gson.toJson(tilesList);

        try {
            // Assuming SerializeToJSON() properly converts the gameBoard to a JSON string
            JSONObject jsonObject = new JSONObject(gameBoardJSON);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Handle successful response
                            Log.d("Netcom", "Board sent to server successfully");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Netcom", "Error onErrorResponse: " + error.getMessage());
                    // Handle error
                }
            });

            // Add the request to the RequestQueue.
            netcom.getRequestQueue().add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("Netcom", "Error on catch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void attackPosition(){
        //send position to server and get the result

    }



}
