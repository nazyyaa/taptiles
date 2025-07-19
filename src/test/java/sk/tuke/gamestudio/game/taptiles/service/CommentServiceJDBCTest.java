package sk.tuke.gamestudio.game.taptiles.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import sk.tuke.gamestudio.game.taptiles.entity.Comment;

import java.util.List;

public class CommentServiceJDBCTest {

    private CommentServiceJDBC commentService;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceJDBC();
    }

    @Test
    public void testAddComment() {
        Comment comment = new Comment("taptiles", "player1", "Great game!", new java.util.Date());
        commentService.addComment(comment);

        List<Comment> comments = commentService.getComments("taptiles");
        assertFalse(comments.isEmpty());
        assertEquals("Great game!", comments.get(0).getComment());
    }

    @Test
    public void testUpdateComment() {
        Comment comment = new Comment("taptiles", "player1", "Great game!", new java.util.Date());
        commentService.addComment(comment);

        comment.setComment("Amazing game!");
        commentService.addComment(comment);

        List<Comment> comments = commentService.getComments("taptiles");
        assertEquals("Amazing game!", comments.get(0).getComment());
    }

    @Test
    public void testGetComments() {
        Comment comment1 = new Comment("taptiles", "player1", "Great game!", new java.util.Date());
        Comment comment2 = new Comment("taptiles", "player2", "Not bad.", new java.util.Date());
        commentService.addComment(comment1);
        commentService.addComment(comment2);

        List<Comment> comments = commentService.getComments("taptiles");
        assertEquals(2, comments.size());
    }

    @Test
    public void testResetComments() {
        commentService.reset();
        List<Comment> comments = commentService.getComments("taptiles");
        assertTrue(comments.isEmpty());
    }
}