package sk.tuke.gamestudio.game.taptiles.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {
    private final char value;
    private TileState tileState;
    private int row;
    private int column;
    private int solved;

    public Tile(char value, int row, int column) {
        this.value = value;
        this.tileState = TileState.ENABLED;
        this.row = row;
        this.column = column;
        solved = 0;
    }
}