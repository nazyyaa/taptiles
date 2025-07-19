package sk.tuke.gamestudio.game.taptiles.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.game.taptiles.entity.Rating;
import sk.tuke.gamestudio.game.taptiles.service.RatingService;
import sk.tuke.gamestudio.game.taptiles.service.RatingException;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/average/{game}")
    public int getAverageRating(@PathVariable String game) throws RatingException {
        return ratingService.getAverageRating(game);
    }

    @GetMapping("/{game}")
    public List<Rating> getRatings(@PathVariable String game) throws RatingException {
        return ratingService.getRatings(game);
    }

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) throws RatingException {
        return ratingService.getRating(game, player);
    }

    @PostMapping
    public void setRating(@RequestBody Rating rating) throws RatingException {
        ratingService.setRating(rating);
    }

    @PostMapping("/reset")
    public void reset() throws RatingException {
        ratingService.reset();
    }
}