package pl.polsl.heuristics;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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


    protected boolean checkIfColourIsValid(Map<String, CustomWeightedEdge> randomVertexNeighbourhoodList, Map<String, Integer> verticesColourMap, Integer randomColour) {
        for (String vertex : randomVertexNeighbourhoodList.keySet()) {
            if (verticesColourMap.get(vertex) == randomColour) {
                return false;
            }
        }
        return true;
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
}
