package pl.polsl.metaheuristics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public abstract class AbstractColouringHeuristic {

    protected Map<String, Integer> initVerticesColourMap(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap) {
        List<String> vertexList = graph.vertexSet().stream().toList();
        //Integer colourIndex = 0;
        for (String vertex: vertexList) {
            verticesColourMap.put(vertex, 0);
            //colourIndex++;
        }
        return verticesColourMap;
    }

    protected Map<Integer, Integer> initColourList(Map<Integer, Integer> coloursMap, int numberOfGraphVertices, int beginningNumberOfColours) {
        coloursMap.put(0, numberOfGraphVertices);
        for(int i = 1; i <= beginningNumberOfColours; i++) {
            coloursMap.put(i, 0);
        }
        return coloursMap;
    }

    protected int choosingVerticesToModifyUsingNormalDistribution(int numberOfVerticesInGraph, int standardDeviationDivisionFactor) {
        double arithmeticalMean = (double) numberOfVerticesInGraph / 2;
        double standardDeviation = (double) numberOfVerticesInGraph / standardDeviationDivisionFactor;
        NormalDistribution vertexNumberNormalDistribution = new NormalDistribution(arithmeticalMean, standardDeviation);
        double M = vertexNumberNormalDistribution.sample();
        return (int) Math.floor(M);
    }

    protected Integer randomlySelectColour(Map<Integer, Integer> coloursMap) {
        Random random = new Random();
        Integer randomColour = -1;
        randomColour = coloursMap
                .keySet()
                .stream()
                .skip(random.nextInt(coloursMap.size() - 1) + 1)
                .findFirst()
                .orElse(-1);
        return randomColour;
    }

    protected boolean checkIfColourIsValid(Map<String, CustomWeightedEdge> randomVertexNeighbourhoodList, Map<String, Integer> verticesColourMap, Integer randomColour) {
        for (String vertex : randomVertexNeighbourhoodList.keySet()) {
            if (verticesColourMap.get(vertex) == randomColour) {
                return false;
            }
        }
        return true;
    }

    protected void applyColouring(Map<String, Integer> verticesColourMap, Map<Integer, Integer> coloursMap, String currentVertex, Integer oldColour, Integer newColour) {
        //update supervisor colour
        verticesColourMap.replace(currentVertex, newColour);
        //update coloursMap
        coloursMap.replace(oldColour, coloursMap.get(oldColour) - 1);
        coloursMap.replace(newColour, coloursMap.get(newColour) + 1);
    }

    protected boolean checkGraphValidityAmongSolidEdges(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap) {
        for(CustomWeightedGraphHelper.CustomWeightedEdge edge : graph.edgeSet()) {
            Integer sourceVertexColour = verticesColourMap.get(graph.getEdgeSource(edge));
            Integer targetVertexColour = verticesColourMap.get(graph.getEdgeTarget(edge));
            if(graph.getEdgeWeight(edge) == 1.0 && Objects.equals(sourceVertexColour, targetVertexColour)) {
                return false;
            }
        }
        return true;
    }

    protected double calculateRobustness(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap) {
        double graphPenaltiesSum = 0.0;
        for(CustomWeightedGraphHelper.CustomWeightedEdge edge : graph.edgeSet()) {
            Integer sourceVertexColour = verticesColourMap.get(graph.getEdgeSource(edge));
            Integer targetVertexColour = verticesColourMap.get(graph.getEdgeTarget(edge));
            if((graph.getEdgeWeight(edge) < 1.0) && (Objects.equals(sourceVertexColour, targetVertexColour))) {
                graphPenaltiesSum += graph.getEdgeWeight(edge);
            }
        }
        return graphPenaltiesSum;
    }

    protected void getMetaheuristicsStatistics(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, double robustness, long startTime, long cpuStartTime, long cpuEndTime, long endTime) {
        System.out.println("Execution time: " + (endTime - startTime) + "[ns]"); // nanoseconds
        System.out.println("CPU execution time: " + (cpuEndTime - cpuStartTime) + "[ns]");// nanoseconds
        System.out.println("Robustness: " + robustness);
        System.out.println("Is colouring valid among solid edges: " + this.checkGraphValidityAmongSolidEdges(graph, verticesColourMap));
    }
}
