package sk.tuke.gamestudio.game.taptiles.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    private Tile tile;

    @BeforeEach
    public void setUp() {
        tile = new Tile('A', 0, 0);
    }

    @Test
    public void testTileInitialization() {
        assertNotNull(tile);
        assertEquals('A', tile.getValue());
        assertEquals(0, tile.getRow());
        assertEquals(0, tile.getColumn());
        assertEquals(TileState.ENABLED, tile.getTileState());
    }

    @Test
    public void testSetTileState() {
        tile.setTileState(TileState.CHOOSE);
        assertEquals(TileState.CHOOSE, tile.getTileState());
    }

    @Test
    public void testTilePosition() {
        tile.setRow(1);
        tile.setColumn(2);
        assertEquals(1, tile.getRow());
        assertEquals(2, tile.getColumn());
    }

    @Test
    public void testTileStateEnumValues() {
        assertEquals(3, TileState.values().length);
        assertEquals(TileState.ENABLED, TileState.valueOf("ENABLED"));
        assertEquals(TileState.CHOOSE, TileState.valueOf("CHOOSE"));
        assertEquals(TileState.DISABLED, TileState.valueOf("DISABLED"));
    }

    @Test
    public void testTileStateToString() {
        assertEquals("ENABLED", TileState.ENABLED.toString());
        assertEquals("CHOOSE", TileState.CHOOSE.toString());
        assertEquals("DISABLED", TileState.DISABLED.toString());
    }

    @Test
    public void testTileStateOrdinal() {
        assertEquals(0, TileState.ENABLED.ordinal());
        assertEquals(1, TileState.CHOOSE.ordinal());
        assertEquals(2, TileState.DISABLED.ordinal());
    }
}
