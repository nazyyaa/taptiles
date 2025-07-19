package sk.tuke.gamestudio.game.taptiles.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import sk.tuke.gamestudio.game.taptiles.entity.Rating;

public class RatingServiceJDBCTest {

    private RatingServiceJDBC ratingService;

    @BeforeEach
    public void setUp() {
        ratingService = new RatingServiceJDBC();
    }

    @Test
    public void testSetRating() {
        Rating rating = new Rating("taptiles", "player1", 5, new java.util.Date());
        ratingService.setRating(rating);

        assertEquals(5, ratingService.getRating("game1", "player1"));
    }

    @Test
    public void testGetAverageRating() {
        Rating rating1 = new Rating("taptiles", "player1", 5, new java.util.Date());
        Rating rating2 = new Rating("taptiles", "player2", 3, new java.util.Date());
        ratingService.setRating(rating1);
        ratingService.setRating(rating2);

        assertEquals(4, ratingService.getAverageRating("taptiles"));
    }

    @Test
    public void testGetRatingForPlayer() {
        Rating rating = new Rating("taptiles", "player1", 4, new java.util.Date());
        ratingService.setRating(rating);

        assertEquals(4, ratingService.getRating("taptiles", "player1"));
    }
}