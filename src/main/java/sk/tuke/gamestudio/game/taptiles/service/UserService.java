package sk.tuke.gamestudio.game.taptiles.service;

import sk.tuke.gamestudio.game.taptiles.entity.User;

public interface UserService {
    void addUser(User user);
    boolean loginUser(User user);
    boolean changeUsername(String currentUsername, String newUsername);
    boolean changePassword(String username, String newPassword);
}
