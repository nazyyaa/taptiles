package sk.tuke.gamestudio.game.taptiles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import sk.tuke.gamestudio.game.taptiles.entity.Score;
import sk.tuke.gamestudio.game.taptiles.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

@Transactional
public class UserServiceJPA implements UserService {

    @Autowired
    private ScoreService scoreService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(User user) {
        try {
            List<User> existingUsers = entityManager.createQuery(
                            "select u from User u where u.username = :username", User.class)
                    .setParameter("username", user.getUsername())
                    .getResultList();

            if (!existingUsers.isEmpty()) {
                throw new IllegalArgumentException("User already exists");
            }
            if (user.getGame() == null) {
                user.setGame("taptiles");
            }

            if (user.getLastLogin() == null) {
                user.setLastLogin(new Date());
            }

            entityManager.persist(user);
            scoreService.addScore(new Score("taptiles", user.getUsername(), 0, new Date()));

        } catch (Exception e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    @Override
    public boolean loginUser(User user) {
        try {
            User existingUser = entityManager
                    .createQuery("select u from User u where u.username = :username", User.class)
                    .setParameter("username", user.getUsername())
                    .getSingleResult();

            return existingUser.getPassword().equals(user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean changeUsername(String currentUsername, String newUsername) {
        try {
            List<User> users = entityManager.createQuery(
                            "select u from User u where u.username = :username", User.class)
                    .setParameter("username", newUsername)
                    .getResultList();
            if (!users.isEmpty()) {
                return false;
            }

            User user = entityManager.createQuery(
                            "select u from User u where u.username = :username", User.class)
                    .setParameter("username", currentUsername)
                    .getSingleResult();

            user.setUsername(newUsername);
            entityManager.merge(user);
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean changePassword(String username, String newPassword) {
        try {
            User user = entityManager.createQuery(
                            "select u from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            user.setPassword(newPassword);
            entityManager.merge(user);
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}