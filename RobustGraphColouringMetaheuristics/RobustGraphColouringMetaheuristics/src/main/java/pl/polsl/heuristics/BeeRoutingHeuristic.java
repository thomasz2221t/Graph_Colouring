package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.bees.BeeAgent;
import pl.polsl.agents.bees.BeesHive;
import pl.polsl.constants.BeeRoutingConstants;
import pl.polsl.enums.BeeAgentType;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeRoutingHeuristic extends AbstractColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();//the observator
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    private List<BeesHive> beesHives;
    private List<BeeAgent> bees;
    private Double robustness = Double.valueOf(100);
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph() {

        this.init();
        long i = 0;
        while(i < BeeRoutingConstants.BEE_COLOURING_MAX_ITERATIONS) {

            for(int k = 0; k < BeeRoutingConstants.NUMBER_OF_AGENTS; k++) {
                //dla kazdej mozliwosci ruchu
                //colour vertex
                //synchronise information with each vertex
                //
            }
            i++;
        }

        return this.verticesColourMap;
    }

    private void init() {
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        this.initColourList(this.coloursMap, this.graph.vertexSet().size(), BeeRoutingConstants.MAXIMAL_ROBUST_COLOUR_NUMBER);
        //init Hives and Bees
        this.initHives(this.graph, this.beesHives);
        this.initBees(this.bees, this.beesHives);
    }

    //Iteracyjny Algorytm przeszukiwania w głąb, ale sąsiedztwa a nie pojedynczych sciezek
    private Map<String, Integer> determineFeedingRegionInformation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, String beginVertex, Integer feedingRegionDepth) {
        List<String> verticesToVisit = new ArrayList<>();
        Map<String, Integer> feedingRegion = new HashMap<>();
        verticesToVisit.add(beginVertex);
        for(int i = 0; i < feedingRegionDepth; i++) {
            int k = 0;
            while(!verticesToVisit.isEmpty()) {
                Map<String, CustomWeightedEdge> neighbourhoodMap =
                        this.customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, verticesToVisit.get(k));
                for(String vertex : neighbourhoodMap.keySet()) {
                    if(feedingRegion.containsKey(vertex))
                        continue;
                    feedingRegion.put(vertex, 0);
                    verticesToVisit.add(vertex);
                }
                k++;
            }
        }
        return feedingRegion;
    }

    private List<BeesHive> initHives(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<BeesHive> hives) {
        for(int i = 0; i < BeeRoutingConstants.NUMBER_OF_AGENTS; i++) {
            String randomVertex = this.customWeightedGraphHelper.getRandomVertexFromGraph(graph);
            Map<String, Integer> feedingRegion = this.determineFeedingRegionInformation(graph, randomVertex, BeeRoutingConstants.FEEDING_REGION_DEPTH);
            BeesHive beesHive = new BeesHive(randomVertex, feedingRegion);
            hives.add(beesHive);
        }
        return hives;
    }

    private List<BeeAgent> initBees(List<BeeAgent> bees, List<BeesHive> hives) {
        int scoutingVacancies = BeeRoutingConstants.NUMBER_OF_BEE_SCOUTS;
        int workingVacancies = BeeRoutingConstants.NUMBER_OF_BEE_WORKERS;
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
            BeeAgent bee = new BeeAgent(beeType, hive.getFeedingRegionInformation());
            bees.add(bee);
            hiveIndex++;
        }
        return bees;
    }

    public BeeRoutingHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
