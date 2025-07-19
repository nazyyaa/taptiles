package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";
    public static final String INSERT = "INSERT INTO rating (game, player, rating, rated_on) " + "VALUES (?, ?, ?, ?) " + "ON CONFLICT (game, player) " + "DO UPDATE SET rating = EXCLUDED.rating, rated_on = EXCLUDED.rated_on;";
    static final String SELECT_AVG = "SELECT AVG(rating) FROM rating WHERE game = ?";
    public static final String SELECT_ALL = "SELECT player, rating FROM rating WHERE game = ?";
    public static final String SELECT_SINGLE = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    public static final String DELETE = "DELETE FROM rating";

    @Override
    public void setRating(Rating rating) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getPlayer());
            statement.setInt(3, rating.getRating());
            statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Problem inserting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVG)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting average rating", e);
        }
    }

    @Override
    public List<Rating> getRatings(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Rating> ratings = new ArrayList<>();
                while (rs.next()) {
                    String player = rs.getString("player");
                    int rating = rs.getInt("rating");

                    Rating ratingObj = new Rating();
                    ratingObj.setGame(game);
                    ratingObj.setPlayer(player);
                    ratingObj.setRating(rating);
                    ratings.add(ratingObj);
                }
                return ratings;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting ratings for game " + game, e);
        }
    }


    @Override
    public int getRating(String game, String player) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_SINGLE)
        ) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating for player", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting ratings", e);
        }
    }
}
