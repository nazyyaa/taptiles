package sk.tuke.gamestudio.game.taptiles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.taptiles.entity.User;

import java.util.HashMap;
import java.util.Map;

public class UserServiceRestClient implements UserService {

    private final String url = "http://localhost:8080/api/users";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addUser(User user) {
        try {
            restTemplate.postForEntity(url + "/register", user, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error registering user", e);
        }
    }

    @Override
    public boolean loginUser(User user) {
        try {
            String response = restTemplate.postForObject(url + "/login", user, String.class);
            return response != null && response.equals("Login successful");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean changeUsername(String currentUsername, String newUsername) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("newUsername", newUsername);

            restTemplate.postForEntity(url + "/change-username", request, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean changePassword(String username, String newPassword) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("newPassword", newPassword);

            restTemplate.postForEntity(url + "/change-password", request, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}