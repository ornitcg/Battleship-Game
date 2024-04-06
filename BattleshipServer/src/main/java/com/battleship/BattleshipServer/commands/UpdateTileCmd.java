package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.resources.ApiResponse;

public class UpdateTileCmd {

    private Tile tileToUpdate;
    private TileDao tileDao;

    public UpdateTileCmd(TileDao tileDao, Tile tileToUpdate) {
        this.tileToUpdate = tileToUpdate;
        this.tileDao = tileDao;
    }

    public ApiResponse<String> execute() {
        ApiResponse<String> retVal;

        retVal = tileDao.update(tileToUpdate, tileToUpdate.getBoardId());

        return retVal;
    }
}
