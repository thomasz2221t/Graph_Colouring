package pl.polsl.view;

import lombok.Getter;
import lombok.Setter;
import pl.polsl.view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Getter
@Setter
public class GraphColouringView extends JFrame implements KeyListener {

    private GraphPanel graphPanel;
    private AntColouringPanel antPanel;
    private CuckooSearchPanel cuckooPanel;
    private BeeColouringPanel beesPanel;
    private StorkFeedingPanel storkPanel;

//    public showGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
//        Container frame = getContentPane();
//        Component graphComponent = frame.getComponent(0);
//        graphComponent.
//
//    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        if(c == 'z') {
            this.graphPanel.zoomInGraph();
        } else if(c == 'o') {
            this.graphPanel.zoomOutGraph();
        }
    }

    public GraphColouringView() {
        super();
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.X_AXIS);
        setLayout(layout);
        Box[] boxes = new Box[2];
        boxes[0] = Box.createHorizontalBox();
        boxes[1] = Box.createHorizontalBox();

        boxes[0].createGlue();
        boxes[1].createGlue();

        add(boxes[0]);
        add(boxes[1]);

        this.graphPanel = new GraphPanel();
        this.antPanel = new AntColouringPanel();
        this.cuckooPanel = new CuckooSearchPanel();
        this.beesPanel = new BeeColouringPanel();
        this.storkPanel = new StorkFeedingPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ACO", null, antPanel, "Run Ant Colony Optimisation");
        tabbedPane.addTab("CS", null, cuckooPanel, "Run Cuckoo Search");
        tabbedPane.addTab("ABC", null, beesPanel, "Run Artificial Bee Colony");
        tabbedPane.addTab("SFO", null, storkPanel, "Run Stork Feeding Optimisation");
        graphPanel.setPreferredSize(new Dimension(9000, 800));
//        graphPanel.addKeyListener(graphPanel);
//        graphPanel.setFocusable(true);
        tabbedPane.setPreferredSize(new Dimension(300, 800));
//        GridBagConstraints graphConstraints = new GridBagConstraints();
//        graphConstraints.gridwidth = 800;
//        GridBagConstraints algorithmsPaneConstraints = new GridBagConstraints();
//        algorithmsPaneConstraints.gridwidth = 400;
        boxes[0].add(graphPanel);
        boxes[1].add(tabbedPane);
        this.addKeyListener(this);
        this.setFocusable(true);
        setVisible(true);
    }
}
