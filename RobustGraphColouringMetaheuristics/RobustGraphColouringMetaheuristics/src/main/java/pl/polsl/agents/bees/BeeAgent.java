package pl.polsl.agents.bees;

import lombok.Getter;
import lombok.Setter;
import pl.polsl.enums.BeeAgentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BeeAgent {
    private final BeeAgentType type;
    private String currentVertex;
    private String hiveVertex;
    private Integer currentDistance = 0;
    private List<String> visitedVertexMemory = new ArrayList<>();
    private Map<String, Integer> feedingRegionInformation;

    public void memorizeNewVisitedVertex(String vertex) {
        if(!visitedVertexMemory.contains(vertex))
            this.visitedVertexMemory.add(vertex);
    }

    public void updateNeighbourhoodInformation(String vertex, Integer colour) {
        if(this.feedingRegionInformation.containsKey(vertex)){
            this.feedingRegionInformation.replace(vertex, colour);
        } else {
            this.feedingRegionInformation.put(vertex, colour);
        }
    }

    public BeeAgent(BeeAgentType type, BeesHive hive){
        this.type = type;
        this.feedingRegionInformation = hive.getFeedingRegionInformation();
        this.hiveVertex = hive.getLocationVertex();
        this.currentVertex = hive.getLocationVertex();
        memorizeNewVisitedVertex(hive.getLocationVertex());
    }
}
