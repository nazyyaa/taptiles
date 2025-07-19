package sk.tuke.gamestudio.game.taptiles.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import sk.tuke.gamestudio.game.taptiles.entity.Score;

import java.util.List;


public class ScoreServiceJDBCTest {

    private ScoreServiceJDBC scoreService;

    @BeforeEach
    public void setUp() {
        scoreService = new ScoreServiceJDBC();
    }

    @Test
    public void testAddScore() {
        Score score = new Score("taptiles", "player1", 10, new java.util.Date());
        scoreService.addScore(score);

        List<Score> scores = scoreService.getTopScores("taptiles");
        assertFalse(scores.isEmpty());
        assertEquals(10, scores.get(0).getPoints());
    }

    @Test
    public void testGetTopScores() {
        Score score1 = new Score("taptiles", "player1", 10, new java.util.Date());
        Score score2 = new Score("taptiles", "player2", 20, new java.util.Date());
        scoreService.addScore(score1);
        scoreService.addScore(score2);

        List<Score> scores = scoreService.getTopScores("taptiles");
        assertEquals(2, scores.size());
        assertEquals("player1", scores.get(0).getPlayer());
    }

    @Test
    public void testResetScores() {
        scoreService.reset();
        List<Score> scores = scoreService.getTopScores("taptiles");
        assertTrue(scores.isEmpty());
    }
}