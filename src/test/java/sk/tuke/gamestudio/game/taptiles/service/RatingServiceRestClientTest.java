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
import sk.tuke.gamestudio.game.taptiles.entity.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceRestClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    RatingServiceRestClient ratingService;

    @Test
    void getAverageRating_Success() {
        when(restTemplate.getForEntity("http://localhost:8080/api/rating/average/game", Integer.class))
                .thenReturn(new ResponseEntity<>(4, HttpStatus.OK));

        int averageRating = ratingService.getAverageRating("game");

        assertEquals(4, averageRating);
    }

    @Test
    void getAverageRating_NoRatings() {
        when(restTemplate.getForEntity("http://localhost:8080/api/rating/average/game", Integer.class))
                .thenReturn(new ResponseEntity<>(0, HttpStatus.OK));

        int averageRating = ratingService.getAverageRating("game");

        assertEquals(0, averageRating);
    }

    @Test
    void getAverageRating_ServerError() {
        when(restTemplate.getForEntity("http://localhost:8080/api/rating/average/game", Integer.class))
                .thenThrow(new RestClientException("Server error"));

        assertThrows(RestClientException.class, () -> ratingService.getAverageRating("game"));
    }

    @Test
    void getRatings_Success() {
        Rating[] ratings = {new Rating("game", "player", 5, new java.util.Date())};
        when(restTemplate.getForEntity("http://localhost:8080/api/rating/game", Rating[].class))
                .thenReturn(new ResponseEntity<>(ratings, HttpStatus.OK));

        List<Rating> result = ratingService.getRatings("game");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void getRatings_NoRatings() {
        when(restTemplate.getForEntity("http://localhost:8080/api/rating/game", Rating[].class))
                .thenReturn(new ResponseEntity<>(new Rating[0], HttpStatus.OK));

        List<Rating> result = ratingService.getRatings("game");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRatings_ServerError() {
        when(restTemplate.getForEntity("http://localhost:8080/api/rating/game", Rating[].class))
                .thenThrow(new RestClientException("Server error"));

        assertThrows(RestClientException.class, () -> ratingService.getRatings("game"));
    }
}