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
    public final BeeAgentType type;
    private String currentVertex;
    private String hiveVertex;
    private Integer currentDistance;
    private List<String> visitedVertexMemory = new ArrayList<>();
    private Map<String, Integer> neighbourhoodInformation;

    public void memorizeNewVisitedVertex(String vertex) {
        this.visitedVertexMemory.add(vertex);
    }

    public void addNeighbourhoodInformation(String vertex, Integer colour) {
        if(this.neighbourhoodInformation.containsKey(vertex)){
            this.neighbourhoodInformation.replace(vertex, colour);
        }
    }

    public BeeAgent(BeeAgentType type, Map<String, Integer> neighbourhoodInformation){
        this.type = type;
        this.neighbourhoodInformation = neighbourhoodInformation;
    }
}
