package com.clientapp.battleshipclient.logic;

import android.util.Log;

import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;
import com.clientapp.battleshipclient.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/* This class is responsible for creating JSON objects to be sent to the server
 * It contains methods to create JSON objects for the game board, the ships and the sign in request
 */

public class JsonHelper {


    /*
     *  This method creates the whole gameboard in JSON format
     *  to be sent to the server
     * */
    public static JSONObject createJsonGameBoard(GameBoard gameBoard) {
        JSONObject gameBoardJson = new JSONObject();
        JSONArray ships = createShipsJson(gameBoard);
        JSONArray board = createBoardJson(gameBoard);
        try {
            gameBoardJson.put("userId", gameBoard.getUser().getId());
            gameBoardJson.put("gameId", gameBoard.getGameId());
            gameBoardJson.put("ships", ships);
            gameBoardJson.put("board", board);
        } catch (JSONException e) {
            Log.e("myDEBUG GameLogic", "createGameBoardJson: " + e);
        }
        return gameBoardJson;
    }


    /*
     *  This method creates the board in JSON format
     * */
    private static JSONArray createBoardJson(GameBoard gameBoard) {
        JSONArray board = new JSONArray();
        for (Tile tile : gameBoard.getBoard()) {
            JSONObject tileJson = new JSONObject();
            try {
                tileJson.put("position", tile.getPosition());
                tileJson.put("state", tile.getState().getName());
                tileJson.put("shipId", tile.getShipId());
                board.put(tileJson);
            } catch (JSONException e) {
                Log.e("myDEBUG GameLogic", "createBoardJson: " + e);
            }
        }
        return board;
    }


    /*
     *  This method creates the ships array part in JSON format
     * */
    private static JSONArray createShipsJson(GameBoard gameBoard) {
        JSONArray ships = new JSONArray();
        for (Ship ship : gameBoard.getShips()) {
            JSONObject shipJson = new JSONObject();
            try {
                shipJson.put("id", ship.getId());
                shipJson.put("position", ship.getEdgePosition());
                shipJson.put("type", ship.getType().getName());
                shipJson.put("orientation", ship.getOrientation().getName());
                shipJson.put("size", ship.getSize());
                ships.put(shipJson);
            } catch (JSONException e) {
                Log.e("myDEBUG GameLogic", "createShipsJson: " + e);
            }
        }
        return ships;
    }


    /*
     *  Create JSON object for sign in request
     * */
    public static JSONObject createSignJson(User user) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", user.getName());
            postData.put("password", user.getPassword());
        } catch (Exception error) {
            Log.e("DEBUG", "Error creating JSON for signIn", error);
        }
        return postData;
    }


    /*
    *  Extract the board from the response
    *  @param response: the response from the server
    * */
    public static ArrayList<Tile> extractBoardFromResponse(String response) {
        ArrayList<Tile> board = new ArrayList<>();
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONObject value = responseJson.getJSONObject("value");
            JSONArray boardJson = value.getJSONArray("board");
            for (int i = 0; i < boardJson.length(); i++) {
                JSONObject tileJson = boardJson.getJSONObject(i);
                int position = tileJson.getInt("position");
                String state = tileJson.getString("state");
                String shipId = tileJson.getString("shipId");
                Tile tile = new Tile(position, TileStateEnum.fromString(state), shipId);
//                Log.d("myDEBUG GameLogic", "extractBoardFromResponse: " + tile);
                board.add(tile);
            }
        } catch (JSONException e) {
            Log.e("myDEBUG GameLogic", "extractBoardFromResponse: " + e);
        }
        return board;
    }
}
