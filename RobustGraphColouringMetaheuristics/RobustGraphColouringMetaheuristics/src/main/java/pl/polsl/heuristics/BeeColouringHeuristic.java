package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.bees.BeeAgent;
import pl.polsl.agents.bees.BeesHive;
import pl.polsl.constants.BeeColouringConstants;
import pl.polsl.enums.BeeAgentType;
import pl.polsl.exceptions.BeesHiveNotFound;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.*;

public class BeeColouringHeuristic extends AbstractColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();//the observator
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    private List<BeesHive> beesHives = new ArrayList<>();
    private List<BeeAgent> bees = new ArrayList<>();
    private Double robustness = 100.0;
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph() {

        this.init();
        long i = 0;
        while(i < BeeColouringConstants.BEE_COLOURING_MAX_ITERATIONS) {

            for(int k = 0; k < BeeColouringConstants.NUMBER_OF_AGENTS; k++) {
                BeeAgent bee = this.bees.get(k);
                this.performBeeSearch(this.graph, this.verticesColourMap, this.coloursMap, this.beesHives, bee);
            }
            if(i % BeeColouringConstants.HIVES_SHUFFLE_ITERATION_PERIOD == 0) {
                this.shuffleBeesAndHives(this.graph, this.bees, this.beesHives);
            }
            if(i % BeeColouringConstants.ROBUSTNESS_UPDATE_INTERVAL == 0) {
                this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
            }
            i++;
        }
        this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
        System.out.println("Robustness: " + robustness);
        System.out.println("Is colouring valid among solid edges: " + this.checkGraphValidityAmongSolidEdges(this.graph, this.verticesColourMap));

        return this.verticesColourMap;
    }

    private void init() {
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        this.initColourList(this.coloursMap, this.graph.vertexSet().size(), BeeColouringConstants.MAXIMAL_ROBUST_COLOUR_NUMBER);
        //init Hives and Bees
        this.initHives(this.graph, this.beesHives);
        this.initBees(this.bees, this.beesHives);
    }

    //Iteracyjny Algorytm przeszukiwania w głąb, ale sąsiedztwa a nie pojedynczych sciezek
    private Map<String, Integer> determineFeedingRegionInformation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, String beginVertex) {
        List<String> verticesToVisit = new ArrayList<>();
        Map<String, Integer> feedingRegion = new HashMap<>();
        verticesToVisit.add(beginVertex);
        for(int i = 0; i < BeeColouringConstants.FEEDING_REGION_DEPTH; i++) {
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

    private List<BeesHive> initHives(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<BeesHive> hives) {
        for(int i = 0; i < BeeColouringConstants.NUMBER_OF_AGENTS; i++) {
            String randomVertex = this.customWeightedGraphHelper.getRandomVertexFromGraph(graph);
            Map<String, Integer> feedingRegion = this.determineFeedingRegionInformation(graph, randomVertex);
            BeesHive beesHive = new BeesHive(randomVertex, feedingRegion);
            hives.add(beesHive);
        }
        return hives;
    }

    private List<BeeAgent> initBees(List<BeeAgent> bees, List<BeesHive> hives) {
        int scoutingVacancies = BeeColouringConstants.NUMBER_OF_BEE_SCOUTS;
        int workingVacancies = BeeColouringConstants.NUMBER_OF_BEE_WORKERS;
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
        //getting vertex neighbourhood
        Map<String, CustomWeightedEdge> neighbourhoodMap =
                this.customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, bee.getCurrentVertex());
        //zapisanie poprzedniego koloru
        Integer oldColour = verticesColourMap.get(bee.getCurrentVertex());
        //przygotowanie tablicy kolorow wierzcholkow z barwami z colourMap
        Map<Integer, Integer> neighbourColours = new HashMap<>(coloursMap);
        neighbourColours.replaceAll((key, value) -> value = 0);
        //get colour occurrence in neighbour vertices and bee synchronise info
        for(String vertex : neighbourhoodMap.keySet()) {
            Integer vertexColour = verticesColourMap.get(vertex);
            neighbourColours.replace(vertexColour, neighbourColours.get(vertexColour) + 1);
            bee.updateFeedingRegionInformation(vertex, vertexColour);
        }
        //excluding 0 colour as it stands for not coloured
        neighbourColours.remove(0);
        //get minimal colour
        Integer minimalColour = Collections.min(neighbourColours.entrySet(), Map.Entry.comparingByValue()).getKey();
        //update bee information
        bee.updateFeedingRegionInformation(bee.getCurrentVertex(), minimalColour);
        //update obserwator colour
        verticesColourMap.replace(bee.getCurrentVertex(), minimalColour);
        //update coloursMap
        coloursMap.replace(oldColour, coloursMap.get(oldColour) - 1);
        coloursMap.replace(minimalColour, coloursMap.get(minimalColour) + 1);
        return neighbourhoodMap;
    }

    private double calculatePassingProbability(BeeAgent bee, Map<String, Integer> verticesColourMap, Map<String, CustomWeightedEdge> neighbourhood, Map<String, Double> passingProbabilites, BeesHive hive, double probabilitesSum, String vertex) {
        if(bee.getType() == BeeAgentType.WORKER && !hive.getFeedingRegionInformation().containsKey(vertex)) {
            //pruning routes to feeding region
            neighbourhood.remove(vertex);
        }
        //find edge
        CustomWeightedEdge edge = graph.getEdge(bee.getCurrentVertex(), vertex) != null
                ? graph.getEdge(bee.getCurrentVertex(), vertex)
                : graph.getEdge(vertex, bee.getCurrentVertex());
        //calculating heuristic information
        //(waga * pokolorowane + waga * robustness)/ waga * odwiedzone
        double robustness = graph.getEdgeWeight(edge);
        double colouringValidity =  verticesColourMap.get(vertex) > 0 ? 1 : 0;
        double vertexVisited = bee.getVisitedVertexMemory().contains(vertex) ? 1 : 0;
        double heuristicInformation = BeeColouringConstants.HEURISTIC_INFO_ROBUSTNESS_FACTOR * robustness
                + BeeColouringConstants.HEURISTIC_INFO_COLOURING_VALIDITY_FACTOR * colouringValidity
                / BeeColouringConstants.HEURISTIC_INFO_VERTEX_VISITED_FACTOR * vertexVisited;
        probabilitesSum += heuristicInformation;
        passingProbabilites.put(vertex, heuristicInformation);
        return probabilitesSum;
    }

    private String estimateRouteByProbabilites(Map<String, Double> passingProbabilites, double probabilitesSum) {
        //picking random vertex based on heuristic information as weight
        int index = 0;
        List<String> verticesList = passingProbabilites.keySet().stream().toList();
        for(double random = Math.random() * probabilitesSum; index < passingProbabilites.size() - 1; ++index) {
            random -= passingProbabilites.get(verticesList.get(index));
            if(random <= 0.0) break;
        }
        return verticesList.get(index);
    }

    private String estimateNewRouteForBee(BeeAgent bee, Map<String, Integer> verticesColourMap, Map<String, CustomWeightedEdge> neighbourhood, BeesHive hive) {
        //probability criterion based on
        Map<String, Double> passingProbabilites = new HashMap<>();
        double probabilitesSum = 0.0;

        for (String vertex : neighbourhood.keySet()) {
            probabilitesSum = calculatePassingProbability(bee, verticesColourMap, neighbourhood, passingProbabilites, hive, probabilitesSum, vertex);
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
        //return to hive
        this.moveBeeToNextRoute(bee, bee.getHiveVertex());
        //get hive
        hive.updateFeedingRegionInformation(bee.getFeedingRegionInformation());
    }

    private void performBeeSearch(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, List<BeesHive> hives, BeeAgent bee) {
        //dla kazdej mozliwosci ruchu
        int beeOperationalTime = bee.getType() == BeeAgentType.SCOUT
                ? BeeColouringConstants.SCOUT_OPERATIONAL_ITERATION_NUMBER
                : BeeColouringConstants.WORKER_OPERATIONAL_ITERATION_NUMBER;
        try {
            BeesHive hive = this.getHive(bee, hives);
            for(int i = 0; i < beeOperationalTime; i++) {
                //colour vertex and synchronise information about each neighbour vertex
                Map<String, CustomWeightedEdge> neighbourhood = this.colourVertexWithDSaturAndReturnNeighbourhood(graph, verticesColourMap, coloursMap, bee);
                //calculate probability + heuristic information
                String nextVertex = this.estimateNewRouteForBee(bee, verticesColourMap, neighbourhood, hive);
                //change node
                this.moveBeeToNextRoute(bee, nextVertex);
            }
            //go back to hive, share information
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
            //setting new vertex
            bee.setCurrentVertex(hiveRelocationVertex);
            //setting cleaned out feeding region information
            bee.setFeedingRegionInformation(feedingRegion);
            //getting hive
            //setting cleaned out feeding region information to hive
            BeesHive hive = this.getHive(bee, hives);
            hive.setFeedingRegionInformation(feedingRegionInformation);
            //setting hive location for bees
            hive.setLocationVertex(hiveRelocationVertex);
            bee.setHiveVertex(hiveRelocationVertex);
        } catch (BeesHiveNotFound e) {
            System.err.println(e + ": " + e.getMessage());
        }
    }

    private void shuffleBeesAndHives(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<BeeAgent> bees, List<BeesHive> hives) {
        for (BeeAgent bee : bees) {
            Map<String, Integer> feedingRegionInformation = bee.getFeedingRegionInformation();
            //get uncoloured vertex from neighbourhood or get random vertex
            String hiveRelocationVertex = this.getHiveRelocationVertex(graph, feedingRegionInformation);
            //get new feeding region without colouring on vertex
            Map<String, Integer> feedingRegion = this.determineFeedingRegionInformation(graph, hiveRelocationVertex);
            this.relocateHiveAndBee(hives, bee, feedingRegionInformation, hiveRelocationVertex, feedingRegion);
        }
    }

    public BeeColouringHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
