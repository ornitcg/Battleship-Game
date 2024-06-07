package com.clientapp.battleshipclient.data.Tile;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tile  implements Serializable {

    private int position;
    private TileStateEnum state;
    private String shipId;
    private String boardId;



    public Tile(int position){
        this.state = TileStateEnum.SEA;
        this.setShipId("-1");
        this.setBoardId(null);
        this.setPosition(position);
    }



    public void setFired(boolean isFired){

        if (isFired){
            if (state == TileStateEnum.SHIP){
                state = TileStateEnum.HIT;
            }
            else if (state == TileStateEnum.SEA){
                state = TileStateEnum.MISS;
            }
        }
        if (state == TileStateEnum.SHIP){
            state = TileStateEnum.HIT;
        }
        else if (state == TileStateEnum.SEA){
            state = TileStateEnum.MISS;
        }
    }

    public void resetSingleTileData() {
        this.setState(TileStateEnum.SEA);
        this.setShipId("-1");
    }



    public void setState(TileStateEnum tileStateEnum) {
        this.state = tileStateEnum;
    }

}
