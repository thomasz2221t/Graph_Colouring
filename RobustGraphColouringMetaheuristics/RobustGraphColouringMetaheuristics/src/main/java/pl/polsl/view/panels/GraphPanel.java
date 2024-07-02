package pl.polsl.view.panels;

import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.graphs.CustomWeightedGraphHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GraphPanel extends JPanel implements KeyListener {

    private mxGraphComponent graphVisu;
    private CustomWeightedGraphHelper graphHelper = new CustomWeightedGraphHelper();

    public void showGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        graphVisu = graphHelper.showingGraphInView(graph);
        //mxIGraphLayout layout = new mxHierarchicalLayout(graph);
        this.setLayout(new BorderLayout());
        this.add(graphVisu);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println(c);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println(c);
        if(c == 'z') {
            this.graphVisu.zoomIn();
        } else if(c == 'o') {
            this.graphVisu.zoomOut();
        }
        this.add(graphVisu);
    }

    public GraphPanel() {
//        addKeyListener(this);
//        setFocusable(true);
        setBackground(Color.GREEN);
//        setPreferredSize(new Dimension(800, 800));
    }
}
