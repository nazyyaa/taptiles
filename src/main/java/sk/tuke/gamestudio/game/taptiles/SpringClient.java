package sk.tuke.gamestudio.game.taptiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.game.taptiles.service.*;
import sk.tuke.gamestudio.game.taptiles.consoleui.ConsoleUI;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "sk.tuke.gamestudio.game.taptiles.server.*"))
public class SpringClient {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner runner(ConsoleUI ui) {
        return args -> ui.motdGameLogin();
    }

    @Bean
    public CommentService commentService() {
//        return new CommentServiceJPA();
        return new CommentServiceRestClient();
    }

    @Bean
    public RatingService ratingService() {
//        return new RatingServiceJPA();
        return new RatingServiceRestClient();
    }

    @Bean
    public ScoreService scoreService() {
//        return new ScoreServiceJPA();
        return new ScoreServiceRestClient();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
