package pl.polsl.view.panels;

import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

public class GraphPanel extends JPanel implements KeyListener {

    private mxGraphComponent graphVisu;
    private CustomWeightedGraphHelper graphHelper = new CustomWeightedGraphHelper();

    public void showGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graphVisu = graphHelper.showingGraphInView(graph);
        //mxIGraphLayout layout = new mxHierarchicalLayout(graph);
        this.setLayout(new BorderLayout());
        this.add(this.graphVisu);
    }

    public void showColouredGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap) {
        this.graphVisu = graphHelper.showingColouredGraphInView(graph, verticesColourMap);
        graphHelper.savingColouredGraphVisualizationToFile(graph, verticesColourMap, GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY + "coloured.png");
        this.setLayout(new BorderLayout());
        this.setVisible(false);
        this.removeAll();
        this.add(this.graphVisu);
        this.setVisible(true);
    }

//    public void zoomInGraph(){
//        this.setVisible(false);
//        this.removeAll();
//        this.graphVisu.zoomIn();
//        this.add(this.graphVisu);
//        this.setVisible(true);
//    }
//
//    public void zoomOutGraph(){
//        this.setVisible(false);
//        this.removeAll();
//        this.graphVisu.zoomOut();
//        this.add(graphVisu);
//        this.setVisible(true);
//    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
//        System.out.println(c);
        if(c == 'z') {
            this.graphVisu.zoomIn();
        } else if(c == 'x') {
            this.graphVisu.zoomOut();
        }
        this.add(graphVisu);
    }

    public GraphPanel() {
        addKeyListener(this);
        setFocusable(true);
//        setBackground(Color.GREEN);
//        setPreferredSize(new Dimension(800, 700));
    }
}
