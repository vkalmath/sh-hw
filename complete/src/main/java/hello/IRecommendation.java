package hello;

/**
 * Created by veereshkalmath on 30/04/17.
 */
public interface IRecommendation {

    public void buildRecommendations(String fromJson) ;

    public String getRecommendation(String meditationId);
}
