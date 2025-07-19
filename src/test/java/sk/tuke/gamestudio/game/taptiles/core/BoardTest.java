package sk.tuke.gamestudio.game.taptiles.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;
    private final int rowsCount = 4;
    private final int columnCount = 4;

    @BeforeEach
    public void setUp() {
        board = new Board(rowsCount, columnCount);
    }

    @Test
    public void testBoardInitialization() {
        assertNotNull(board);
        assertEquals(rowsCount, board.getRowCount());
        assertEquals(columnCount, board.getColumnCount());
        assertNotNull(board.getTiles());
        assertEquals(rowsCount, board.getTiles().length);
        assertEquals(columnCount, board.getTiles()[0].length);
    }

    @Test
    public void testInvalidBoardSize() {
        assertThrows(IllegalArgumentException.class, () -> new Board(3, 3));
        assertThrows(IllegalArgumentException.class, () -> new Board(9, 9));
    }

    @Test
    public void testUndo() {
        Tile tile = board.getTile(0, 0);
        board.chooseTile(tile);
        board.saveState();

        board.getTile(0, 0).setTileState(TileState.DISABLED);

        board.undo();
        assertEquals(TileState.ENABLED, board.getTile(0, 0).getTileState());
    }

    @Test
    public void testChooseTile() {
        Tile tile = board.getTile(0, 0);
        board.chooseTile(tile);
        assertEquals(TileState.CHOOSE, tile.getTileState());

        board.chooseTile(tile);
        assertEquals(TileState.CHOOSE, tile.getTileState());
    }

    @Test
    public void testCheckGameState() {
        board.checkGameState();

        assertEquals(BoardState.PLAYING, board.getBoardState());

        Tile tile1 = board.getTile(0, 0);
        Tile tile2 = board.getTile(0, 1);

        board.chooseTile(tile1);
        board.chooseTile(tile2);

        board.connectTiles(tile1, tile2);

        board.checkGameState();
        assertEquals(BoardState.PLAYING, board.getBoardState());
    }
}