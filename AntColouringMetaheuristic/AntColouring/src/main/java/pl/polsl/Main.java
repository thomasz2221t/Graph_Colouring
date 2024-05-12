package pl.polsl;

import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.heuristics.AntColouringHeuristic;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();
//        customWeightedGraph.testGraph();
//        customWeightedGraph.importGraphInDIMACSFormat("D:\\GraphColouring\\instances\\myciel5.col");
//        final var dimacsDataset = customWeightedGraph.importDIMACSBenchmarkDatasetAsDirected("D:\\GraphColouring\\instances");
//        System.out.println(dimacsDataset.size());
//        for (final DefaultUndirectedWeightedGraph<String, CustomWeightedGraph.CustomWeightedEdge> graph: dimacsDataset) {
//            System.out.println(graph);
//        }
        //customWeightedGraphHelper.importGraphInDIMACSFormat("D:\\GraphColouring\\instances\\myciel3.col");
        //customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel3.col");
//        final var dimacsDataset = customWeightedGraph.importDIMACSBenchmarkDatasetAsUndirected("D:\\GraphColouring\\instances");
//        System.out.println(dimacsDataset.size());
        AntColouringHeuristic antColouringHeuristic = new AntColouringHeuristic(customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel3.col"));
        var graphColouring = antColouringHeuristic.colourTheGraph();
        for(String vertex : graphColouring.keySet()) {
            System.out.println(vertex + " kolor: " + graphColouring.get(vertex));
        }
    }
}