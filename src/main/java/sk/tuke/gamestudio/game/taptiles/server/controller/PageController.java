package sk.tuke.gamestudio.game.taptiles.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.tuke.gamestudio.game.taptiles.entity.Comment;
import sk.tuke.gamestudio.game.taptiles.entity.Rating;
import sk.tuke.gamestudio.game.taptiles.entity.Score;
import sk.tuke.gamestudio.game.taptiles.service.CommentService;
import sk.tuke.gamestudio.game.taptiles.service.RatingService;
import sk.tuke.gamestudio.game.taptiles.service.ScoreService;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @GetMapping("/")
    public String home() {
        return "redirect:/taptiles";
    }

    @GetMapping("/taptiles")
    public String taptiles() {
        return "taptiles";
    }

    @GetMapping("/taptiles/leaderboard")
    public String leaderboard(Model model) {
        List<Score> topScores = scoreService.getTopScores("taptiles");
        model.addAttribute("topPlayers", topScores);
        return "taptiles/leaderboard";
    }

    @GetMapping("/taptiles/comment")
    public String comment(Model model) {
        List<Comment> comments = commentService.getComments("taptiles");

        model.addAttribute("comments", comments);
        return "taptiles/comment";
    }


    @GetMapping("/taptiles/rating")
    public String rating(Model model) {
        List<Rating> ratings = ratingService.getRatings("taptiles");
        int averageRating = ratingService.getAverageRating("taptiles");
        model.addAttribute("ratings", ratings);
        model.addAttribute("averageRating", averageRating);
        return "taptiles/rating";
    }

    @RequestMapping(value = "/{[path:[^\\.]*}")
    public String redirect() {
        return "redirect:/taptiles";
    }

    @RequestMapping("/**/{path:[^\\.]*}")
    public String fallback() {
        return "redirect:/taptiles";
    }
}
