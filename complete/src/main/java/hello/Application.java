package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        IRecommendation engine = RecommendationEngine.getInstance();
        engine.buildRecommendations("listens.json");

        //Simple input
//        engine.buildRecommendations("listens-simple.json");
    }
}
