package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.agents.AntAgent;
import pl.polsl.constants.AntColouringConstants;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.*;

public class AntColouringHeuristic extends AbstractColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    private Map<Integer, Integer> coloursMap = new HashMap<>();
    private Map<String, Double> pheromoneMap = new HashMap<>();
    private List<AntAgent> ants = new ArrayList<>();
    private Double robustness = Double.valueOf(100);
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph() {
        ////////////////////////////////
        /////Przygotowanie zasobów/////
        //////////////////////////////
        //przygotowanie grafu
        this.graph = customWeightedGraphHelper.imposeUncertaintyToGraph(this.graph,
                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);//Losowy wybór krawędzi które będą miały zmienione losowo wagi
        //only for testing
        customWeightedGraphHelper.savingGraphVisualizationToFile(this.graph, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"uncertainty.png");
        this.init();

        long i = 0;
        //pętla while
        //while((i < AntColouringConstants.AntColouringMaxIterations) && (robustness > AntColouringConstants.AntColouringMinRobustness)) {
        while(i < AntColouringConstants.ANT_COLOURING_MAX_ITERATIONS){
//            if(robustness < AntColouringConstants.AntColouringMinRobustness) {
//                break;
//            }
            //====
            for(int k=0; k < this.ants.size(); k++) {
                this.antOptimization(k);
                //======
            }
            //update feromonu
            this.updatePheromoneTrail();
            //====
            //local search
            this.localSearchProcedure(this.graph);
            i++;
            System.out.println(i);
            if(i % AntColouringConstants.ROBUSTNESS_UPDATE_INTERVAL == 0) {
                this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
            }
        }
        this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
        System.out.println("Robustness: " + robustness);
        System.out.println("Is colouring valid among solid edges: " + this.checkGraphValidityAmongSolidEdges(this.graph, this.verticesColourMap));

        System.out.println("Koniec");
        return this.verticesColourMap;
    }

    private void antOptimization(int k) {
        AntAgent ant = this.ants.get(k);
        //tablica lokalnych przejść mrówki
        Map<String, CustomWeightedEdge> vertexNeighbourhoodList = customWeightedGraphHelper.
                getNeighbourhoodListOfVertex(this.graph, ant.getCurrentVertex());
        //=====
        //Nadaj kolor
        this.assignColourToVertex(ant, vertexNeighbourhoodList);
        //====
        //obliczenie wag przejscia
        Map<String, Double> passingProbabilityMap = this.calculateAntPassingProbabilities(graph,
                ant,
                vertexNeighbourhoodList,
                AntColouringConstants.PASSING_PROBABILITY_HEURISTIC_WEIGHT,
                AntColouringConstants.PASSING_PROBABILITY_PHEROMONE_WEIGHT);
        //====
        //while coloring isn't valid select higher probability node
        //przenies mrowke
        this.relocateAntToNextVertex(ant, passingProbabilityMap);
    }

    private void init() {
        //Przygotowanie mapy koloru Map<V,Integer> (Interface VertexColoringAlgorithm.Coloring<V>)
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        //Przygotowanie listy colorow c
        this.initColourList(this.coloursMap, this.graph.vertexSet().size(), AntColouringConstants.MINIMAL_ROBUST_COLOUR_NUMBER);
        //Przygotowanie mapy feromonu
        this.initPheromoneMap(this.graph, this.pheromoneMap);
        //spawn mrówek
        this.initAnts(this.ants, AntColouringConstants.NUMBER_OF_AGENTS);
    }

    private Map<String, Double> initPheromoneMap(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, Map<String, Double> pheromoneMap) {
        List<String> vertexList = graph.vertexSet().stream().toList();
        for (String vertex: vertexList) {
            pheromoneMap.put(vertex, 0.0001);
        }
        return pheromoneMap;
    }

    private List<AntAgent> initAnts(List<AntAgent> ants, int numberOfAgents) {
        for (int i = 0; i < numberOfAgents; i++) {
            AntAgent ant = new AntAgent();
            String vertexToVisit = customWeightedGraphHelper.getRandomVertexFromGraph(this.graph);
            ant.setCurrentVertex(vertexToVisit);
            ants.add(ant);
        }
        return ants;
    }

    private Map<Integer, Integer> initColourList(Map<Integer, Integer> coloursMap, int numberOfGraphVertices, int beginningNumberOfColours) {
        coloursMap.put(0, numberOfGraphVertices);
        for(int i = 1; i <= beginningNumberOfColours; i++) {
            coloursMap.put(i, 0);
        }
        return coloursMap;
    }

    private boolean checkIfAllVerticesAreSolid(Map<String, CustomWeightedEdge> vertexNeighbourhoodList) {
        for(String neighbourVertex : vertexNeighbourhoodList.keySet()) {
            if(graph.getEdgeWeight(vertexNeighbourhoodList.get(neighbourVertex)) < AntColouringConstants.ROBUSTNESS_PENALTY_COLOURING_INVALID_ACCEPTABLE) {
                return false;
            }
        }
        return true;
    }

    private Integer chooseMinimalColourFromNeighbourhood(Map<String, CustomWeightedEdge> vertexNeighbourhoodList) {
        Map<Integer, Integer> neighbourVerticesColours = new HashMap<>();
        for(String neighbourVertex : vertexNeighbourhoodList.keySet()) {
            Integer neighbourColour = this.verticesColourMap.get(neighbourVertex);
            if(neighbourVerticesColours.containsKey(neighbourColour)) {
                neighbourVerticesColours.replace(neighbourColour, neighbourVerticesColours.get(neighbourColour) + 1);
            } else {
                neighbourVerticesColours.put(neighbourColour, 1);
            }
        }
        Integer minimalNeighbourColourIndex = -1;
        Integer minimalNeighbourColourOccurrences = this.graph.vertexSet().size();
        for (Integer colour : neighbourVerticesColours.keySet()) {
            if(neighbourVerticesColours.get(colour) < minimalNeighbourColourOccurrences) {
                minimalNeighbourColourIndex = colour;
                minimalNeighbourColourOccurrences = neighbourVerticesColours.get(colour);
            }
        }
        return minimalNeighbourColourIndex;
    }

    private void assignColourToVertex(AntAgent ant, Map<String, CustomWeightedEdge> vertexNeighbourhoodList) {
        Integer oldColourIndex = verticesColourMap.get(ant.getCurrentVertex());
        Integer newColourIndex = -1;
        for (Integer colourIndex : this.coloursMap.keySet()) {
            if(colourIndex == 0)
                continue;
            boolean isColourFree = checkIfColourIsValid(vertexNeighbourhoodList, this.verticesColourMap, colourIndex);
            if(isColourFree) {
                newColourIndex = colourIndex;
            }
        }
        if(newColourIndex == -1) {//out of minimal colour, add new color
            //check if some edge has penalty higher than const
            boolean allVerticesSolid = this.checkIfAllVerticesAreSolid(vertexNeighbourhoodList);
            if(allVerticesSolid) {
                //if all vertices are solid and out of minimal colour, then create new colour
                newColourIndex = coloursMap.keySet().size();
                this.coloursMap.put(coloursMap.keySet().size(), 0);
            } else {
                //if not all vertices solid then choose minimal colour out of all neighbours
                newColourIndex = this.chooseMinimalColourFromNeighbourhood(vertexNeighbourhoodList);
            }
        }
        this.verticesColourMap.replace(ant.getCurrentVertex(), newColourIndex);
        this.coloursMap.replace(oldColourIndex, this.coloursMap.get(oldColourIndex) - 1);
        this.coloursMap.replace(newColourIndex, this.coloursMap.get(newColourIndex) + 1);
    }

    private double calculateUnvisitedNeighboursPheromoneAndHeuristicInformation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, AntAgent ant, Map<String, CustomWeightedEdge> vertexNeighbourhoodList, double heuristicInformationWeight, double pheromoneWeight, List<String> antMemory, Map<String, Double> pheromone, Map<String, Double> passingProbabilities, double unvisitedNeighboursPheromoneAndProbabilitySum) {
        for (String vertex: vertexNeighbourhoodList.keySet()) {
            //find edge
            CustomWeightedEdge edge = graph.getEdge(ant.getCurrentVertex(), vertex) != null
                    ? graph.getEdge(ant.getCurrentVertex(), vertex)
                    : graph.getEdge(vertex, ant.getCurrentVertex());
            //obliczenie informacji heurystycznej
            //1/ilosc_wierzcholkow + solidnosc
            double robustness = graph.getEdgeWeight(edge);
            int numberOfPotentialRoutes = customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, vertex).size();
            double heuristicInformation = robustness/numberOfPotentialRoutes;
            //obliczenie feromonu
            double pheromoneValue = pheromone.get(vertex);
            //obliczenie feromonu i informacji heurystycznej
            double probabilityContentsMultiplication = (heuristicInformationWeight * heuristicInformation) * (pheromoneWeight * pheromoneValue);
            if(!antMemory.contains(vertex)){ //unvisited
                unvisitedNeighboursPheromoneAndProbabilitySum += probabilityContentsMultiplication;
            }
            passingProbabilities.put(vertex, probabilityContentsMultiplication);
        }
        return unvisitedNeighboursPheromoneAndProbabilitySum;
    }

    private void divideProbabilityByPassingProbabilityOfUnvisitedNodes(List<String> antMemory, Map<String, Double> passingProbabilities, double unvisitedNeighboursPheromoneAndProbabilitySum) {
        for(String vertex : passingProbabilities.keySet()) {
            double unvisitedSumTemp = unvisitedNeighboursPheromoneAndProbabilitySum;
            double probabilityContentsValue = passingProbabilities.get(vertex);
            //if vertex was unvisited subtract its value
            if(!antMemory.contains(vertex)) {
                unvisitedSumTemp -= probabilityContentsValue;
            }
            //watch out for dividing by zero
            unvisitedSumTemp = unvisitedSumTemp > 0.0 ? unvisitedSumTemp : 1;
            double probabilityValue = probabilityContentsValue / unvisitedSumTemp;
            passingProbabilities.replace(vertex, probabilityValue);
        }
    }

    private Map<String, Double> calculateAntPassingProbabilities(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, AntAgent ant,  Map<String, CustomWeightedEdge> vertexNeighbourhoodList, double heuristicInformationWeight, double pheromoneWeight) {
        List<String> antMemory = ant.getVisitedVertexMemory();
        final Map<String, Double> pheromone = this.pheromoneMap;
        Map<String, Double> passingProbabilities = new HashMap<>();
        double unvisitedNeighboursPheromoneAndProbabilitySum = 0.0;
        unvisitedNeighboursPheromoneAndProbabilitySum =
                this.calculateUnvisitedNeighboursPheromoneAndHeuristicInformation(graph, ant, vertexNeighbourhoodList, heuristicInformationWeight, pheromoneWeight, antMemory, pheromone, passingProbabilities, unvisitedNeighboursPheromoneAndProbabilitySum);
        //divide passing probability by unvisited nodes
        this.divideProbabilityByPassingProbabilityOfUnvisitedNodes(antMemory, passingProbabilities, unvisitedNeighboursPheromoneAndProbabilitySum);
        return passingProbabilities;
    }

    private void updatePheromoneTrail() {
        int numberOfColors = this.coloursMap.size();
        double pheromoneMaxValue = 1 / ((1 - AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT) * numberOfColors);
        double pheromoneMinValue = 0.087 * pheromoneMaxValue;
        for (String vertex : this.pheromoneMap.keySet()) {
            double oldPheromoneValue = this.pheromoneMap.get(vertex);
            double newPheromoneValue = 0;
            //if vertex is coloured enhance solution if not just evaporate pheromone
            if(this.verticesColourMap.get(vertex) != 0) {
                newPheromoneValue = AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT * oldPheromoneValue
                        + ((1 - AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT) / numberOfColors);
            } else {
                newPheromoneValue = AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT * oldPheromoneValue;
            }
            //checking min-max constraint of pheromone
            newPheromoneValue = Math.min(newPheromoneValue, pheromoneMaxValue);
            newPheromoneValue = Math.max(newPheromoneValue, pheromoneMinValue);
            this.pheromoneMap.replace(vertex, newPheromoneValue);
        }
    }

    private String getHighestProbabilityRoute(Map<String, Double> passingProbabilityMap) {
        double maxProbability = 0.0;
        String vertexName = "";
        for (String vertex : passingProbabilityMap.keySet()) {
            if(passingProbabilityMap.get(vertex) > maxProbability){
                maxProbability = passingProbabilityMap.get(vertex);
                vertexName = vertex;
            }
        }
        return vertexName;
    }

    private void relocateAntToNextVertex(AntAgent ant, Map<String, Double> passingProbabilityMap){
        ant.memorizeNewVisitedVertex(ant.getCurrentVertex());
        String nextVertex = this.getHighestProbabilityRoute(passingProbabilityMap);
        ant.setCurrentVertex(nextVertex);
    }

    private Integer randomlySelectColour() {
        Random random = new Random();
        Integer randomColour = -1;
        randomColour = this.coloursMap
                .keySet()
                .stream()
                .skip(random.nextInt(coloursMap.size() - 1) + 1)
                .findFirst()
                .orElse(null);
        return randomColour;
    }

    private void imposeColouringAndUpdatePheromone(String randomVertex, Integer randomColour) {
        Integer oldColour = this.verticesColourMap.get(randomVertex);
        this.verticesColourMap.replace(randomVertex, randomColour);
        this.coloursMap.replace(randomColour, this.coloursMap.get(randomColour) + 1);
        if (this.coloursMap.get(oldColour) > 1) {
            this.coloursMap.replace(oldColour, this.coloursMap.get(oldColour) - 1);
        } else {
            this.coloursMap.remove(oldColour);
        }
        this.updatePheromoneTrail();
    }

    private void localSearchProcedure(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        //protecting against empty colouring
        if (this.coloursMap.size() > 1) {
            String randomVertex = this.customWeightedGraphHelper.getRandomVertexFromGraph(graph);//get random vertex from graph
            //randomly select colour
            Integer randomColour = this.randomlySelectColour();
            Map<String, CustomWeightedEdge> randomVertexNeighbourhoodList = customWeightedGraphHelper.
                    getNeighbourhoodListOfVertex(this.graph, randomVertex);
            boolean isRandomColourValid = checkIfColourIsValid(randomVertexNeighbourhoodList,this.verticesColourMap, randomColour);
            //check if colouring is valid, if so approve changes and update pheromone
            if (isRandomColourValid) {
                this.imposeColouringAndUpdatePheromone(randomVertex, randomColour);
            }
        }
    }

    public AntColouringHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
