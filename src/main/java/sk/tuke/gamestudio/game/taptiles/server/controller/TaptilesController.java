package sk.tuke.gamestudio.game.taptiles.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.game.taptiles.core.Board;
import sk.tuke.gamestudio.game.taptiles.core.BoardState;
import sk.tuke.gamestudio.game.taptiles.core.Tile;
import sk.tuke.gamestudio.game.taptiles.core.TileState;
import sk.tuke.gamestudio.game.taptiles.entity.Score;
import sk.tuke.gamestudio.game.taptiles.entity.User;
import sk.tuke.gamestudio.game.taptiles.service.ScoreService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/taptiles/play")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TaptilesController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private HttpSession session;

    private Board board = new Board(4, 4);
    private int level = 1;

    private Tile chooseTile1 = null;
    private Tile chooseTile2 = null;

    private void setupBoard() {
        switch (level) {
            case 2:
                board = new Board(4, 5);
                break;
            case 3:
                board = new Board(4, 6);
                break;
            case 4:
                board = new Board(6, 6);
                break;
            case 5:
                board = new Board(7, 8);
                break;
            default:
                board = new Board(4, 4);
                break;
        }
    }

    @GetMapping
    public String play(Model model) {
        model.addAttribute("boardHtml", generateHtmlBoard());
        return "taptiles/play";
    }

    @GetMapping("/reset")
    @ResponseBody
    public String resetBoard() {
        setupBoard();
        return generateHtmlBoard();
    }

    @GetMapping("/getCurrentScore")
    @ResponseBody
    public int getCurrentScore() {
        int score = scoreService.getScore("taptiles", getNickname());
        return score;
    }


    @GetMapping("/level")
    @ResponseBody
    public String changeLevel(@RequestParam("value") int value) {
        this.level = value;
        setupBoard();
        return generateHtmlBoard();
    }

    @GetMapping("/undo")
    @ResponseBody
    public String undoMove() {
        if (board.canUndo()) {
            board.undo();
            scoreService.addScore(new Score("taptiles", getNickname(), -level, new java.util.Date()));
            return generateHtmlBoard();
        }
        return "";
    }

    @GetMapping("/choose")
    @ResponseBody
    public String chooseTile(@RequestParam int row, @RequestParam int col) {
        Tile chosen = board.getTile(row, col);
        if (chosen.getTileState() == TileState.CHOOSE) {
            chosen.setTileState(TileState.ENABLED);
            chooseTile1 = null;
            chooseTile2 = null;
            return generateHtmlBoard();
        }

        if (chooseTile1 == null) {
            chooseTile1 = chosen;
            board.chooseTile(chosen);
        } else if (chooseTile2 == null) {
            chooseTile2 = chosen;
            board.chooseTile(chosen);
            board.connectTiles(chooseTile1, chooseTile2);

            chooseTile1 = null;
            chooseTile2 = null;
        }
        board.checkGameState();
        if (board.getBoardState() == BoardState.SOLVED) {
            scoreService.addScore(new Score("taptiles", getNickname(), level * 10, new java.util.Date()));
            if (level < 5) {
                level++;
                setupBoard();
            }
            return "<div>Game Won!</div>";
        }

        if (board.getBoardState() == BoardState.FAILED) {
            scoreService.addScore(new Score("taptiles", getNickname(), - (level * 5), new java.util.Date()));
            setupBoard();
            return "<div>Game Lost!</div>";
        }
        return generateHtmlBoard();
    }

    @GetMapping("/html")
    @ResponseBody
    private String generateHtmlBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2 class=\"text-center\" style=\"color: white\">" + toStringLevel(level) + "</h2>");
        sb.append("<table class=\"d-flex flex-column align-items-center\">\n");

        for (int row = 0; row < board.getRowCount(); row++) {
            sb.append("<tr>\n");
            for (int col = 0; col < board.getColumnCount(); col++) {
                Tile tile = board.getTile(row, col);

                char tileValue = tile.getValue();
                String tileColor = getColorForTile(tileValue);

                String selectedClass = tile.getTileState() == TileState.CHOOSE ? "selected" : "";

                sb.append("<td>\n");
                if (tile.getTileState() == TileState.DISABLED && tile.getSolved() == 1) {
                    sb.append("<g class=\"tile removed\" style=\"color: " + tileColor + ";\">\n");
                    tile.setSolved(0);
                } else if (tile.getTileState() == TileState.DISABLED) {
                    sb.append("</td>\n");
                    continue;
                } else {
                    sb.append("<g class=\"tile " + selectedClass + "\" style=\"color: " + tileColor + ";\" onclick=\"choose(" + row + ", " + col + ")\">\n");
                }
                sb.append(tileValue);
                sb.append("</g>\n");
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }

        sb.append("</table>\n");
        return sb.toString();
    }

    private String getColorForTile(char tileValue) {
        return switch (tileValue) {
            case '0' -> "gray";
            case '1' -> "blue";
            case '2' -> "green";
            case '3' -> "red";
            case '4' -> "purple";
            case '5' -> "orange";
            case '6' -> "yellow";
            case '7' -> "pink";
            case '8' -> "cyan";
            case '9' -> "magenta";
            case 'A' -> "lightblue";
            case 'B' -> "lightgreen";
            case 'C' -> "lightcoral";
            case 'D' -> "lightyellow";
            case 'E' -> "lightgray";
            case 'F' -> "gold";
            case 'G' -> "salmon";
            case 'H' -> "lightseagreen";
            case 'I' -> "tomato";
            case 'J' -> "violet";
            case 'K' -> "plum";
            case 'L' -> "lime";
            case 'M' -> "maroon";
            case 'N' -> "navy";
            case 'O' -> "olive";
            case 'P' -> "peru";
            case 'Q' -> "seashell";
            case 'R' -> "seashell";
            case 'S' -> "slateblue";
            case 'T' -> "slategray";
            case 'U' -> "snow";
            case 'V' -> "springgreen";
            case 'W' -> "steelblue";
            case 'X' -> "tan";
            case 'Y' -> "teal";
            case 'Z' -> "tomato";
            default -> "black";
        };
    }

    private String toStringLevel(int level) {
        return switch (level) {
            case 2 -> "Storm";
            case 3 -> "Thunder";
            case 4 -> "Inferno";
            case 5 -> "Apocalypse";
            default -> "Breeze";
        };
    }

    public String getNickname() {
        User user = (User) session.getAttribute("user");
        return user.getUsername();
    }
}