package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.BeeRoutingConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.HashMap;
import java.util.Map;

public class BeeRoutingHeuristic extends AbstractColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();

    public Map<String, Integer> colourTheGraph() {

        long i = 0;
        while(i < BeeRoutingConstants.BEE_COLOURING_MAX_ITERATIONS) {

            i++;
        }

        return this.verticesColourMap;
    }

    public BeeRoutingHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
