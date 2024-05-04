package com.battleship.BattleshipServer.commands;

import com.battleship.BattleshipServer.dao.BoardDao;
import com.battleship.BattleshipServer.dao.GameDao;
import com.battleship.BattleshipServer.dao.ShipDao;
import com.battleship.BattleshipServer.dao.TileDao;
import com.battleship.BattleshipServer.logic.CreateAttackResponse;
import com.battleship.BattleshipServer.model.AttackResultEnum;
import com.battleship.BattleshipServer.model.Board;
import com.battleship.BattleshipServer.model.game.Game;
import com.battleship.BattleshipServer.model.game.GameStateEnum;
import com.battleship.BattleshipServer.model.ship.OrientationEnum;
import com.battleship.BattleshipServer.model.ship.Ship;
import com.battleship.BattleshipServer.model.ship.ShipTypeEnum;
import com.battleship.BattleshipServer.model.tile.Tile;
import com.battleship.BattleshipServer.model.tile.TileStateEnum;
import com.battleship.BattleshipServer.resources.ApiResponse;
import com.battleship.BattleshipServer.resources.GameResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateAttackCmd {
    private final GameDao gameDao;

    private final BoardDao boardDao;

    private final TileDao tileDao;
    private final ShipDao shipDao;

    private final String userId;
    private final String gameId;

    private final Integer position;

    public CreateAttackCmd(GameDao gameDao, BoardDao boardDao, TileDao tileDao, ShipDao shipDao, String userId, String gameId, Integer position) {
        this.gameDao = gameDao;
        this.boardDao = boardDao;
        this.tileDao = tileDao;
        this.shipDao = shipDao;
        this.userId = userId;
        this.gameId = gameId;
        this.position = position;
    }

    public ApiResponse<CreateAttackResponse> execute() {
        ApiResponse<CreateAttackResponse> retVal = null;

        //list will be null if failed to fetch, else always size=2 when one is player's game board and one opponents
        List<Board> gameBoards = getGameBoards();

        if (gameBoards != null) {
            Board opponentBoard = gameBoards.stream().filter(e -> e.getUserId().equals(userId) == false).toList().get(0);
            CreateAttackResponse attackResponse = getAttackResponse(opponentBoard);

            if (attackResponse != null) {
                String attackResult = attackResponse.getAttackResult();
                boolean succeeded = updateGame(opponentBoard.getId(), opponentBoard.getUserId(),
                        AttackResultEnum.fromString(attackResult));

                if (succeeded) {
                    insertAttackToSet();
                    retVal = ApiResponse.createSucceededResponse(attackResponse);
                }
            }
        }

        //there was a problem
        if (retVal == null) {
            retVal = ApiResponse.createFailedResponse("Failed to attack");
        }

        return retVal;
    }

    private Game getGame() {
        Game retVal;

        ApiResponse<Game> gameApiResponse = gameDao.get(gameId);
        retVal = gameApiResponse.getValue();

        return retVal;
    }

    private List<Board> getGameBoards() {
        List<Board> retVal;

        ApiResponse<List<Board>> gameBoardsResponse = boardDao.getGameBoards(gameId);
        retVal = gameBoardsResponse.getValue();

        return retVal;
    }

    private CreateAttackResponse getAttackResponse(Board opponentBoard) {
        CreateAttackResponse retVal = null;
        String boardId = opponentBoard.getId();

        ApiResponse<List<Tile>> boardTilesResponse = tileDao.getBoardTiles(boardId);
        List<Tile> boardTiles = boardTilesResponse.getValue();

        if (boardTiles != null) {
            Tile tile = boardTiles.stream().filter(e -> e.getPosition() == position).collect(Collectors.toList()).get(0);

            if (tile != null) {
                retVal = getAttackResponse(tile);
            }
        }

        return retVal;
    }

    private CreateAttackResponse getAttackResponse(Tile tile) {
        CreateAttackResponse retVal;

        TileStateEnum state = tile.getState();

        retVal = switch (state) {
            case SEA, HIT, MISS -> {
                AttackResultEnum attackResult = AttackResultEnum.MISS;
                yield CreateAttackResponse.createResponse(attackResult.getName());
            }
            case SHIP -> {
                CreateAttackResponse toYield = null;
                AttackResultEnum attackResult = null;
                Ship ship = getShip(tile.getShipId());

                if (ship != null) {
                    Integer size = ship.getSize();
                    Integer numHits = ship.getNumHits() + 1;

                    if (Objects.equals(size, numHits)) {
                        attackResult = AttackResultEnum.SUNK;
                    } else {
                        boolean updated = updateShip(ship);

                        if (updated) {
                            attackResult = AttackResultEnum.HIT;
                        }
                    }

                    if (attackResult != null) {
                        toYield = switch (attackResult) {
                            case HIT -> CreateAttackResponse.createResponse(attackResult.getName());
                            case SUNK -> {
                                ShipTypeEnum shipType = ship.getType();
                                Integer position = ship.getPosition();
                                OrientationEnum orientation = ship.getOrientation();
                                yield CreateAttackResponse.createSunkResponse(attackResult.getName(), shipType.getName(), position, orientation.getName());
                            }
                            // can't happen
                            default -> throw new IllegalStateException("Unexpected value: " + attackResult);
                        };
                    }
                }

                yield toYield;
            }
        };

        return retVal;
    }

    private Ship getShip(String shipId) {
        Ship retVal;
        ApiResponse<Ship> shipApiResponse = shipDao.get(shipId);
        retVal = shipApiResponse.getValue();

        return retVal;
    }

    private boolean updateShip(Ship ship) {
        boolean retVal;

        ship.setNumHits(ship.getNumHits() + 1);
        ApiResponse<String> update = shipDao.update(ship, ship.getId());
        retVal = update.getValue() != null;

        return retVal;
    }

    private boolean updateGame(String boardId, String opponentId, AttackResultEnum attackResult) {
        boolean retVal = false;
        Game game = getGame();

        if (game != null) {
            Object o = switch (attackResult) {
                case HIT -> null;
                case MISS -> {
                    game.setTurnUserId(opponentId);
                    yield true; // only for compiling
                }
                case SUNK -> {
                    Boolean gameOver = isGameOver(boardId);

                    if (gameOver != null) {
                        if (gameOver) {
                            game.setGameState(GameStateEnum.FINISHED);
                            game.setWinnerUserId(userId);
                            game.setLooserUserId(opponentId);
                            game.setEndTime(LocalDateTime.now());
                        }
                    }
                    yield true; // only for compiling
                }
            };

            ApiResponse<String> update = gameDao.update(game, gameId);
            retVal = update.getValue() != null;
        }

        return retVal;
    }

    private Boolean isGameOver(String boardId) {
        Boolean retVal = null;
        ApiResponse<List<Ship>> boardShipsResponse = shipDao.getBoardShips(boardId);

        if (boardShipsResponse.isSucceeded()) {
            List<Ship> ships = boardShipsResponse.getValue();
            List<Ship> shipsNotSunk = ships.stream().filter(e -> Objects.equals(e.getSize() == null, e.getNumHits())).toList();

            retVal = shipsNotSunk.isEmpty();
        }

        return retVal;
    }

    private void insertAttackToSet() {
        synchronized (GameResource.attacksLock) {
            GameResource.attacks.put(gameId, position);
            GameResource.attacksLock.notifyAll();
        }
    }
}
