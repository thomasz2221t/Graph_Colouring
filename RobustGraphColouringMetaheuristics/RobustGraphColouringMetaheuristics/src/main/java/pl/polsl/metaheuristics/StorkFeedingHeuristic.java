package pl.polsl.metaheuristics;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.StorkAgent;
import pl.polsl.constants.StorkFeedingConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class StorkFeedingHeuristic extends AbstractColouringHeuristic {

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    public Double robustness = 100.0;
    public long systemTime;
    public long cpuTime;
    public boolean colouringValid;
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    public List<StorkAgent> storks = new ArrayList<>();
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph(final int numberOfAgents, final long storkFeedingMaxIterations,
                                               final int maximalRobustColourNumber, final double goodColouringFitness,
                                               final double moderateColouringFitness, final int sightNormalDistributionDeviationFactor){
        this.resetVariables();
        this.init(numberOfAgents, maximalRobustColourNumber);
        long i = 0;

        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        long startTime = System.nanoTime();
        long cpuStartTime = threadMxBean.getCurrentThreadCpuTime();
        while(i < storkFeedingMaxIterations) {
            for(int k = 0; k < numberOfAgents; k++) {
                StorkAgent stork = this.storks.get(k);
                this.storkOptimisation(this.graph, this.verticesColourMap, this.coloursMap, stork, goodColouringFitness, moderateColouringFitness, sightNormalDistributionDeviationFactor);
            }
            i++;
            //only for checking robustness criterrium
            if(i % StorkFeedingConstants.ROBUSTNESS_UPDATE_INTERVAL == 0) {
                this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
            }
        }
        long cpuEndTime = threadMxBean.getCurrentThreadCpuTime();
        long endTime = System.nanoTime();

        this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
        this.getMetaheuristicsStatistics(this.graph, this.verticesColourMap, robustness, startTime, cpuStartTime, cpuEndTime, endTime);
        Triple<Long, Long, Boolean> statistics = this.estimateMetaheuristicsStatistics(this.graph, this.verticesColourMap, startTime, cpuStartTime, cpuEndTime, endTime);
        this.systemTime = statistics.getLeft();
        this.cpuTime = statistics.getMiddle();
        this.colouringValid = statistics.getRight();
        return this.verticesColourMap;
    }

    private void init(int numberOfAgents, int maximalRobustColourNumber) {
        //Przygotowanie mapy koloru Map<V,Integer> (Interface VertexColoringAlgorithm.Coloring<V>)
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        //Przygotowanie listy colorow c
        this.initColourList(this.coloursMap, this.graph.vertexSet().size(), maximalRobustColourNumber);
        this.initStorks(this.storks, numberOfAgents);
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

    private double calculatePrecisionFitnessFunction(Map<String, Integer> verticesColourMap, Map<String, CustomWeightedEdge> neighbourhoodList, String currentVertex) {
        Integer currentVertexColour = verticesColourMap.get(currentVertex);
        int correctColours = 0;
        for(String vertex : neighbourhoodList.keySet()) {
            if(Objects.equals(currentVertexColour, verticesColourMap.get(vertex))) {
                correctColours++;
            }
        }
        return (double) correctColours/neighbourhoodList.size();
    }

    private Map<String, CustomWeightedEdge> animalSightNeighbourhoodSelection(Map<String, CustomWeightedEdge> neighbourhoodMap, int sightNormalDistributionDeviationFactor){
        int numberOfVertices = this.choosingVerticesToModifyUsingNormalDistribution(neighbourhoodMap.keySet().size(), sightNormalDistributionDeviationFactor);
        numberOfVertices = Math.max(numberOfVertices, 1);
        numberOfVertices = Math.min(numberOfVertices, neighbourhoodMap.size());
//        List<Integer> randomValues = new Random()
//                .ints(0, neighbourhoodMap.size())
//                .distinct()
//                .limit(numberOfVertices)
//                .boxed()
//                .toList();
        Random random = new Random();
        List<String> randomVertices = neighbourhoodMap
                .keySet()
                .stream()
                .skip(random.nextInt(neighbourhoodMap.size()))
                .limit(numberOfVertices)
                .toList();
        Map<String, CustomWeightedEdge> reducedNeighbourhood = new HashMap<>();
        for(String vertex : randomVertices) {
            reducedNeighbourhood.put(vertex, neighbourhoodMap.get(vertex));
        }
//        List<String> neighbourhoodVertices = neighbourhoodMap.keySet().stream().toList();
//        for (Integer randomValue : randomValues) {
//            String vertex = neighbourhoodVertices.get(randomValue);
//            reducedNeighbourhood.put(vertex,neighbourhoodMap.get(vertex));
//        }
        return reducedNeighbourhood;
    }

    private void colouringWithDSaturOnReducedNeignbourhood(Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, String currentVertex, Map<String, CustomWeightedEdge> neighbourhoodMap, int sightNormalDistributionDeviationFactor) {
        Map<String, CustomWeightedEdge> reducedNeighbourhood = this.animalSightNeighbourhoodSelection(neighbourhoodMap, sightNormalDistributionDeviationFactor);
        this.colouringVertexWithDSatur(verticesColourMap, coloursMap, reducedNeighbourhood, currentVertex);
    }

    private void greedyColourReductionOptimization(Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, Map<String, CustomWeightedEdge> neighbourhoodMap,  String currentVertex) {
        //zapisanie poprzedniego koloru
        Integer currentColour = verticesColourMap.get(currentVertex);
        Integer currentColourOccurrence = coloursMap.get(currentColour);
        //checking colour changing on
        for(Integer colour : coloursMap.keySet()) {
            Integer colourOccurrence = coloursMap.get(colour);
            if(currentColourOccurrence > colourOccurrence && checkIfColourIsValid(neighbourhoodMap, verticesColourMap, colour)) {
                currentColour = colour;
                currentColourOccurrence = colourOccurrence;
            }
        }
        if(!Objects.equals(verticesColourMap.get(currentVertex), currentColour)) {
            this.applyColouring(verticesColourMap, coloursMap, currentVertex, verticesColourMap.get(currentVertex), currentColour);
        }
    }

    private void stochasticSolutionOptimization(Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, Map<String, CustomWeightedEdge> neighbourhoodMap, String currentVertex) {
        Integer oldColour = verticesColourMap.get(currentVertex);
        Integer randomColour = this.randomlySelectColour(coloursMap);
        boolean isRandomColourValid = checkIfColourIsValid(neighbourhoodMap, verticesColourMap, randomColour);
        //check if colouring is valid, if so approve changes and update pheromone
        if (isRandomColourValid) {
            this.applyColouring(verticesColourMap, coloursMap, currentVertex, oldColour, randomColour);
        }
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
        this.applyColouring(verticesColourMap, coloursMap, currentVertex, oldColour, minimalColour);
    }

    private double calculatePassingProbability(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<String, CustomWeightedEdge> neighbourhoodMap, StorkAgent stork, Map<String, Double> passingProbabilites, double probabilitesSum, String vertex) {
        //find edge
        CustomWeightedEdge edge = graph.getEdge(stork.getCurrentVertex(), vertex) != null
                ? graph.getEdge(stork.getCurrentVertex(), vertex)
                : graph.getEdge(vertex, stork.getCurrentVertex());
        //calculating heuristic information
        //(waga * robustness)/ (waga * odwiedzone + waga * fitness)
        double robustness = graph.getEdgeWeight(edge);
        double vertexFitness =  this.calculatePrecisionFitnessFunction(verticesColourMap, neighbourhoodMap, vertex);
        double vertexVisited = stork.getVisitedVertexMemory().contains(vertex) ? 1 : 0;
        double heuristicInformation = StorkFeedingConstants.HEURISTIC_INFO_ROBUSTNESS_FACTOR * robustness
                / (StorkFeedingConstants.HEURISTIC_INFO_VERTEX_VISITED_FACTOR * vertexVisited
                + StorkFeedingConstants.HEURISTIC_INFO_VERTEX_FITNESS_FACTOR * vertexFitness);
        probabilitesSum += heuristicInformation;
        passingProbabilites.put(vertex, heuristicInformation);
        return probabilitesSum;
    }

    private String estimateNewRouteForStork(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<String, CustomWeightedEdge> neighbourhood, StorkAgent stork) {
        Map<String, Double> passingProbabilites = new HashMap<>();
        double probabilitesSum = 0.0;

        for (String vertex : neighbourhood.keySet()) {
            probabilitesSum = this.calculatePassingProbability(graph, verticesColourMap, neighbourhood, stork, passingProbabilites, probabilitesSum, vertex);
        }
        return this.estimateRouteByProbabilites(passingProbabilites, probabilitesSum);
    }

    private void relocateStorkToNextVertex(StorkAgent stork, String nextVertex) {
        stork.setCurrentVertex(nextVertex);
        stork.memorizeNewVisitedVertex(nextVertex);
    }

    private void storkOptimisation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, StorkAgent stork, double goodColouringFitness, double moderateColouringFitness, int sightNormalDistributionDeviationFactor) {
        String currentVertex = stork.getCurrentVertex();
        Map<String, CustomWeightedEdge> neighbourhoodMap = this.customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, currentVertex);
        double fitnessValue = this.calculatePrecisionFitnessFunction(verticesColourMap, neighbourhoodMap, currentVertex);

        if (fitnessValue < goodColouringFitness && fitnessValue >= moderateColouringFitness) {
            //moderate colouring - dSatur for vertex / animal sight, dstaur based on part of neighbourhood
            this.colouringWithDSaturOnReducedNeignbourhood(verticesColourMap, coloursMap, currentVertex, neighbourhoodMap, sightNormalDistributionDeviationFactor);
        } else if(fitnessValue >= StorkFeedingConstants.PERFECT_COLOURING_FITNESS) {
            //perfect colouring - colour improvement
            this.greedyColourReductionOptimization(verticesColourMap, coloursMap, neighbourhoodMap, currentVertex);
        } else if (fitnessValue >= goodColouringFitness) {//&& fitnessValue < StorkFeedingConstants.PERFECT_COLOURING_FITNESS) {
            //good colouring - colouring improvement
            this.stochasticSolutionOptimization(verticesColourMap, coloursMap, neighbourhoodMap, currentVertex);
        } else if (fitnessValue < moderateColouringFitness && fitnessValue >= StorkFeedingConstants.LOW_COLOURING_FITNESS) {
            //low colouring - dSatur for neighbourhood / dsatur for vertex
            this.colouringVertexWithDSatur(verticesColourMap, coloursMap, neighbourhoodMap, currentVertex);
        }
        this.relocateStorkToNextVertex(stork, this.estimateNewRouteForStork(graph, verticesColourMap, neighbourhoodMap, stork));
    }

    private void resetVariables() {
        this.verticesColourMap = new HashMap<>();
        this.robustness = 100.0;
        this.systemTime = 0;
        this.cpuTime = 0;
        this.colouringValid = false;
        this.coloursMap = new HashMap<>();
        this.storks = new ArrayList<>();
    }

    public StorkFeedingHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
