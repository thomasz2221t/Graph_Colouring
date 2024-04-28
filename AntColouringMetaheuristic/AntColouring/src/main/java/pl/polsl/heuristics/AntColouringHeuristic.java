package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

public class AntColouringHeuristic {
    public final int NUMBER_OF_AGENTS = 3;
    public final float PROPORTION_EDGES_TO_FUZZ = 0.33f;
    public final float LOWER_BOUNDARY_OF_UNCERTAINTY = 0.60f;
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> colourTheGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        prepareGraph(graph, PROPORTION_EDGES_TO_FUZZ, LOWER_BOUNDARY_OF_UNCERTAINTY);//przygotowanie grafu
        customWeightedGraphHelper.savingGraphVisualizationToFile(graph, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"uncertainty");
            //Losowy wybór krawędzi które będą miały zmienione losowo wagi

        //spawn mrówek

        //
        return graph;
    }

    private DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> prepareGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, float proportionOfEdgesToFuzz, float lowerBoundaryOfUncertainty){
        customWeightedGraphHelper.imposeUncertaintyToGraph(graph, proportionOfEdgesToFuzz, lowerBoundaryOfUncertainty);

        return graph;
    }

    private void createAnt() {

    }

    public AntColouringHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
