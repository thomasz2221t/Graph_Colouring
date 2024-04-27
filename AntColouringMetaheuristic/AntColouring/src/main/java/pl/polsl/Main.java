package pl.polsl;

import pl.polsl.graphs.CustomWeightedGraph;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        CustomWeightedGraph customWeightedGraph = new CustomWeightedGraph();
//        customWeightedGraph.testGraph();
//        customWeightedGraph.importGraphInDIMACSFormat("D:\\GraphColouring\\instances\\myciel5.col");
//        final var dimacsDataset = customWeightedGraph.importDIMACSBenchmarkDatasetAsDirected("D:\\GraphColouring\\instances");
//        System.out.println(dimacsDataset.size());
//        for (final DefaultUndirectedWeightedGraph<String, CustomWeightedGraph.CustomWeightedEdge> graph: dimacsDataset) {
//            System.out.println(graph);
//        }
        customWeightedGraph.importGraphInDIMACSFormat("D:\\GraphColouring\\instances\\myciel3.col");
        customWeightedGraph.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel3.col");
//        final var dimacsDataset = customWeightedGraph.importDIMACSBenchmarkDatasetAsUndirected("D:\\GraphColouring\\instances");
//        System.out.println(dimacsDataset.size());
    }
}