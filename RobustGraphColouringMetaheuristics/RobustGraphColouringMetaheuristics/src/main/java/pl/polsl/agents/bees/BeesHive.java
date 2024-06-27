package pl.polsl.agents.bees;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BeesHive {
    private String locationVertex;
    private Map<String, Integer> feedingRegionInformation;

    public void updateFeedingRegionInformation(Map<String, Integer> feedingRegionInformation){
        for (String vertex : feedingRegionInformation.keySet()) {
            if(this.feedingRegionInformation.containsKey(vertex)){
                this.feedingRegionInformation.replace(vertex, feedingRegionInformation.get(vertex));
            } else {
                this.feedingRegionInformation.put(vertex, feedingRegionInformation.get(vertex));
            }
        }
    }

    public BeesHive(String locationVertex, Map<String, Integer> foodRegionInformation){
        this.locationVertex = locationVertex;
        this.feedingRegionInformation = foodRegionInformation;
    }
}
