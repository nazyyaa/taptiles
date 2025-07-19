package sk.tuke.gamestudio.game.taptiles.core;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Board {

    @Setter
    @Getter
    private Tile[][] tiles;

    private final Stack<Board> history;

    @Getter
    @Setter
    private BoardState boardState;

    @Getter
    private final int rowCount;

    @Getter
    private final int columnCount;


    public Board(int rowCount, int columnCount) {
        if (((rowCount * columnCount) % 4 != 0) || rowCount < 4 || columnCount < 4 || rowCount > 8 || columnCount > 8 ) throw new IllegalArgumentException("Invalid board size [4-8][4-8]");

        this.boardState = BoardState.PLAYING;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.tiles = new Tile[rowCount][columnCount];
        this.generate(rowCount, columnCount);

        this.history = new Stack<>();
    }

    public void generate(int row, int column) {
        char[] value = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z'
        };

        int totalTiles = row * column;
        ArrayList<int[]> positions = new ArrayList<>();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                positions.add(new int[]{i, j});
            }
        }

        Collections.shuffle(positions, new Random());

        int valueId = 0;
        int charCount = 0;

        for (int i = 0; i < totalTiles; i++) {
            int[] pos = positions.get(i);
            Tile newTile = new Tile(value[valueId], pos[0], pos[1]);
            tiles[pos[0]][pos[1]] = newTile;

            if (++charCount % 4 == 0) {
                valueId = (valueId + 1) % value.length;
                charCount = 0;
            }
        }
    }

    public void chooseTile(Tile tile) {
        if (tile == null || tile.getTileState() == TileState.CHOOSE || tile.getTileState() == TileState.DISABLED) return;

        tile.setTileState(TileState.CHOOSE);
    }

    public void connectTiles(Tile tile1, Tile tile2) {
        if (tile1 == null || tile2 == null || tile1 == tile2) return;

        if (possibleConnection(tile1, tile2)) {
            tile1.setSolved(1);
            tile2.setSolved(1);
            saveState();
            tile1.setTileState(TileState.DISABLED);
            tile2.setTileState(TileState.DISABLED);
        } else {
            tile1.setTileState(TileState.ENABLED);
            tile2.setTileState(TileState.ENABLED);
        }
    }

    public void checkGameState() {
        int tileCount = 0;
        boolean isSolved = true;

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Tile currentTile = getTile(row, column);

                if (currentTile.getTileState() != TileState.DISABLED) isSolved = false;

                if (currentTile.getTileState() == TileState.ENABLED) tileCount++;
            }
        }

        if (isSolved) {
            setBoardState(BoardState.SOLVED);
            return;
        }

        if (tileCount != 4) return;

        for (int row = 0; row < rowCount - 1; row++) {
            for (int column = 0; column < columnCount - 1; column++) {
                if (getTile(row, column).getTileState() == TileState.ENABLED &&
                        getTile(row, column).getValue() == getTile(row + 1, column + 1).getValue() &&
                        getTile(row, column + 1).getValue() == getTile(row + 1, column).getValue()) {
                    setBoardState(BoardState.FAILED);
                    return;
                    }
            }
        }
    }

    public boolean possibleConnection(Tile tile1, Tile tile2) {
        if (tile1.getValue() != tile2.getValue()) return false;
        if (tile1.getTileState() != TileState.CHOOSE || tile2.getTileState() != TileState.CHOOSE) return false;

        boolean rowMatchEdge = tile1.getRow() == tile2.getRow() && (tile1.getRow() == 0 || tile1.getRow() == getRowCount() - 1);
        boolean columnMatchEdge = tile1.getColumn() == tile2.getColumn() && (tile1.getColumn() == 0 || tile1.getColumn() == getColumnCount() - 1);

        int[][] visited = new int[rowCount][columnCount];

        return rowMatchEdge || columnMatchEdge || directions(tile1.getRow(), tile1.getColumn(), tile2.getRow(), tile2.getColumn(), visited);
    }

    private boolean directions(int startRow, int startColumn, int finishRow, int finishColumn, int[][] visited) {
        visited[startRow][startColumn] = 1;
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

        for (int[] direction : directions) {
            int tempRow = startRow + direction[0];
            int tempColumn = startColumn + direction[1];
            if (openDirection(tempRow, tempColumn, finishRow, finishColumn, visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean openDirection(int startRow, int startColumn, int finishRow, int finishColumn, int[][] visited) {
        if (startRow < 0 || startRow >= rowCount || startColumn < 0 || startColumn >= columnCount) return false;

        if (startRow == finishRow && startColumn == finishColumn) return true;

        if (visited[startRow][startColumn] == 1 || tiles[startRow][startColumn].getTileState() != TileState.DISABLED) {
            return false;
        }

        return directions(startRow, startColumn, finishRow, finishColumn, visited);
    }

    public void saveState() {
        Board savedState = new Board(this.rowCount, this.columnCount);
        savedState.setBoardState(this.boardState);

        Tile[][] savedTiles = new Tile[this.rowCount][this.columnCount];
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                Tile originalTile = this.tiles[row][column];
                Tile savedTile = new Tile(originalTile.getValue(), originalTile.getRow(), originalTile.getColumn());
                if (originalTile.getTileState() == TileState.CHOOSE) {
                    savedTile.setTileState(TileState.ENABLED);
                }
                else savedTile.setTileState(originalTile.getTileState());

                savedTiles[row][column] = savedTile;
            }
        }

        savedState.setTiles(savedTiles);
        history.push(savedState);
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }
    public void undo() {
        if (canUndo()) {
            Board previousState = history.pop();
            this.tiles = previousState.getTiles();
            this.boardState = previousState.getBoardState();
        }
    }

    public Tile getTile(int row, int column) {
        if (column > columnCount || row > rowCount || column < 0 || row < 0) return null;

        return this.tiles[row][column];
    }
}