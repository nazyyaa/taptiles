package sk.tuke.gamestudio.game.taptiles.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.tuke.gamestudio.game.taptiles.core.*;
import sk.tuke.gamestudio.game.taptiles.service.*;
import sk.tuke.gamestudio.game.taptiles.entity.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConsoleUI {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    private String nickname;

    private Pattern INPUT_PATTERN;

    private Board board;
    private int level;
    private Tile chooseTile1;
    private Tile chooseTile2;

    private final ScoreService scoreService;

    private final CommentService commentService;

    private final RatingService ratingService;


    @Autowired
    public ConsoleUI(ScoreService scoreService, CommentService commentService, RatingService ratingService) {
        this.scoreService = scoreService;
        this.commentService = commentService;
        this.ratingService = ratingService;

        this.level = 1;
        setupBoard();
    }

    public void play() {
        INPUT_PATTERN = Pattern.compile("([A-" + (char) (board.getRowCount() + 'A' - 1) + "])([1-" + board.getColumnCount() + "])");

        while (true) {
            printBoard();

            board.checkGameState();
            if (board.getBoardState() != BoardState.PLAYING) break;
            processInput();

        }
        motdGameResult();
    }

    private void processInput() {
        System.out.print(ANSI_BLUE + "Please, input coordinates: " + ANSI_RESET);

        String input = new Scanner(System.in).nextLine().trim().toUpperCase();

        switch (input) {
            case "M":
                toggleMenu();
                return;

            case "X":
                System.exit(0);

            case "R":
                setupBoard();
                return;

            case "U":
                if (board.canUndo()) {
                    board.undo();
                    scoreService.addScore(new Score("taptiles", nickname, -level, new java.util.Date()));
                }
                return;
        }

        Matcher matcher = INPUT_PATTERN.matcher(input);
        if (!matcher.matches()) {
            System.out.println(ANSI_RED + "Sorry, but your INPUT is bad!" + ANSI_RESET);
            return;
        }

        try {
            int row = matcher.group(1).charAt(0) - 'A';
            int column = Integer.parseInt(matcher.group(2)) - 1;
            Tile chooseTile = board.getTile(row, column);

            if (chooseTile.getTileState() != TileState.ENABLED) {
                System.out.println(ANSI_RED + "Sorry, but your INPUT is bad!" + ANSI_RESET);
                return;
            }


            if (chooseTile1 == null) {

                chooseTile1 = chooseTile;
                board.chooseTile(chooseTile1);

            } else if (chooseTile2 == null) {

                chooseTile2 = chooseTile;
                board.chooseTile(chooseTile2);

                board.connectTiles(chooseTile1, chooseTile2);

                chooseTile1 = null;
                chooseTile2 = null;
            }

        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Sorry, but your INPUT is bad!" + ANSI_RESET);
        }
    }

    private void motdGameResult() {
        switch (board.getBoardState()) {
            case SOLVED:
                System.out.println(ANSI_RED + "Congratulations! You won a game!");
                scoreService.addScore(new Score("taptiles", nickname, level * 10, new java.util.Date()));
                break;

            case FAILED:
                System.out.println(ANSI_RED + "Sorry. Dead-End. Game is over...");
                scoreService.addScore(new Score("taptiles", nickname, - (level * 5), new java.util.Date()));
                break;
        }

        setupBoard();
    }

    public void motdGameLogin() {
        clearConsole(1);
        System.out.println(ANSI_PURPLE + "Hi. Welcome to Taptiles");
        clearConsole(1);
        System.out.print("Please, enter your nickname: ");
        nickname = new Scanner(System.in).nextLine().trim().toUpperCase();
        scoreService.addScore(new Score("taptiles", nickname, 0, new java.util.Date()));

        toggleMenu();
    }

    private void toggleMenu() {
        clearConsole(4);
        System.out.println(ANSI_PURPLE + "      MENU");
        System.out.println(ANSI_PURPLE + "Welcome, " + ANSI_BLUE + nickname + "\n ");
        System.out.println(ANSI_PURPLE + "1. " + ANSI_RESET + "Play");
        System.out.println(ANSI_PURPLE + "2. " + ANSI_RESET + "Comment");
        System.out.println(ANSI_PURPLE + "3. " + ANSI_RESET + "Rate");
        System.out.println(ANSI_PURPLE + "4. " + ANSI_RESET + "Leaderboard");
        System.out.println(ANSI_PURPLE + "5. " + ANSI_RESET + "Change level");
        System.out.println(ANSI_PURPLE + "6. " + ANSI_RESET + "Change player");
        System.out.println(ANSI_PURPLE + "7. " + ANSI_RESET + "Exit");

        clearConsole(1);

        String temp = new Scanner(System.in).nextLine().trim();

        while (true) {
            switch (temp) {
                case "1":
                    play();
                    temp = "default";
                    break;
                case "2":
                    controlService(0);
                    temp = "default";
                    break;
                case "3":
                    controlService(1);
                    temp = "default";
                    break;
                case "4":
                    controlService(2);
                    temp = "default";
                    break;
                case "5":
                    changeSettings(0);
                    temp = "default";
                    break;
                case "6":
                    changeSettings(1);
                    temp = "default";
                    break;
                case "7":
                    System.exit(0);
                default:
                    exitMenu();
                    toggleMenu();
                    break;
            }
        }
    }

    private void printBoard() {
        clearConsole(3);

        System.out.println(ANSI_PURPLE + "[X] - exit, [R] - restart, [U] - undo, [M] - menu, coordinates - e.g. [A1].\n");
        System.out.println(ANSI_RED + "   LEVEL:  " + level + ANSI_RESET + "\n    ");

        clearConsole(1);

        System.out.print("    " + ANSI_CYAN);
        for (int length = 0; length < board.getColumnCount(); ++length) {
            System.out.print((length + 1) + " ");
        }

        System.out.print("\n   " + ANSI_PURPLE);

        for (int column = 0; column < board.getColumnCount(); ++column) {
            System.out.print("--");
        }
        System.out.print("-\n" + ANSI_RESET);

        for (int row = 0; row < board.getRowCount(); ++row) {
            System.out.print(ANSI_CYAN + (char) ('A' + row) + ANSI_PURPLE + " | " + ANSI_RESET);
            for (int column = 0; column < board.getColumnCount(); ++column) {
                printTile(row, column);
            }
            System.out.println();
        }

        clearConsole(1);
    }

    private void printTile(int row, int column) {
        Tile tile = board.getTile(row, column);
        switch (tile.getTileState()) {
            case ENABLED:
                System.out.print(tile.getValue() + " ");
                break;
            case CHOOSE:
                System.out.print(ANSI_RED + tile.getValue() + ANSI_RESET + " ");
                break;
            case DISABLED:
                System.out.print("  ");
                break;
        }
    }

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
                board = new Board(8, 8);
                break;
            default:
                board = new Board(4, 4);
                break;
        }
    }

    private void changeSettings(int setting) {
        String temp;
        clearConsole(3);
        switch (setting) {
            case 0:
                System.out.println(ANSI_PURPLE + "LEVEL NOW: " + ANSI_RED + level);
                System.out.print(ANSI_PURPLE + "Please, enter level [1-5]: ");

                while (true) {
                    temp = new Scanner(System.in).nextLine().trim().toUpperCase();
                    try {
                        int temp_level = Integer.parseInt(temp);
                        if (temp_level > 0 && temp_level < 6) {
                            level = temp_level;
                            setupBoard();
                            break;
                        } else System.out.println(ANSI_RED + "Please, enter valid level! [1-5]");
                    } catch (NumberFormatException e) {
                        System.out.println(ANSI_RED + "Please, enter valid level! [1-5]");
                    }
                }

                clearConsole(1);
                System.out.println(ANSI_PURPLE + "Level successfully changed to: " + ANSI_RED + level + ANSI_RESET);


                break;
            case 1:
                System.out.println(ANSI_PURPLE + "NICKNAME NOW: " + ANSI_BLUE + nickname);
                System.out.print(ANSI_PURPLE + "Please, enter your new nickname: ");

                temp = new Scanner(System.in).nextLine().trim().toUpperCase();

                nickname = temp;
                scoreService.addScore(new Score("taptiles", nickname, 0, new java.util.Date()));

                clearConsole(1);
                System.out.println(ANSI_PURPLE + "Nickname changed to: " + ANSI_BLUE + nickname);
                break;

            default:
                break;
        }
    }

    private void controlService(int service) {
        clearConsole(3);

        Scanner scanner = new Scanner(System.in);

        switch (service) {
            case 0:
                System.out.println(ANSI_PURPLE + "Comments for Taptiles:\n");

                var comments = commentService.getComments("taptiles");

                if (comments.isEmpty()) {
                    System.out.println(ANSI_RED + "No comments yet.");
                } else {
                    for (var comment : comments) {
                        System.out.println(ANSI_BLUE + comment.getPlayer() + ": " + ANSI_RESET + comment.getComment() + ANSI_CYAN + " (" + comment.getCommentedOn() + ")");
                    }
                }

                while (true) {
                    System.out.print(ANSI_PURPLE + "\nDo you want to add a comment? (Y/N): ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("Y")) {
                        System.out.print(ANSI_PURPLE + "\nEnter your comment: " + ANSI_RESET);
                        String commentText = scanner.nextLine().trim();
                        commentService.addComment(new Comment("taptiles", nickname, commentText, new java.util.Date()));
                        System.out.println(ANSI_BLUE + "\nComment added successfully!");
                        break;
                    } else if (input.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println(ANSI_RED + "\nInvalid input, please enter 'Y' or 'N'.");
                    }
                }
                break;

            case 1:
                int avgRating = ratingService.getAverageRating("taptiles");
                System.out.println(ANSI_PURPLE + "Average rating for Taptiles: " + ANSI_RED + (avgRating > 0 ? avgRating + "/5" : "No ratings yet"));

                while (true) {
                    System.out.print(ANSI_PURPLE + "\nDo you want to rate the game? (Y/N): ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("Y")) {
                        System.out.print(ANSI_PURPLE + "\nEnter your rating [1-5]: " + ANSI_RESET);
                        while (true) {
                            String ratingInput = scanner.nextLine().trim();
                            try {
                                int rate = Integer.parseInt(ratingInput);
                                if (rate >= 1 && rate <= 5) {
                                    ratingService.setRating(new Rating("taptiles", nickname, rate, new java.util.Date()));
                                    System.out.println(ANSI_BLUE + "\nRating submitted successfully!");
                                    break;
                                } else {
                                    System.out.println(ANSI_RED + "\nRating must be between 1 and 5!");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(ANSI_RED + "\nPlease enter a valid number [1-5]!");
                            }
                        }
                        break;
                    } else if (input.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println(ANSI_RED + "\nInvalid input, please enter 'Y' or 'N'.");
                    }
                }
                break;
            case 2:
                System.out.println(ANSI_PURPLE + "Top 10 Scores for Taptiles:\n");

                var scores = scoreService.getTopScores("taptiles");

                if (scores.isEmpty()) {
                    System.out.println(ANSI_RED + "No scores yet.");
                } else {
                    int position = 1;
                    for (var score : scores) {
                        System.out.println(ANSI_RED + position + ". " + ANSI_BLUE + score.getPlayer() + ": " + ANSI_RESET + score.getPoints() + " pts " + ANSI_CYAN + "(" + score.getPlayedOn() + ")");
                        position++;
                    }
                }
                break;

            default:
                break;
        }
    }

    private void clearConsole(int length) {
        for (int i = 0; i < length; i++)
            System.out.println(" ");
    }

    private void exitMenu() {
        clearConsole(1);
        System.out.println(ANSI_RED + "Press (ENTER) for back to menu");
        new Scanner(System.in).nextLine();
    }
}