package com.battleship.BattleshipServer.model.game;


public class GameConverter {
    public static Game fromDb(DbGame dbGame){
        Game retVal = null;

        if (dbGame != null) {
            retVal = new Game(dbGame.getId(), dbGame.getTurnUserId(), dbGame.getStartTime(), dbGame.getEndTime(), GameStateEnum.fromString(dbGame.getGameState()), dbGame.getWinnerUserId(), dbGame.getLooserUserId());
        }

        return retVal;
    }

    public static DbGame toDb(Game game) {
        DbGame retVal = null;

        if (game != null) {
            retVal = new DbGame(game.getId(), game.getTurnUserId(), game.getStartTime(), game.getEndTime(), game.getGameState().getName(), game.getWinnerUserId(), game.getLooserUserId());
        }

        return retVal;
    }
}
