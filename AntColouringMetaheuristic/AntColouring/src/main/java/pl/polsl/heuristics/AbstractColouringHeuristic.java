package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.graphs.CustomWeightedGraphHelper;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractColouringHeuristic {

    protected double calculateRobustness(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap) {
        double graphPenaltiesSum = 0.0;
        for(CustomWeightedGraphHelper.CustomWeightedEdge edge : graph.edgeSet()) {
            Integer sourceVertexColour = verticesColourMap.get(graph.getEdgeSource(edge));
            Integer targetVertexColour = verticesColourMap.get(graph.getEdgeTarget(edge));
            if((graph.getEdgeWeight(edge) < 1.0) && (Objects.equals(sourceVertexColour, targetVertexColour))) {
                graphPenaltiesSum += graph.getEdgeWeight(edge);
            }
        }
        return graphPenaltiesSum;
    }

}
