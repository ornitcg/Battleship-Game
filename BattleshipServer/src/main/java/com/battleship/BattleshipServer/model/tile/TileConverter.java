package com.battleship.BattleshipServer.model.tile;

public class TileConverter {

    public static Tile fromDb(DbTile dbTile){
        Tile retVal = null;

        if (dbTile != null) {
            retVal = new Tile(dbTile.getPosition(), dbTile.getBoardId(), TileStateEnum.fromString(dbTile.getState()),
                    dbTile.getShipId());
        }

        return retVal;
    }

    public static DbTile toDb(Tile tile) {
        DbTile retVal = null;

        if (tile != null) {
            retVal = new DbTile(tile.getPosition(), tile.getBoardId(), tile.getState().getName(), tile.getShipId());
        }

        return retVal;
    }
}
