package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.Comment;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class CommentServiceJPA implements CommentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        try {
            List<Comment> existingComments = entityManager.createQuery("select c from Comment c where c.game = :game and c.player = :player", Comment.class)
                    .setParameter("game", comment.getGame())
                    .setParameter("player", comment.getPlayer())
                    .getResultList();

            if (existingComments.isEmpty()) {
                entityManager.persist(comment);
            } else {
                Comment existingComment = existingComments.get(0);
                existingComment.setComment(comment.getComment());
                existingComment.setCommentedOn(comment.getCommentedOn());
                entityManager.merge(existingComment);
            }
        } catch (Exception e) {
            throw new CommentException("addComment error", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        try {
            return entityManager.createQuery("select c from Comment c where c.game = :game order by c.commentedOn desc", Comment.class)
                    .setParameter("game", game)
                    .getResultList();
        } catch (Exception e) {
            throw new CommentException("getComments error", e);
        }
    }

    @Override
    public void reset() throws CommentException {
        try {
            entityManager.createNativeQuery("DELETE FROM comment").executeUpdate();
        } catch (Exception e) {
            throw new CommentException("reset Comment error", e);
        }
    }
}
