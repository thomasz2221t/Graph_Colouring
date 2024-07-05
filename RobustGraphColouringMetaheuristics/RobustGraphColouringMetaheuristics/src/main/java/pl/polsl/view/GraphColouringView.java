package pl.polsl.view;

import lombok.Getter;
import lombok.Setter;
import pl.polsl.constants.GraphConstants;
import pl.polsl.controller.GraphColouringController;
import pl.polsl.view.panels.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;

@Getter
@Setter
public class GraphColouringView extends JFrame implements ActionListener {

    private GraphColouringController controller;
    private final JMenuBar appMenuBar;
    private JMenu menu;
    private JMenuItem pickGraphMenu;
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
                this.graphPanel.showGraph(this.controller.graph);
            }
        }
    }

//    @Override
//    public void keyTyped(KeyEvent e) {}
//
//    @Override
//    public void keyPressed(KeyEvent e) {}
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        char c = e.getKeyChar();
//        if(c == 'z') {
//            this.graphPanel.zoomInGraph();
//        } else if(c == 'x') {
//            this.graphPanel.zoomOutGraph();
//        }
//    }

    public GraphColouringView(GraphColouringController controller) {
        super("Robust Graph Colouring");
        this.controller = controller;
        this.appMenuBar = new JMenuBar();
        this.menu = new JMenu("Menu");
        this.pickGraphMenu = new JMenuItem("Choose graph");
        this.menu.add(this.pickGraphMenu);
        this.appMenuBar.add(this.menu);
        setJMenuBar(appMenuBar);
        this.pickGraphMenu.addActionListener(this);
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
//        this.addKeyListener(this);
//        this.setFocusable(true);
//        antPanel.addKeyListener(antPanel);
//        antPanel.setFocusable(true);
//        cuckooPanel.addKeyListener(cuckooPanel);
//        cuckooPanel.setFocusable(true);
//        beesPanel.addKeyListener(beesPanel);
//        beesPanel.setFocusable(true);
//        storkPanel.addKeyListener(storkPanel);
//        storkPanel.setFocusable(true);
        setVisible(true);
    }
}
