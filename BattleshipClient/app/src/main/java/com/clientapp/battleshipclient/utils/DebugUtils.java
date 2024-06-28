package com.clientapp.battleshipclient.utils;

import android.util.Log;

import com.clientapp.battleshipclient.model.Tile.Tile;

import java.util.ArrayList;

public class DebugUtils {

    public static void LongLog(String start, String data)
    {
        while (data.length() > 0) {
            int endIndex = Math.min(data.length(), 4000);
            Log.d(start, data.substring(0, endIndex));
            data = data.substring(endIndex);
        }

    }

    public static void logTilesStatus(String methodName, ArrayList<Tile> tilesList) { ///TODO - for DEBUG
        for (Tile tile : tilesList) {
            Log.d("DEBUG logTilesStatus " + methodName, "Tile: " + tile.getPosition() + " STATUS: " + tile.getState() + " shipId: " + tile.getShipId() );
        }
    }
}
