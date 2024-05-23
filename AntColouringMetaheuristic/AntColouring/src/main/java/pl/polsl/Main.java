package pl.polsl;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.heuristics.AntColouringHeuristic;
import pl.polsl.heuristics.CuckooSearchHeuristic;

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
        DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel3.col");
        //============================================Mrówkowa heurysytka===============================================//
        AntColouringHeuristic antColouringHeuristic = new AntColouringHeuristic(graph);
        var graphColouring = antColouringHeuristic.colourTheGraph();
        for(String vertex : graphColouring.keySet()) {
            System.out.println(vertex + " kolor: " + graphColouring.get(vertex));
        }
        //============================================Kukułcza heurysytka===============================================//
        CuckooSearchHeuristic cuckooSearchHeuristic = new CuckooSearchHeuristic(graph);
        var cuckooColouring = cuckooSearchHeuristic.colourTheGraph();
        for(String vertex : cuckooColouring.keySet()) {
            System.out.println(vertex + " kolor: " + cuckooColouring.get(vertex));
        }
    }
}