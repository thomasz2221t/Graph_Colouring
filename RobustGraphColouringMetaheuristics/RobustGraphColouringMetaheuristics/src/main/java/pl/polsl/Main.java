package pl.polsl;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.metaheuristics.AntColouringHeuristic;
import pl.polsl.metaheuristics.BeeColouringHeuristic;
import pl.polsl.metaheuristics.CuckooSearchHeuristic;

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
        //============================================Pszczela heurysytka===============================================//
        BeeColouringHeuristic beeColouringHeuristic = new BeeColouringHeuristic(graph);
        var beesColouring = beeColouringHeuristic.colourTheGraph();
        for(String vertex : beesColouring.keySet()) {
            System.out.println(vertex + " kolor: " + beesColouring.get(vertex));
        }
        var graphVizu = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel4.col");
        customWeightedGraphHelper.savingGraphVisualizationToFile(graphVizu, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"prezka0.png");
        graphVizu = customWeightedGraphHelper.imposeUncertaintyToGraph(graphVizu, GraphConstants.PROPORTION_EDGES_TO_FUZZ, GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);
        customWeightedGraphHelper.savingGraphVisualizationToFile(graphVizu, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"prezka1.png");
    }
}