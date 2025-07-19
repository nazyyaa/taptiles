package sk.tuke.gamestudio.game.taptiles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.taptiles.entity.Score;

import java.util.Arrays;
import java.util.List;

public class ScoreServiceRestClient implements ScoreService {
    private final String url = "http://localhost:8080/api/score";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public int getScore(String game, String username) throws ScoreException {
        try {
            String fullUrl = url + "/current?game=" + game + "&username=" + username;
            return restTemplate.getForObject(fullUrl, Integer.class);
        } catch (Exception e) {
            throw new ScoreException("Error while fetching score from REST service", e);
        }
    }

    @Override
    public void addScore(Score score) {
        restTemplate.postForEntity(url, score, Score.class);
    }

    @Override
    public List<Score> getTopScores(String gameName) {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + gameName, Score[].class).getBody());
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}