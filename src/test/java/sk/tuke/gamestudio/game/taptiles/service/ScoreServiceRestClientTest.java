package sk.tuke.gamestudio.game.taptiles.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.taptiles.entity.Score;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceRestClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ScoreServiceRestClient scoreService;

    @Test
    void getTopScores_Success() {
        Score[] scores = {new Score("game", "player1", 100, new Date()), new Score("game", "player2", 200, new Date())};
        when(restTemplate.getForEntity("http://localhost:8080/api/score/game", Score[].class))
                .thenReturn(new ResponseEntity<>(scores, HttpStatus.OK));

        List<Score> result = scoreService.getTopScores("game");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("player1", result.get(0).getPlayer());
        assertEquals(100, result.get(0).getPoints());
    }

    @Test
    void getTopScores_NoScores() {
        when(restTemplate.getForEntity("http://localhost:8080/api/score/game", Score[].class))
                .thenReturn(new ResponseEntity<>(new Score[0], HttpStatus.OK));

        List<Score> result = scoreService.getTopScores("game");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTopScores_ServerError() {
        when(restTemplate.getForEntity("http://localhost:8080/api/score/game", Score[].class))
                .thenThrow(new RestClientException("Server error"));

        assertThrows(RestClientException.class, () -> scoreService.getTopScores("game"));
    }
}