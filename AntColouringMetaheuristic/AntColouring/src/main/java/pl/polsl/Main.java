package pl.polsl;

import pl.polsl.graphs.CustomWeightedGraph;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        CustomWeightedGraph customWeightedGraph = new CustomWeightedGraph();
        customWeightedGraph.testGraph();
        customWeightedGraph.importGraphInDIMACSFormat("D:\\GraphColouring\\instances\\myciel5.col");

    }
}