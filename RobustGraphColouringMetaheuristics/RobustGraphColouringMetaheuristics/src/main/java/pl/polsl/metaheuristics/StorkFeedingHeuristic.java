package pl.polsl.metaheuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.StorkAgent;
import pl.polsl.constants.CuckooSearchConstants;
import pl.polsl.constants.StorkFeedingConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class StorkFeedingHeuristic extends AbstractColouringHeuristic {

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    public List<StorkAgent> storks = new ArrayList<>();
    private Double robustness = 100.0;
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph(){
        this.init();
        long i = 0;

        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        long startTime = System.nanoTime();
        long cpuStartTime = threadMxBean.getCurrentThreadCpuTime();
        while(i < StorkFeedingConstants.STORK_FEEDING_MAX_ITERATIONS) {
            for(int k = 0; k < StorkFeedingConstants.NUMBER_OF_AGENTS; k++) {
                StorkAgent stork = this.storks.get(k);
                this.storkOptimisation(this.graph, this.verticesColourMap, this.coloursMap, stork);
            }
            i++;
        }
        long cpuEndTime = threadMxBean.getCurrentThreadCpuTime();
        long endTime = System.nanoTime();

        this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
        getMetaheuristicsStatistics(this.graph, this.verticesColourMap, robustness, startTime, cpuStartTime, cpuEndTime, endTime);

        return this.verticesColourMap;
    }

    private void init() {
        //Przygotowanie mapy koloru Map<V,Integer> (Interface VertexColoringAlgorithm.Coloring<V>)
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        //Przygotowanie listy colorow c
        this.initColourList(this.coloursMap, this.graph.vertexSet().size(), StorkFeedingConstants.MAXIMAL_ROBUST_COLOUR_NUMBER);
        this.initStorks(this.storks, StorkFeedingConstants.NUMBER_OF_AGENTS);
    }

    private List<StorkAgent> initStorks(List<StorkAgent> storks, int numberOfAgents) {
        for (int i = 0; i < numberOfAgents; i++) {
            StorkAgent stork = new StorkAgent();
            String vertexToVisit = customWeightedGraphHelper.getRandomVertexFromGraph(this.graph);
            stork.setCurrentVertex(vertexToVisit);
            storks.add(stork);
        }
        return storks;
    }

    private double calculatePrecisionFitnessFunction(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<String, CustomWeightedEdge> neighbourhoodList, String currentVertex) {
        Integer currentVertexColour = verticesColourMap.get(currentVertex);
        int correctColours = 0;
        for(String vertex : neighbourhoodList.keySet()) {
            if(Objects.equals(currentVertexColour, verticesColourMap.get(vertex))) {
                correctColours++;
            }
        }
        return (double) correctColours/neighbourhoodList.size();
    }

    private Map<String, CustomWeightedEdge> animalSightNeighbourhoodSelection(Map<String, CustomWeightedEdge> neighbourhoodMap){
        int numberOfVertices = this.choosingVerticesToModifyUsingNormalDistribution(neighbourhoodMap.size(), StorkFeedingConstants.SIGHT_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR);
        System.out.println("Normal deviation result: " + numberOfVertices);
        numberOfVertices = Math.min(numberOfVertices, neighbourhoodMap.size());
        List<Integer> randomValues = new Random()
                .ints(0, neighbourhoodMap.size())
                .distinct()
                .limit(numberOfVertices)
                .boxed()
                .toList();
        Map<String, CustomWeightedEdge> reducedNeighbourhood = new HashMap<>();
        List<String> neighbourhoodVertices = neighbourhoodMap.keySet().stream().toList();
        for (Integer randomValue : randomValues) {
            String vertex = neighbourhoodVertices.get(randomValue);
            reducedNeighbourhood.put(vertex,neighbourhoodMap.get(vertex));
        }
        return reducedNeighbourhood;
    }

    private void colouringWithDSaturOnReducedNeignbourhood(Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, String currentVertex, Map<String, CustomWeightedEdge> neighbourhoodMap) {
        //TODO: If necessary implement rejecting solution if it makes fitting worse
        Map<String, CustomWeightedEdge> reducedNeighbourhood = this.animalSightNeighbourhoodSelection(neighbourhoodMap);
        this.colouringVertexWithDSatur(verticesColourMap, coloursMap, reducedNeighbourhood, currentVertex);
    }

    private void colouringVertexWithDSatur(Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, Map<String, CustomWeightedEdge> neighbourhoodMap, String currentVertex){
        //zapisanie poprzedniego koloru
        Integer oldColour = verticesColourMap.get(currentVertex);
        //przygotowanie tablicy kolorow wierzcholkow z barwami z colourMap
        Map<Integer, Integer> neighbourColours = new HashMap<>(coloursMap);
        neighbourColours.replaceAll((key, value) -> value = 0);
        //get colour occurrence in neighbour vertices
        for(String vertex : neighbourhoodMap.keySet()) {
            Integer vertexColour = verticesColourMap.get(vertex);
            neighbourColours.replace(vertexColour, neighbourColours.get(vertexColour) + 1);
        }
        //excluding 0 colour as it stands for not coloured
        neighbourColours.remove(0);
        //get minimal colour
        Integer minimalColour = Collections.min(neighbourColours.entrySet(), Map.Entry.comparingByValue()).getKey();
        //update supervisor colour
        verticesColourMap.replace(currentVertex, minimalColour);
        //update coloursMap
        coloursMap.replace(oldColour, coloursMap.get(oldColour) - 1);
        coloursMap.replace(minimalColour, coloursMap.get(minimalColour) + 1);
    }

    private void storkOptimisation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, StorkAgent stork) {
        String currentVertex = stork.getCurrentVertex();
        Map<String, CustomWeightedEdge> neighbourhoodMap = this.customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, currentVertex);
        double fitnessValue = this.calculatePrecisionFitnessFunction(graph, verticesColourMap, neighbourhoodMap, currentVertex);

        if (fitnessValue < StorkFeedingConstants.GOOD_COLOURING_FITNESS && fitnessValue >= StorkFeedingConstants.MODERATE_COLOURING_FITNESS) {
            //moderate colouring - dSatur for vertex / animal sight, dstaur based on part of neighbourhood
            colouringWithDSaturOnReducedNeignbourhood(verticesColourMap, coloursMap, currentVertex, neighbourhoodMap);
        } else if(fitnessValue >= StorkFeedingConstants.PERFECT_COLOURING_FITNESS) {
            //perfect colouring - colour improvement
        } else if (fitnessValue >= StorkFeedingConstants.GOOD_COLOURING_FITNESS) {//&& fitnessValue < StorkFeedingConstants.PERFECT_COLOURING_FITNESS) {
            //good colouring - colouring improvement
        } else if (fitnessValue < StorkFeedingConstants.MODERATE_COLOURING_FITNESS && fitnessValue >= StorkFeedingConstants.LOW_COLOURING_FITNESS) {
            //low colouring - dSatur for neighbourhood / dsatur for vertex
            this.colouringVertexWithDSatur(verticesColourMap, coloursMap, neighbourhoodMap, currentVertex);
        }
    }

    public StorkFeedingHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
