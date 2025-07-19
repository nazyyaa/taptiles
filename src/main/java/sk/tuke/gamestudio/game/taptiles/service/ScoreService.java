package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Score;

import java.util.List;

public interface ScoreService {
    int getScore(String game, String username) throws ScoreException;
    void addScore(Score score) throws ScoreException;
    List<Score> getTopScores(String game) throws ScoreException;
    void reset() throws ScoreException;
}
