package com.clientapp.battleshipclient.logic;

import android.content.Context;
import com.clientapp.battleshipclient.networking.GameNW;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameBoard {
    private String userId;
    @Getter
    private ArrayList<Tile> tilesList = new ArrayList<>();
    private int totalTilesOccupied;
    @Getter
    private int totalTilesMissed;
    @Getter
    private int totalTilesHit;
    GameNW gameNW = null;
    private final int TOTAL_SHIP_TILES = 17;
    Context context;

    // constructor to use when done with ship placement
    public GameBoard(Context context,String userId, ArrayList<Tile> tilesList ){
        this.userId = userId;
        this.tilesList = tilesList;
        this.totalTilesMissed = 0;
        this.totalTilesHit = 0;
        this.gameNW = new GameNW(userId);
        this.context = context;
    }

    public GameBoard(String userId){
        this.userId = userId;
        this.totalTilesMissed = 0;
        this.totalTilesHit = 0;

        for (int i = 0; i < 100; i++){
            this.tilesList.add(new Tile(i));
        }
    }

    public void sendTilesDataToServer(String currPlayerUserId) {
        gameNW = new GameNW(currPlayerUserId);
        gameNW.sendTilesDataToServer(context,currPlayerUserId ,tilesList);
    }


    public void setTile(int Position, Tile tile){
        int x = Position / 10;
        int y = Position % 10;
    }

    public Tile getTile(int Position){
        int x = Position / 10;
        int y = Position % 10;
        return null;
    }

    public void setTotalTilesMissed(int totalTilesMissed){
        this.totalTilesMissed = totalTilesMissed;
    }

    public void setTotalTilesHit(int totalTilesHit){
        this.totalTilesHit = totalTilesHit;
    }


    //    public void sendBoardToNW(String currPlayerUserId, Context context) {
//        String boardJSON = serializeGameBoardToJson();
//        Log.d("GameBoard", "sendBoardToNW: " + boardJSON);
//        gameNW = new GameNW(currPlayerUserId);
//        gameNW.sendBoardToServer(context, tilesList);
//
//    }

//    public String serializeGameBoardToJson() {
//        Gson gson = new Gson();
//        return gson.toJson(this);
//    }


}
