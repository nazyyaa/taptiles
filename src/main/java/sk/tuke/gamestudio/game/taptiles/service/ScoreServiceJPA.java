package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Score;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ScoreServiceJPA implements ScoreService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int getScore(String game, String username) throws ScoreException {
        try {
            Integer result = entityManager.createQuery(
                            "SELECT s.points FROM Score s WHERE s.game = :game AND s.player = :player", Integer.class)
                    .setParameter("game", game)
                    .setParameter("player", username)
                    .getSingleResult();

            return result != null ? result : 0;
        } catch (Exception e) {
            throw new ScoreException("getScore error", e);
        }
    }

    @Override
    public void addScore(Score score) throws ScoreException {
        try {
            List<Score> existingScores = entityManager.createQuery("select s from Score s where s.game = :game and s.player = :player", Score.class)
                    .setParameter("game", score.getGame())
                    .setParameter("player", score.getPlayer())
                    .getResultList();

            if (existingScores.isEmpty()) {
                entityManager.persist(score);
            } else {
                Score existingScore = existingScores.get(0);
                int newPoints = existingScore.getPoints() + score.getPoints();
                if (newPoints < 0) newPoints = 0;
                existingScore.setPoints(newPoints);
                existingScore.setPlayedOn(score.getPlayedOn());
                entityManager.merge(existingScore);
            }
        } catch (Exception e) {
            throw new ScoreException("addScore error", e);
        }
    }

    @Override
    public List<Score> getTopScores(String game) throws ScoreException {
        try {
            return entityManager.createQuery("select s from Score s where s.game = :game order by s.points DESC", Score.class)
                    .setParameter("game", game)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            throw new ScoreException("getTopScores error", e);
        }
    }

    @Override
    public void reset() throws ScoreException {
        try {
            entityManager.createNativeQuery("DELETE FROM score").executeUpdate();
        } catch (Exception e) {
            throw new ScoreException("reset error", e);
        }
    }
}
