package sk.tuke.gamestudio.game.taptiles.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.taptiles.entity.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceRestClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    CommentServiceRestClient commentService;

    @Test
    @DisplayName("getComments - Success")
    void getComments_Success() {
        Comment[] comments = {new Comment("game", "player", "Nice game!", null)};
        when(restTemplate.getForEntity("http://localhost:8080/api/comment/game", Comment[].class))
                .thenReturn(new ResponseEntity<>(comments, HttpStatus.OK));

        List<Comment> result = commentService.getComments("game");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nice game!", result.get(0).getComment());
    }

    @Test
    @DisplayName("getComments - No Comments")
    void getComments_NoComments() {
        when(restTemplate.getForEntity("http://localhost:8080/api/comment/game", Comment[].class))
                .thenReturn(new ResponseEntity<>(new Comment[0], HttpStatus.OK));

        List<Comment> result = commentService.getComments("game");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getComments - Server Error")
    void getComments_ServerError() {
        when(restTemplate.getForEntity("http://localhost:8080/api/comment/game", Comment[].class))
                .thenThrow(new RestClientException("Server error"));

        assertThrows(RestClientException.class, () -> commentService.getComments("game"));
    }
}