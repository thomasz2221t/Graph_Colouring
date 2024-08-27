package pl.polsl.view;

import lombok.Getter;
import lombok.Setter;
import pl.polsl.controller.GraphColouringController;
import pl.polsl.view.panels.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Objects;

@Getter
@Setter
public class GraphColouringView extends JFrame implements ActionListener, KeyListener {

    private GraphColouringController controller;
    private final JMenuBar appMenuBar;
    private JMenu menu, viewMenu, graphWeights;
    private JMenuItem pickGraphMenu, saveVisualisationToFile, showWeightsMenu, hideWeightsMenu;
    private GraphPanel graphPanel;
    private AntColouringPanel antPanel;
    private CuckooSearchPanel cuckooPanel;
    private BeeColouringPanel beesPanel;
    private StorkFeedingPanel storkPanel;
    private boolean showEdgeWeights = true;

//    public showGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
//        Container frame = getContentPane();
//        Component graphComponent = frame.getComponent(0);
//        graphComponent.
//
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if(Objects.equals(e.getActionCommand(), "Choose graph")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".col Graph Files", "col");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                this.controller.importGraphFromPath(chooser.getSelectedFile().getAbsolutePath());
                this.graphPanel.showGraph(this.controller.graph, true);
            }
        } else if(Objects.equals(e.getActionCommand(), "Save visualisation to file")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".png Graphical Files", "png");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String fileName = chooser.getSelectedFile().getName().isEmpty() ? "graph.png" : chooser.getSelectedFile().getName() + ".png";
                this.graphPanel.saveVisualisationToFile(this.controller.graph, chooser.getCurrentDirectory().getAbsolutePath() + File.separator + fileName, this.showEdgeWeights);
            }
        } else if(Objects.equals(e.getActionCommand(), "Show edge weights")) {
            this.showEdgeWeights = true;
            if(this.graphPanel.getVerticesColourMap().size() > 0) {
                this.graphPanel.showColouredGraph(this.controller.graph, this.graphPanel.getVerticesColourMap(), true);
            } else {
                this.graphPanel.showGraph(this.controller.graph, true);
            }

        } else if(Objects.equals(e.getActionCommand(), "Hide edge weights")) {
            this.showEdgeWeights = false;
            if(this.graphPanel.getVerticesColourMap().size() > 0) {
                this.graphPanel.showColouredGraph(this.controller.graph, this.graphPanel.getVerticesColourMap(), false);
            } else {
                this.graphPanel.showGraph(this.controller.graph, false);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        if(c == 'z') {
            this.graphPanel.zoomInGraph();
        } else if(c == 'x') {
            this.graphPanel.zoomOutGraph();
        }
    }

    public GraphColouringView(GraphColouringController controller) {
        super("Robust Graph Colouring");
        this.controller = controller;
        this.appMenuBar = new JMenuBar();
        this.menu = new JMenu("Menu");
        this.viewMenu = new JMenu("Graph View");
        this.graphWeights = new JMenu("Edit edge weights");
        this.pickGraphMenu = new JMenuItem("Choose graph");
        this.saveVisualisationToFile = new JMenuItem("Save visualisation to file");
        this.showWeightsMenu = new JMenuItem("Show edge weights");
        this.hideWeightsMenu = new JMenuItem("Hide edge weights");
        this.graphWeights.add(this.showWeightsMenu);
        this.graphWeights.add(this.hideWeightsMenu);
        this.menu.add(this.pickGraphMenu);
        this.menu.add(this.saveVisualisationToFile);
        this.viewMenu.add(this.graphWeights);
        this.appMenuBar.add(this.menu);
        this.appMenuBar.add(this.viewMenu);
        this.setJMenuBar(appMenuBar);
        this.pickGraphMenu.addActionListener(this);
        this.saveVisualisationToFile.addActionListener(this);
        this.showWeightsMenu.addActionListener(this);
        this.hideWeightsMenu.addActionListener(this);
        this.setSize(1200, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.X_AXIS);
        this.setLayout(layout);
        Box[] boxes = new Box[2];
        boxes[0] = Box.createHorizontalBox();
        boxes[1] = Box.createHorizontalBox();

        boxes[0].createGlue();
        boxes[1].createGlue();

        this.add(boxes[0]);
        this.add(boxes[1]);

        this.graphPanel = new GraphPanel();
        this.antPanel = new AntColouringPanel(controller, this.graphPanel);
        this.cuckooPanel = new CuckooSearchPanel(controller, this.graphPanel);
        this.beesPanel = new BeeColouringPanel(controller, this.graphPanel);
        this.storkPanel = new StorkFeedingPanel(controller, this.graphPanel);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ACO", null, this.antPanel, "Run Ant Colony Optimisation");
        tabbedPane.addTab("CS", null, this.cuckooPanel, "Run Cuckoo Search");
        tabbedPane.addTab("ABC", null, this.beesPanel, "Run Artificial Bee Colony");
        tabbedPane.addTab("SFO", null, this.storkPanel, "Run Stork Feeding Optimisation");
        this.graphPanel.setPreferredSize(new Dimension(9000, 800));
        this.graphPanel.requestFocus();
//        graphPanel.addKeyListener(graphPanel);
//        graphPanel.setFocusable(true);
        tabbedPane.setPreferredSize(new Dimension(300, 800));
//        GridBagConstraints graphConstraints = new GridBagConstraints();
//        graphConstraints.gridwidth = 800;
//        GridBagConstraints algorithmsPaneConstraints = new GridBagConstraints();
//        algorithmsPaneConstraints.gridwidth = 400;
        boxes[0].add(this.graphPanel);
        boxes[1].add(tabbedPane);
        this.addKeyListener(this);
        antPanel.addKeyListener(this);
        cuckooPanel.addKeyListener(this);
        beesPanel.addKeyListener(this);
        storkPanel.addKeyListener(this);

        this.setVisible(true);
    }
}
