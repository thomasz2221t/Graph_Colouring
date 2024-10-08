package pl.polsl.metaheuristics;

import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.bees.BeeAgent;
import pl.polsl.agents.bees.BeesHive;
import pl.polsl.constants.BeeColouringConstants;
import pl.polsl.enums.BeeAgentType;
import pl.polsl.exceptions.BeesHiveNotFound;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class BeeColouringHeuristic extends AbstractColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    public Double robustness = 100.0;
    public long systemTime;
    public long cpuTime;
    public boolean colouringValid;
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    private List<BeesHive> beesHives = new ArrayList<>();
    private List<BeeAgent> bees = new ArrayList<>();
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph(final int numberOfBeeWorkers, final int numberOfBeeScouts, final long beeColouringMaxIterations,
                                               final int maximalRobustColourNumber, final int feedingRegionDepth, final int workerOperationalIterationNumber,
                                               final int scoutOperationalIterationNumber, final int hivesShuffleIterationPeriod) {
        this.resetVariables();
        this.init(numberOfBeeWorkers, numberOfBeeScouts, maximalRobustColourNumber, feedingRegionDepth);
        long i = 0;

        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        long startTime = System.nanoTime();
        long cpuStartTime = threadMxBean.getCurrentThreadCpuTime();

        while(i < beeColouringMaxIterations) {

            for(int k = 0; k < (numberOfBeeWorkers + numberOfBeeScouts); k++) {
                BeeAgent bee = this.bees.get(k);
                this.performBeeSearch(this.graph, this.verticesColourMap, this.coloursMap, this.beesHives, bee,
                        workerOperationalIterationNumber, scoutOperationalIterationNumber);
            }
            if(i % hivesShuffleIterationPeriod == 0) {
                this.shuffleBeesAndHives(this.graph, this.bees, this.beesHives, feedingRegionDepth);
            }
            i++;
            if(i % BeeColouringConstants.ROBUSTNESS_UPDATE_INTERVAL == 0) {
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

    private void init(int numberOfBeeWorkers, int numberOfBeeScouts, int maximalRobustColourNumber, int feedingRegionDepth) {
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        this.initColourList(this.coloursMap, this.graph.vertexSet().size(), maximalRobustColourNumber);
        this.initHives(this.graph, this.beesHives, numberOfBeeWorkers + numberOfBeeScouts, feedingRegionDepth);
        this.initBees(this.bees, this.beesHives, numberOfBeeWorkers, numberOfBeeScouts);
    }

    private Map<String, Integer> determineFeedingRegionInformation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, String beginVertex, int feedingRegionDepth) {
        List<String> verticesToVisit = new ArrayList<>();
        Map<String, Integer> feedingRegion = new HashMap<>();
        verticesToVisit.add(beginVertex);
        for(int i = 0; i < feedingRegionDepth; i++) {
            int k = 0;
            while(k < verticesToVisit.size()) {
                Map<String, CustomWeightedEdge> neighbourhoodMap =
                        this.customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, verticesToVisit.get(k));
                for(String vertex : neighbourhoodMap.keySet()) {
                    if(feedingRegion.containsKey(vertex))
                        continue;
                    feedingRegion.put(vertex, 0);
                    verticesToVisit.add(vertex);
                }
                verticesToVisit.remove(k);
                k++;
            }
        }
        return feedingRegion;
    }

    private List<BeesHive> initHives(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<BeesHive> hives, int numberOfAgents, int feedingRegionDepth) {
        for(int i = 0; i < numberOfAgents; i++) {
            String randomVertex = this.customWeightedGraphHelper.getRandomVertexFromGraph(graph);
            Map<String, Integer> feedingRegion = this.determineFeedingRegionInformation(graph, randomVertex, feedingRegionDepth);
            BeesHive beesHive = new BeesHive(randomVertex, feedingRegion);
            hives.add(beesHive);
        }
        return hives;
    }

    private List<BeeAgent> initBees(List<BeeAgent> bees, List<BeesHive> hives, int numberOfBeeWorkers, int numberOfBeeScouts) {
        int scoutingVacancies = numberOfBeeScouts;
        int workingVacancies = numberOfBeeWorkers;
        int hiveIndex = 0;
        while(scoutingVacancies + workingVacancies > 0) {
            BeesHive hive = hives.get(hiveIndex);
            BeeAgentType beeType;
            if(scoutingVacancies > 0){
                beeType = BeeAgentType.SCOUT;
                scoutingVacancies--;
            } else {
                beeType = BeeAgentType.WORKER;
                workingVacancies--;
            }
            BeeAgent bee = new BeeAgent(beeType, hive);
            bees.add(bee);
            hiveIndex++;
        }
        return bees;
    }

    private Map<String, CustomWeightedEdge> colourVertexWithDSaturAndReturnNeighbourhood(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, BeeAgent bee) {
        Map<String, CustomWeightedEdge> neighbourhoodMap =
                this.customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, bee.getCurrentVertex());
        Integer oldColour = verticesColourMap.get(bee.getCurrentVertex());
        Map<Integer, Integer> neighbourColours = new HashMap<>(coloursMap);
        neighbourColours.replaceAll((key, value) -> value = 0);
        for(String vertex : neighbourhoodMap.keySet()) {
            Integer vertexColour = verticesColourMap.get(vertex);
            neighbourColours.replace(vertexColour, neighbourColours.get(vertexColour) + 1);
            bee.updateFeedingRegionInformation(vertex, vertexColour);
        }
        neighbourColours.remove(0);
        Integer minimalColour = Collections.min(neighbourColours.entrySet(), Map.Entry.comparingByValue()).getKey();
        bee.updateFeedingRegionInformation(bee.getCurrentVertex(), minimalColour);
        this.applyColouring(verticesColourMap, coloursMap, bee.getCurrentVertex(), oldColour, minimalColour);
        return neighbourhoodMap;
    }

    private double calculatePassingProbability(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, BeeAgent bee, Map<String, Double> passingProbabilites, BeesHive hive, double probabilitesSum, String vertex) {
        if(bee.getType() == BeeAgentType.WORKER && !hive.getFeedingRegionInformation().containsKey(vertex)) {
            return probabilitesSum;
        }
        CustomWeightedEdge edge = graph.getEdge(bee.getCurrentVertex(), vertex) != null
                ? graph.getEdge(bee.getCurrentVertex(), vertex)
                : graph.getEdge(vertex, bee.getCurrentVertex());
        double robustness = graph.getEdgeWeight(edge);
        double vertexColoured =  verticesColourMap.get(vertex) > 0 ? 1 : 0;
        double vertexVisited = bee.getVisitedVertexMemory().contains(vertex) ? 1 : 0;
        double heuristicInformation = BeeColouringConstants.HEURISTIC_INFO_ROBUSTNESS_FACTOR * robustness
                + BeeColouringConstants.HEURISTIC_INFO_COLOURING_VALIDITY_FACTOR * vertexColoured
                / BeeColouringConstants.HEURISTIC_INFO_VERTEX_VISITED_FACTOR * vertexVisited;
        probabilitesSum += heuristicInformation;
        passingProbabilites.put(vertex, heuristicInformation);
        return probabilitesSum;
    }

    private String estimateNewRouteForBee(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, BeeAgent bee, Map<String, CustomWeightedEdge> neighbourhood, BeesHive hive) {
        Map<String, Double> passingProbabilites = new HashMap<>();
        double probabilitesSum = 0.0;

        for (String vertex : neighbourhood.keySet()) {
            probabilitesSum = this.calculatePassingProbability(graph, verticesColourMap, bee, passingProbabilites, hive, probabilitesSum, vertex);
        }
        return this.estimateRouteByProbabilites(passingProbabilites, probabilitesSum);
    }

    private void moveBeeToNextRoute(BeeAgent bee, String nextVertex) {
        bee.setCurrentVertex(nextVertex);
        bee.memorizeNewVisitedVertex(nextVertex);
    }

    private BeesHive getHive(BeeAgent bee, List<BeesHive> hives) throws BeesHiveNotFound {
        return hives
                .stream()
                .filter((BeesHive beesHive) -> beesHive.getLocationVertex() == bee.getHiveVertex())
                .findFirst()
                .orElseThrow(() -> new BeesHiveNotFound("Couldn't find bee hive."));
    }

    private void returnToHiveAndShareInfo(BeeAgent bee, BeesHive hive) {
        this.moveBeeToNextRoute(bee, bee.getHiveVertex());
        hive.updateFeedingRegionInformation(bee.getFeedingRegionInformation());
    }

    private void performBeeSearch(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph,
                                  Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap,
                                  List<BeesHive> hives, BeeAgent bee, int workerOperationalIterationNumber,
                                  int scoutOperationalIterationNumber) {
        int beeOperationalTime = bee.getType() == BeeAgentType.SCOUT
                ? scoutOperationalIterationNumber
                : workerOperationalIterationNumber;
        try {
            BeesHive hive = this.getHive(bee, hives);
            for(int i = 0; i < beeOperationalTime; i++) {
                Map<String, CustomWeightedEdge> neighbourhood = this.colourVertexWithDSaturAndReturnNeighbourhood(graph, verticesColourMap, coloursMap, bee);
                String nextVertex = this.estimateNewRouteForBee(graph, verticesColourMap, bee, neighbourhood, hive);
                this.moveBeeToNextRoute(bee, nextVertex);
            }
            this.returnToHiveAndShareInfo(bee, hive);
        } catch (BeesHiveNotFound e) {
            System.err.println(e + ": " + e.getMessage());
        }
    }

    private String getHiveRelocationVertex(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> feedingRegionInformation) {
        String hiveRelocationVertex;
        hiveRelocationVertex = feedingRegionInformation
                .entrySet()
                .stream()
                .filter(vertex -> vertex.getValue() == 0)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(
                        this.customWeightedGraphHelper.getRandomVertexFromGraph(graph));
        return hiveRelocationVertex;
    }

    private void relocateHiveAndBee(List<BeesHive> hives, BeeAgent bee, Map<String, Integer> feedingRegionInformation, String hiveRelocationVertex, Map<String, Integer> feedingRegion) {
        try{
            bee.setCurrentVertex(hiveRelocationVertex);
            bee.setFeedingRegionInformation(feedingRegion);
            BeesHive hive = this.getHive(bee, hives);
            hive.setFeedingRegionInformation(feedingRegionInformation);
            hive.setLocationVertex(hiveRelocationVertex);
            bee.setHiveVertex(hiveRelocationVertex);
        } catch (BeesHiveNotFound e) {
            System.err.println(e + ": " + e.getMessage());
        }
    }

    private void shuffleBeesAndHives(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<BeeAgent> bees, List<BeesHive> hives, int feedingRegionDepth) {
        for (BeeAgent bee : bees) {
            Map<String, Integer> feedingRegionInformation = bee.getFeedingRegionInformation();
            String hiveRelocationVertex = this.getHiveRelocationVertex(graph, feedingRegionInformation);
            Map<String, Integer> feedingRegion = this.determineFeedingRegionInformation(graph, hiveRelocationVertex, feedingRegionDepth);
            this.relocateHiveAndBee(hives, bee, feedingRegionInformation, hiveRelocationVertex, feedingRegion);
        }
    }

    public void resetVariables() {
        this.verticesColourMap = new HashMap<>();
        this.robustness = 100.0;
        this.systemTime = 0;
        this.cpuTime = 0;
        this.colouringValid = false;
        this.coloursMap = new HashMap<>();
        this.beesHives = new ArrayList<>();
        this.bees = new ArrayList<>();
    }

    public BeeColouringHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
