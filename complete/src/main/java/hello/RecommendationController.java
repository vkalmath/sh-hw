package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.log4j2.Log4J2LoggingSystem;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/recommendations")
    public String recommendations(@RequestParam(value="subtopic") String subTopicId) {
        logger.debug("subtopicId =>" + subTopicId);

        IRecommendation engine = RecommendationEngine.getInstance();

        return engine.getRecommendation(subTopicId);
    }
}
