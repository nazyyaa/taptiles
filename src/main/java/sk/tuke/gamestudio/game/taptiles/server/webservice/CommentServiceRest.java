package sk.tuke.gamestudio.game.taptiles.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.game.taptiles.entity.Comment;
import sk.tuke.gamestudio.game.taptiles.service.CommentService;
import sk.tuke.gamestudio.game.taptiles.service.CommentException;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentServiceRest {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{game}")
    public List<Comment> getComments(@PathVariable String game) throws CommentException {
        return commentService.getComments(game);
    }

    @PostMapping
    public void addComment(@RequestBody Comment comment) throws CommentException {
        commentService.addComment(comment);
    }

    @PostMapping("/reset")
    public void reset() throws CommentException {
        commentService.reset();
    }
}
