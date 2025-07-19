package sk.tuke.gamestudio.game.taptiles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.taptiles.entity.Rating;

import java.util.Arrays;
import java.util.List;

public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void setRating(Rating rating) {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public int getAverageRating(String game) {
        return restTemplate.getForEntity(url + "/average/" + game, Integer.class).getBody();
    }

    @Override
    public List<Rating> getRatings(String game) {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + game, Rating[].class).getBody());
    }

    @Override
    public int getRating(String game, String player) {
        return restTemplate.getForEntity(url + "/{game}/{player}", Integer.class, game, player).getBody();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}
