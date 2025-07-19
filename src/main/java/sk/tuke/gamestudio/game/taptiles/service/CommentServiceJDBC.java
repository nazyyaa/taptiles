package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService {
    public static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";

    public static final String SELECT_PLAYER = "SELECT * FROM comment WHERE game = ? AND player = ?";
    public static final String INSERT = "INSERT INTO comment (game, player, comment, commented_on) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE comment SET comment = ?, commented_on = ? WHERE game = ? AND player = ?";
    public static final String SELECT = "SELECT game, player, comment, commented_on FROM comment WHERE game = ? ORDER BY commented_on DESC LIMIT 10";
    public static final String DELETE = "DELETE FROM comment";

    @Override
    public void addComment(Comment comment) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_PLAYER)
        ) {
            selectStatement.setString(1, comment.getGame());
            selectStatement.setString(2, comment.getPlayer());

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                    updateStatement.setString(1, comment.getComment());
                    updateStatement.setTimestamp(2, new Timestamp(comment.getCommentedOn().getTime()));
                    updateStatement.setString(3, comment.getGame());
                    updateStatement.setString(4, comment.getPlayer());
                    updateStatement.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, comment.getGame());
                    insertStatement.setString(2, comment.getPlayer());
                    insertStatement.setString(3, comment.getComment());
                    insertStatement.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new CommentException("Problem inserting or updating comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Comment> comments = new ArrayList<>();
                while (rs.next()) {
                    comments.add(new Comment(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4)));
                }
                return comments;
            }
        } catch (SQLException e) {
            throw new CommentException("Problem selecting comments", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new CommentException("Problem deleting comments", e);
        }
    }
}