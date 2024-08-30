package pl.polsl;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.*;
import pl.polsl.controller.GraphColouringController;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;
import pl.polsl.metaheuristics.AntColouringHeuristic;
import pl.polsl.metaheuristics.BeeColouringHeuristic;
import pl.polsl.metaheuristics.CuckooSearchHeuristic;
import pl.polsl.metaheuristics.StorkFeedingHeuristic;
import pl.polsl.view.GraphColouringView;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) {
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
/*        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("D:\\GraphColouring\\log.txt", true), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.setOut(out);
        String graph_name = "le450_5a.col";
        int chromatic_number = 5;
        int agents = 20;
        int iterations = 20000;
        int pszczoly_zwiadowcy = 8;
        int pszczoly_robotnice = agents - pszczoly_zwiadowcy;
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\"+graph_name);
        graph = customWeightedGraphHelper.imposeUncertaintyToGraph(graph,
                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);//Losowy wybór krawędzi które będą miały zmienione losowo wagi
        System.out.println("Graph name: " + graph_name);
        System.out.println("Graph vertices set number: " + graph.vertexSet().size());
        System.out.println("Graph edges set number: " + graph.edgeSet().size());
        System.out.println("Start testow");
        int counter = 0;
        for(int i = 0; i <12; i++) {
            AntColouringHeuristic antColouringHeuristic = new AntColouringHeuristic(graph);
            CuckooSearchHeuristic cuckooSearchHeuristic = new CuckooSearchHeuristic(graph);
            BeeColouringHeuristic beeColouringHeuristic = new BeeColouringHeuristic(graph);
            StorkFeedingHeuristic storkFeedingHeuristic = new StorkFeedingHeuristic(graph);
            System.out.println("=============//==================//===================");
            System.out.println("Metaheurystyka Mrowcza: " + (counter+1));
            var graphColouring = antColouringHeuristic.colourTheGraph(
                    agents,//AntColouringConstants.NUMBER_OF_AGENTS,
                    iterations,//AntColouringConstants.ANT_COLOURING_MAX_ITERATIONS,
                    chromatic_number,//AntColouringConstants.MINIMAL_ROBUST_COLOUR_NUMBER,
                    chromatic_number,//AntColouringConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
                    AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT
            );
            System.out.println("=============//==================//===================");
            System.out.println("Metaheurystyka Kukulcza: " + (counter+1));
            var cuckooColouring = cuckooSearchHeuristic.colourTheGraph(
                    agents,//CuckooSearchConstants.NUMBER_OF_AGENTS,
                    iterations,//CuckooSearchConstants.CUCKOO_SEARCH_MAX_ITERATIONS,
                    chromatic_number,//CuckooSearchConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
                    CuckooSearchConstants.ALFA_PROBLEM_SCALE_FACTOR,
                    CuckooSearchConstants.BETA_DISTRIBUTION_INDEX_FACTOR,
                    CuckooSearchConstants.PARASITISM_OCCURRENCE_PROBABILITY,
                    CuckooSearchConstants.PARASITISM_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR,
                    CuckooSearchConstants.FORCE_HAVING_VALID_COLOURING
            );
            System.out.println("=============//==================//===================");
            System.out.println("Metaheurystyka Pszczela " + (counter+1));
            var beesColouring = beeColouringHeuristic.colourTheGraph(
                    pszczoly_robotnice,// BeeColouringConstants.NUMBER_OF_BEE_WORKERS,
                    pszczoly_zwiadowcy,//BeeColouringConstants.NUMBER_OF_BEE_SCOUTS,
                    iterations,//BeeColouringConstants.BEE_COLOURING_MAX_ITERATIONS,
                    chromatic_number,//BeeColouringConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
                    BeeColouringConstants.FEEDING_REGION_DEPTH,
                    BeeColouringConstants.WORKER_OPERATIONAL_ITERATION_NUMBER,
                    BeeColouringConstants.SCOUT_OPERATIONAL_ITERATION_NUMBER,
                    BeeColouringConstants.HIVES_SHUFFLE_ITERATION_PERIOD
            );
            System.out.println("=============//==================//===================");
            System.out.println("Metaheurystyka Bociania " + (counter+1));
            var storkColouring = storkFeedingHeuristic.colourTheGraph(
                    agents, //StorkFeedingConstants.NUMBER_OF_AGENTS,
                    iterations,//StorkFeedingConstants.STORK_FEEDING_MAX_ITERATIONS,
                    chromatic_number, //StorkFeedingConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
                    StorkFeedingConstants.GOOD_COLOURING_FITNESS,
                    StorkFeedingConstants.MODERATE_COLOURING_FITNESS,
                    StorkFeedingConstants.SIGHT_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR
            );
            System.out.println("=============//==================//===================");
            counter++;
        }*/
//        graph_name = "inithx.i.3.col";
//        graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted("D:\\GraphColouring\\instances\\"+graph_name);
//        graph = customWeightedGraphHelper.imposeUncertaintyToGraph(graph,
//                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
//                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);//Losowy wybór krawędzi które będą miały zmienione losowo wagi
//        System.out.println("Graph name: " + graph_name);
//        System.out.println("Graph vertices set number: " + graph.vertexSet().size());
//        System.out.println("Graph edges set number: " + graph.edgeSet().size());
//        System.out.println("Start testow");
//        counter = 0;
//        for(int i = 0; i <12; i++) {
//            AntColouringHeuristic antColouringHeuristic = new AntColouringHeuristic(graph);
//            CuckooSearchHeuristic cuckooSearchHeuristic = new CuckooSearchHeuristic(graph);
//            BeeColouringHeuristic beeColouringHeuristic = new BeeColouringHeuristic(graph);
//            StorkFeedingHeuristic storkFeedingHeuristic = new StorkFeedingHeuristic(graph);
//            System.out.println("=============//==================//===================");
//            System.out.println("Metaheurystyka Mrowcza: " + (counter+1));
//            var graphColouring = antColouringHeuristic.colourTheGraph(
//                    agents,//AntColouringConstants.NUMBER_OF_AGENTS,
//                    iterations,//AntColouringConstants.ANT_COLOURING_MAX_ITERATIONS,
//                    chromatic_number,//AntColouringConstants.MINIMAL_ROBUST_COLOUR_NUMBER,
//                    chromatic_number,//AntColouringConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
//                    AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT
//            );
//            System.out.println("=============//==================//===================");
//            System.out.println("Metaheurystyka Kukulcza: " + (counter+1));
//            var cuckooColouring = cuckooSearchHeuristic.colourTheGraph(
//                    agents,//CuckooSearchConstants.NUMBER_OF_AGENTS,
//                    iterations,//CuckooSearchConstants.CUCKOO_SEARCH_MAX_ITERATIONS,
//                    chromatic_number,//CuckooSearchConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
//                    CuckooSearchConstants.ALFA_PROBLEM_SCALE_FACTOR,
//                    CuckooSearchConstants.BETA_DISTRIBUTION_INDEX_FACTOR,
//                    CuckooSearchConstants.PARASITISM_OCCURRENCE_PROBABILITY,
//                    CuckooSearchConstants.PARASITISM_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR,
//                    CuckooSearchConstants.FORCE_HAVING_VALID_COLOURING
//            );
//            System.out.println("=============//==================//===================");
//            System.out.println("Metaheurystyka Pszczela " + (counter+1));
//            var beesColouring = beeColouringHeuristic.colourTheGraph(
//                    pszczoly_robotnice,// BeeColouringConstants.NUMBER_OF_BEE_WORKERS,
//                    pszczoly_zwiadowcy,//BeeColouringConstants.NUMBER_OF_BEE_SCOUTS,
//                    iterations,//BeeColouringConstants.BEE_COLOURING_MAX_ITERATIONS,
//                    chromatic_number,//BeeColouringConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
//                    BeeColouringConstants.FEEDING_REGION_DEPTH,
//                    BeeColouringConstants.WORKER_OPERATIONAL_ITERATION_NUMBER,
//                    BeeColouringConstants.SCOUT_OPERATIONAL_ITERATION_NUMBER,
//                    BeeColouringConstants.HIVES_SHUFFLE_ITERATION_PERIOD
//            );
//            System.out.println("=============//==================//===================");
//            System.out.println("Metaheurystyka Bociania " + (counter+1));
//            var storkColouring = storkFeedingHeuristic.colourTheGraph(
//                    agents, //StorkFeedingConstants.NUMBER_OF_AGENTS,
//                    iterations,//StorkFeedingConstants.STORK_FEEDING_MAX_ITERATIONS,
//                    chromatic_number, //StorkFeedingConstants.MAXIMAL_ROBUST_COLOUR_NUMBER,
//                    StorkFeedingConstants.GOOD_COLOURING_FITNESS,
//                    StorkFeedingConstants.MODERATE_COLOURING_FITNESS,
//                    StorkFeedingConstants.SIGHT_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR
//            );
//            System.out.println("=============//==================//===================");
//            counter++;
//        }
//        out.close();
    }
}