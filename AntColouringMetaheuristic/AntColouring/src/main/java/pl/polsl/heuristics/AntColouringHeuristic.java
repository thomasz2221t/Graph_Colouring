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
import java.util.stream.Collectors;

public class AntColouringHeuristic {
    public final int NUMBER_OF_AGENTS = 3;
    public final float PROPORTION_EDGES_TO_FUZZ = 0.33f;
    public final float LOWER_BOUNDARY_OF_UNCERTAINTY = 0.60f;
    public final int MINIMAL_ROBUST_COLOUR_NUMBER = 1;
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    private Map<String, Integer> verticesColourMap = new HashMap<>();
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    private Map<CustomWeightedEdge, Double> pheromoneMap = new HashMap<>();
    private List<AntAgent> ants = new ArrayList<>();
    private Double robustness = Double.valueOf(0);
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
        //Przygotowanie listy colorow c
        this.initColourList();
        //Przygotowanie mapy feromonu
        this.initPheromoneMap(this.graph, this.pheromoneMap);
        //spawn mrówek
        this.initAnts(this.ants, this.NUMBER_OF_AGENTS);

        long i = 0;
        //pętla while
        while((i < AntColouringConstants.AntColouringMaxIterations) || (robustness < AntColouringConstants.AntColouringMinRobustness)) {
            for(int k=0; k < this.ants.size(); k++) {
                //random assign of ant to node
                AntAgent ant = ants.get(k);
                String vertexToVisit = customWeightedGraphHelper.getRandomVertexFromGraph(this.graph);
                ant.setCurrentVertex(vertexToVisit);
                ant.memorizeNewVisitedVertex(vertexToVisit);
                //====
                //tablica lokalnych przejść mrówki
                Map<String, CustomWeightedEdge> vertexNeighbourhoodList = customWeightedGraphHelper.
                        getNeighbourhoodListOfVertex(this.graph, ant.getCurrentVertex());
                //Nadaj kolor
                this.assignColourToVertex(ant, vertexNeighbourhoodList);
                //====
                //obliczenie wag przejscia
                Map<String, Double> passingProbabilitesMap = this.calculateAntPassingProbabilities(ant, vertexNeighbourhoodList);
                //====
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

    private Map<CustomWeightedEdge, Double> initPheromoneMap(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<CustomWeightedEdge, Double> pheromoneMap) {
        List<CustomWeightedEdge> edgeList = graph.edgeSet().stream().toList();
        for (CustomWeightedEdge edge: edgeList) {
            pheromoneMap.put(edge, 0.0);
        }
        return pheromoneMap;
    }

    private List<AntAgent> initAnts(List<AntAgent> ants, int numberOfAgents) {
        for (int i=0; i<numberOfAgents; i++) {
            ants.add(new AntAgent());
        }
        return ants;
    }

    private void initColourList() {
        for(int i=0; i<=MINIMAL_ROBUST_COLOUR_NUMBER; i++)
            this.coloursMap.put(i,0);
    }

    private void assignColourToVertex(AntAgent ant, Map<String, CustomWeightedEdge> vertexNeighbourhoodList) {
        Integer minimalColourIndex = -1;
        Integer minimalColourPresence = this.graph.vertexSet().size();
        for (Integer colourIndex : this.coloursMap.keySet()) {
            if(colourIndex == 0)
                continue;
            if(vertexNeighbourhoodList.get(colourIndex) == null) {//colour is free
                if(this.coloursMap.get(colourIndex) < minimalColourPresence) {
                    minimalColourPresence = this.coloursMap.get(colourIndex);
                    minimalColourIndex = colourIndex;
                }
            }
        }
        if(minimalColourIndex == -1) {//out of minimal colour, add new color
            this.coloursMap.put(coloursMap.keySet().size(), 1);
            minimalColourIndex = coloursMap.keySet().size();
        }
       this.verticesColourMap.replace(ant.currentVertex, minimalColourIndex);
    }

    private Map<String, Double> calculateAntPassingProbabilities(AntAgent ant,  Map<String, CustomWeightedEdge> vertexNeighbourhoodList) {
        List<String> antMemory = ant.getVisitedVertexMemory();
        final Map<CustomWeightedEdge, Double> pheromone = this.pheromoneMap;
        Map<String, Double> passingProbabilities = new HashMap<>();
        for (String vertex: vertexNeighbourhoodList.keySet()) {
            //obliczenie informacji heurystycznej

            //obliczenie feromonu
            if(antMemory.contains(vertex)){ //visited

            }
        }
        return passingProbabilities;
    }

    public AntColouringHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
