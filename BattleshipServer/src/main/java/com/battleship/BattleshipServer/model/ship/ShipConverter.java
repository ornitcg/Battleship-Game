package com.battleship.BattleshipServer.model.ship;

import com.battleship.BattleshipServer.model.tile.DbTile;

public class ShipConverter {
    public static Ship fromDb(DbShip dbShip){
        Ship retVal = null;

        if (dbShip != null) {
            retVal = new Ship(dbShip.getId(), dbShip.getBoardId(), dbShip.getPosition(),
                    ShipTypeEnum.fromString(dbShip.getType()), OrientationEnum.fromString(dbShip.getOrientation()),
                    dbShip.getSize(), dbShip.getNumHits());
        }

        return retVal;
    }

    public static DbShip toDb(Ship ship) {
        DbShip retVal = null;

        if (ship != null) {
            retVal = new DbShip(ship.getId(), ship.getBoardId(), ship.getPosition(),
                    ship.getType().getName(), ship.getOrientation().getName(),
                    ship.getSize(), ship.getNumHits());
        }

        return retVal;
    }
}
