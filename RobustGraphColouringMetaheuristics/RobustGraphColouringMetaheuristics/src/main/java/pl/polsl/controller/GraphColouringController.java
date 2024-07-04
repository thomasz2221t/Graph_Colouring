package pl.polsl.controller;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.metaheuristics.AntColouringHeuristic;
import pl.polsl.metaheuristics.BeeColouringHeuristic;
import pl.polsl.metaheuristics.CuckooSearchHeuristic;
import pl.polsl.metaheuristics.StorkFeedingHeuristic;

import java.util.Map;

public class GraphColouringController {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph;
    private AntColouringHeuristic antColouringHeuristic;
    private CuckooSearchHeuristic cuckooSearchHeuristic;
    private BeeColouringHeuristic beeColouringHeuristic;
    private StorkFeedingHeuristic storkFeedingHeuristic;

    public Map<String, Integer> runAntColouring() {
        return this.antColouringHeuristic.colourTheGraph();
    }

    public Map<String, Integer> runCuckooSearch() {
        return  this.cuckooSearchHeuristic.colourTheGraph();
    }

    public Map<String, Integer> runBeeColouring() {
        return this.beeColouringHeuristic.colourTheGraph();
    }

    public Map<String, Integer> runStorkFeedingColouring() {
        return this.storkFeedingHeuristic.colourTheGraph();
    }

    public GraphColouringController(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graph = graph;
        this.antColouringHeuristic = new AntColouringHeuristic(graph);
        this.cuckooSearchHeuristic = new CuckooSearchHeuristic(graph);
        this.beeColouringHeuristic = new BeeColouringHeuristic(graph);
        this.storkFeedingHeuristic = new StorkFeedingHeuristic(graph);
    }
}
