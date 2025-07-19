package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Rating;

import java.util.List;

public interface RatingService {
    void setRating(Rating rating) throws RatingException;
    int getAverageRating(String game) throws RatingException;
    List<Rating> getRatings(String game) throws RatingException;
    int getRating(String game, String player) throws RatingException;
    void reset() throws RatingException;
}
