package sk.tuke.gamestudio.game.taptiles.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter

@Entity
@Table(name = "\"user\"")
public class User implements Serializable {
    @Id
    @GeneratedValue
    private int ident;

    private String game;
    private String username;
    private String password;
    private Date lastLogin;

    public User() {}

    public User(String game, String username, String password, Date lastLogin) {
        this.game = game;
        this.username = username;
        this.password = password;
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "game='" + game + '\'' +
                ", username='" + username + '\'' +
                ", password=" + password +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
