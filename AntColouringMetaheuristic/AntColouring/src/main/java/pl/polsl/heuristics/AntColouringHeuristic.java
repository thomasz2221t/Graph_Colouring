package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.AntAgent;
import pl.polsl.constants.AntColouringConstants;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntColouringHeuristic {
    public final int NUMBER_OF_AGENTS = 3;
    public final float PROPORTION_EDGES_TO_FUZZ = 0.33f;
    public final float LOWER_BOUNDARY_OF_UNCERTAINTY = 0.60f;
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    private Map<String, Integer> verticesColourMap = new HashMap<>();
    private Map<String, Double> pheromoneMap = new HashMap<>();
    private List<AntAgent> ants = new ArrayList<>();
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> colourTheGraph() {
        ////////////////////////////////
        /////Przygotowanie zasobów/////
        //////////////////////////////
        //przygotowanie grafu
        this.graph = customWeightedGraphHelper.imposeUncertaintyToGraph(this.graph, PROPORTION_EDGES_TO_FUZZ, LOWER_BOUNDARY_OF_UNCERTAINTY);//Losowy wybór krawędzi które będą miały zmienione losowo wagi
        //only for testing
        customWeightedGraphHelper.savingGraphVisualizationToFile(this.graph, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"uncertainty.png");
        //Przygotowanie mapy koloru Map<V,Integer> (Interface VertexColoringAlgorithm.Coloring<V>)
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        //Przygotowanie mapy feromonu
        this.initPheromoneMap(this.graph, this.pheromoneMap);
        //spawn mrówek
        this.initAnts(this.ants, this.NUMBER_OF_AGENTS);

        long i = 0;
        //pętla while
        while(i < AntColouringConstants.AntColouringConstants) {
            for(int k=0; k < this.ants.size(); k++) {
                //random assign of ant to node
                //while coloring isnt valid select higher probability node
            }
            //local search
            i++;
        }
        return graph;
    }

    private Map<String, Integer> initVerticesColourMap(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap) {
        List<String> vertexList = graph.vertexSet().stream().toList();
        //Integer colourIndex = 0;
        for (String vertex: vertexList) {
            verticesColourMap.put(vertex, 0);
            //colourIndex++;
        }
        return verticesColourMap;
    }

    private Map<String, Double> initPheromoneMap(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Double> pheromoneMap) {
        List<String> vertexList = graph.vertexSet().stream().toList();
        for (String vertex: vertexList) {
            pheromoneMap.put(vertex, 0.0);
        }
        return pheromoneMap;
    }

    private List<AntAgent> initAnts(List<AntAgent> ants, int numberOfAgents) {
        for (int i=0; i<numberOfAgents; i++) {
            ants.add(new AntAgent());
        }
        return ants;
    }

    private void createAnt() {

    }

    public AntColouringHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
