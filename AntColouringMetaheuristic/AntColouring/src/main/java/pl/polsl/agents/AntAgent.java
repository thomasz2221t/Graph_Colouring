package pl.polsl.agents;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AntAgent {
    public String currentVertex;

    public List<String> visitedVertexMemory = new ArrayList<>();

    public void memorizeNewVisitedVertex(String vertex) {
        this.visitedVertexMemory.add(vertex);
    }
}
