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
        CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();
        System.out.println(GraphConstants.DIMACS_DATASET_DIRECTORY);
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted(GraphConstants.DIMACS_DATASET_DIRECTORY + "\\myciel5.col");
        graph = customWeightedGraphHelper.imposeUncertaintyToGraph(graph,
                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);

        EventQueue.invokeLater(new Runnable() {
            DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;

            @Override
            public void run() {
                GraphColouringController controller = new GraphColouringController(this.graph);
                GraphColouringView view = new GraphColouringView(controller);
                view.getGraphPanel().showGraph(this.graph, true);
            }

            public Runnable init(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
                this.graph=graph;
                return(this);
            }
        }.init(graph));
    }
}