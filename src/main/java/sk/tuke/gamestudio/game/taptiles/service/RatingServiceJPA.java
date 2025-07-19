package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Rating;

import java.util.List;
import javax.persistence.*;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            List<Rating> existingRatings = entityManager.createQuery("select r from Rating r where r.game = :game  and r.player = :player", Rating.class)
                    .setParameter("game", rating.getGame())
                    .setParameter("player", rating.getPlayer())
                    .getResultList();

            if (existingRatings.isEmpty()) {
                entityManager.persist(rating);
            } else {
                Rating existingRating = existingRatings.get(0);
                existingRating.setRating(rating.getRating());
                existingRating.setRatedOn(rating.getRatedOn());
                entityManager.merge(existingRating);
            }
        } catch (Exception e) {
            throw new RatingException("setRating error", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            Query query = entityManager.createQuery("select avg(r.rating) from Rating r where r.game = :game")
                    .setParameter("game", game);
            return ((Number) query.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Rating> getRatings(String game) throws RatingException {
        try {
            return entityManager.createQuery("SELECT r FROM Rating r WHERE r.game = :game order by r.ratedOn desc", Rating.class)
                    .setParameter("game", game)
                    .getResultList();
        } catch (Exception e) {
            throw new RatingException("Error retrieving ratings for game " + game, e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            Query query = entityManager.createQuery("select r.rating from Rating r where r.game = :game AND r.player = :player")
                    .setParameter("game", game)
                    .setParameter("player", player);
            return ((Number) query.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void reset() throws RatingException {
        try {
            entityManager.createNativeQuery("DELETE FROM rating").executeUpdate();
        } catch (Exception e) {
            throw new RatingException("reset rating error", e);
        }
    }
}
