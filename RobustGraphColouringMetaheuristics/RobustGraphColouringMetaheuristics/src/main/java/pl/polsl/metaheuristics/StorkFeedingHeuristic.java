package pl.polsl.metaheuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.StorkAgent;
import pl.polsl.constants.StorkFeedingConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorkFeedingHeuristic extends AbstractColouringHeuristic {

    public DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph;
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
            this.storkOptimisation();
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

    private double calculatePrecisionFitnessFunction() {
        double fitnessFunction = 0.0;

        return fitnessFunction;
    }

    private void storkOptimisation() {
        double fintessValue = this.calculatePrecisionFitnessFunction();

        if (fintessValue < StorkFeedingConstants.GOOD_COLOURING_FITNESS && fintessValue >= StorkFeedingConstants.MODERATE_COLOURING_FITNESS) {
            //moderate colouring - dSatur for vertex / animal sight, dstaur based on part of neighbourhood
        } else if(fintessValue >= StorkFeedingConstants.PERFECT_COLOURING_FITNESS) {
            //perfect coluring - colour improvement
        } else if (fintessValue >= StorkFeedingConstants.GOOD_COLOURING_FITNESS) {//&& fintessValue < StorkFeedingConstants.PERFECT_COLOURING_FITNESS) {
            //good colouring - colouring improvement
        } else if (fintessValue < StorkFeedingConstants.MODERATE_COLOURING_FITNESS && fintessValue >= StorkFeedingConstants.LOW_COLOURING_FITNESS) {
            //low colouring - dSatur for neighbourhood / dsatur for vertex
        }
    }


    public StorkFeedingHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
