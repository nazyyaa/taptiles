package sk.tuke.gamestudio.game.taptiles.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.game.taptiles.entity.User;
import sk.tuke.gamestudio.game.taptiles.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserServiceRest {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user, HttpSession session) {
        boolean loginSuccess = userService.loginUser(user);
        if (loginSuccess) {
            session.setAttribute("user", user);
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logout successful";
    }

    @GetMapping("/session")
    public String sessionInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "User logged in: " + user.getUsername();
        } else {
            return "User not logged in";
        }
    }

    @PostMapping("/change-username")
    public ResponseEntity<String> changeUsername(
            @RequestBody Map<String,String> body,
            HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Not logged in");
        }

        String newUsername = body.get("newUsername");
        if (newUsername == null || newUsername.length() < 3) {
            return ResponseEntity.badRequest()
                    .body("Username must be at least 3 characters");
        }

        boolean ok = userService.changeUsername(
                sessionUser.getUsername(), newUsername);

        if (ok) {
            sessionUser.setUsername(newUsername);
            session.setAttribute("user", sessionUser);
            return ResponseEntity.ok("Username changed");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already taken");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody Map<String,String> body,
            HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Not logged in");
        }

        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 3) {
            return ResponseEntity.badRequest()
                    .body("Password must be at least 3 characters");
        }

        boolean ok = userService.changePassword(
                sessionUser.getUsername(), newPassword);

        if (ok) {
            return ResponseEntity.ok("Password changed");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to change password");
        }
    }
}
