package pl.polsl.view.panels;

import com.mxgraph.swing.mxGraphComponent;
import lombok.Getter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

@Getter
public class GraphPanel extends JPanel implements KeyListener {

    private mxGraphComponent graphVisualisation;
    private CustomWeightedGraphHelper graphHelper = new CustomWeightedGraphHelper();
    private Map<String, Integer> verticesColourMap = new HashMap<>();

    public void showGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, boolean showEdgeWeights) {
        this.verticesColourMap = new HashMap<>();
        this.graphVisualisation = graphHelper.showingGraphInView(graph, showEdgeWeights);
        //mxIGraphLayout layout = new mxHierarchicalLayout(graph);
        this.setLayout(new BorderLayout());
        this.setVisible(false);
        this.removeAll();
        this.add(this.graphVisualisation);
        this.setVisible(true);
    }

    public void showColouredGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, boolean showEdgeWeights) {
        this.verticesColourMap = verticesColourMap;
        this.graphVisualisation = graphHelper.showingColouredGraphInView(graph, verticesColourMap, showEdgeWeights);
        graphHelper.savingColouredGraphVisualizationToFile(graph, verticesColourMap, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY + "coloured.png", true);
        this.setLayout(new BorderLayout());
        this.setVisible(false);
        this.removeAll();
        this.add(this.graphVisualisation);
        this.setVisible(true);
    }

    public void saveVisualisationToFile(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, String directory, boolean showEdgeWeights) {
        if(this.verticesColourMap.size() > 0) {
            this.graphHelper.savingColouredGraphVisualizationToFile(graph, this.verticesColourMap, directory, showEdgeWeights);
        } else {
            this.graphHelper.savingGraphVisualizationToFile(graph, directory, showEdgeWeights);
        }
    }

    public void zoomInGraph(){
        this.setVisible(false);
        this.removeAll();
        this.graphVisualisation.zoomIn();
        this.add(this.graphVisualisation);
        this.setVisible(true);
    }

    public void zoomOutGraph(){
        this.setVisible(false);
        this.removeAll();
        this.graphVisualisation.zoomOut();
        this.add(graphVisualisation);
        this.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
//        System.out.println(c);
        if(c == 'z') {
            this.graphVisualisation.zoomIn();
        } else if(c == 'x') {
            this.graphVisualisation.zoomOut();
        }
        this.add(graphVisualisation);
    }

    public GraphPanel() {
        addKeyListener(this);
        setFocusable(true);
//        setBackground(Color.GREEN);
//        setPreferredSize(new Dimension(800, 700));
    }
}
