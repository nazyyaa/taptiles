package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Score;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ScoreServiceJDBC implements ScoreService {
    public static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";
    public static final String SELECT = "SELECT game, player, points, played_on FROM score WHERE game = ? ORDER BY points DESC LIMIT 10";
    public static final String SELECT_PLAYER = "SELECT * FROM score WHERE game = ? AND player = ?";
    public static final String DELETE = "DELETE FROM score";
    public static final String INSERT = "INSERT INTO score (game, player, points, played_on) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE score SET points = ?, played_on = ? WHERE game = ? AND player = ?";

    @Override
    public int getScore(String game, String username) {
        final String SQL = "SELECT points FROM score WHERE game = ? AND player = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setString(1, game);
            statement.setString(2, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;
        } catch (SQLException e) {
            throw new ScoreException("Problem getting score", e);
        }
    }

    @Override
    public void addScore(Score score) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_PLAYER)
        ) {
            selectStatement.setString(1, score.getGame());
            selectStatement.setString(2, score.getPlayer());

            ResultSet resultSet = selectStatement.executeQuery();
            int newPoints = score.getPoints();

            if (resultSet.next()) {
                int existingPoints = resultSet.getInt("points");
                newPoints = existingPoints + score.getPoints();
                if (newPoints < 0) newPoints = 0;

                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                    updateStatement.setInt(1, newPoints);
                    updateStatement.setTimestamp(2, new Timestamp(score.getPlayedOn().getTime()));
                    updateStatement.setString(3, score.getGame());
                    updateStatement.setString(4, score.getPlayer());
                    updateStatement.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    if (newPoints < 0) newPoints = 0;
                    insertStatement.setString(1, score.getGame());
                    insertStatement.setString(2, score.getPlayer());
                    insertStatement.setInt(3, newPoints);
                    insertStatement.setTimestamp(4, new Timestamp(score.getPlayedOn().getTime()));
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem inserting or updating score", e);
        }
    }


    @Override
    public List<Score> getTopScores(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT);
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Score> scores = new ArrayList<>();
                while (rs.next()) {
                    scores.add(new Score(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getTimestamp(4)));
                }
                return scores;
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem selecting score", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new ScoreException("Problem deleting score", e);
        }
    }
}
