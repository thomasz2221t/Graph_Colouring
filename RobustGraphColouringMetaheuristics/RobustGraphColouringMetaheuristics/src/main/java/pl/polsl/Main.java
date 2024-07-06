package pl.polsl;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.GraphConstants;
import pl.polsl.controller.GraphColouringController;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;
import pl.polsl.view.GraphColouringView;

import java.awt.*;

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
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\anna.col");
        graph = customWeightedGraphHelper.imposeUncertaintyToGraph(graph,
                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);//Losowy wybór krawędzi które będą miały zmienione losowo wagi
        //only for testing
        customWeightedGraphHelper.savingGraphVisualizationToFile(graph, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY + "uncertainty.png", true);
        //DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel3.col");
        System.out.println("Graph vertices set number: " + graph.vertexSet().size());
        System.out.println("Graph edges set number: " + graph.edgeSet().size());
        //============================================Mrówkowa heurysytka===============================================//
//        AntColouringHeuristic antColouringHeuristic = new AntColouringHeuristic(graph);
//        var graphColouring = antColouringHeuristic.colourTheGraph();
//        for(String vertex : graphColouring.keySet()) {
//            System.out.println(vertex + " kolor: " + graphColouring.get(vertex));
//        }
        //============================================Kukułcza heurysytka===============================================//
//        CuckooSearchHeuristic cuckooSearchHeuristic = new CuckooSearchHeuristic(graph);
//        var cuckooColouring = cuckooSearchHeuristic.colourTheGraph();
//        for(String vertex : cuckooColouring.keySet()) {
//            System.out.println(vertex + " kolor: " + cuckooColouring.get(vertex));
//        }
        //============================================Pszczela heurysytka===============================================//
//        BeeColouringHeuristic beeColouringHeuristic = new BeeColouringHeuristic(graph);
//        var beesColouring = beeColouringHeuristic.colourTheGraph();
//        for(String vertex : beesColouring.keySet()) {
//            System.out.println(vertex + " kolor: " + beesColouring.get(vertex));
//        }
        //============================================Bociania heurysytka===============================================//
//        StorkFeedingHeuristic storkFeedingHeuristic = new StorkFeedingHeuristic(graph);
//        var storkColouring = storkFeedingHeuristic.colourTheGraph();
//        for(String vertex : storkColouring.keySet()) {
//            System.out.println(vertex + " kolor: " + storkColouring.get(vertex));
//        }
//        var graphVizu = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\myciel4.col");
//        customWeightedGraphHelper.savingGraphVisualizationToFile(graphVizu, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"prezka0.png");
//        graphVizu = customWeightedGraphHelper.imposeUncertaintyToGraph(graphVizu, GraphConstants.PROPORTION_EDGES_TO_FUZZ, GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);
//        customWeightedGraphHelper.savingGraphVisualizationToFile(graphVizu, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"prezka1.png");

        EventQueue.invokeLater(new Runnable() {
            DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;

            @Override
            public void run() {
                GraphColouringController controller = new GraphColouringController(this.graph);
                GraphColouringView view = new GraphColouringView(controller);
                view.getGraphPanel().showGraph(this.graph, true);
                //view.showGraph(graph);
            }

            public Runnable init(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
                this.graph=graph;
                return(this);
            }
        }.init(graph));
    }
}