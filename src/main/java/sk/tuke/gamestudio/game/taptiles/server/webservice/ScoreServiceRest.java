package sk.tuke.gamestudio.game.taptiles.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.game.taptiles.entity.Score;
import sk.tuke.gamestudio.game.taptiles.service.ScoreService;
import sk.tuke.gamestudio.game.taptiles.service.ScoreException;

import java.util.List;

@RestController
@RequestMapping("/api/score")
public class ScoreServiceRest {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{game}")
    public List<Score> getTopScores(@PathVariable String game) throws ScoreException {
        return scoreService.getTopScores(game);
    }

    @PostMapping
    public void addScore(@RequestBody Score score) throws ScoreException {
        scoreService.addScore(score);
    }

    @PostMapping("/reset")
    public void reset() throws ScoreException {
        scoreService.reset();
    }
}
