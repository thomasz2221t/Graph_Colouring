package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.CuckooSearchConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;

import java.util.HashMap;
import java.util.Map;

public class CuckooSearchHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    private Double robustness = Double.valueOf(100);
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph() {

        long i = 0;
        while(i < CuckooSearchConstants.CUCKOO_SEARCH_MAX_ITERATIONS) {

            i++;
        }
        return this.verticesColourMap;
    }

    public CuckooSearchHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
