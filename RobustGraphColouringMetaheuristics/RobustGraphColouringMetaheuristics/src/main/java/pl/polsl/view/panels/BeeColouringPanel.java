package pl.polsl.view.panels;

import pl.polsl.constants.BeeColouringConstants;
import pl.polsl.controller.GraphColouringController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class BeeColouringPanel extends JPanel implements ActionListener {

    private GraphColouringController controller;
    private GraphPanel graphPanel;
    private JLabel workersNumberLabel = new JLabel("Number of bees workers:");
    private JFormattedTextField workersNumberText = new JFormattedTextField();
    private JLabel scoutsNumberLabel = new JLabel("Number of bees scouts:");
    private JFormattedTextField scoutsNumberText = new JFormattedTextField();
    private JLabel iterationsLabel = new JLabel("Number of iteration to execute:");
    private JFormattedTextField iterationsText = new JFormattedTextField();
    private JLabel colourNumberLabel = new JLabel("Number of colours:");
    private JFormattedTextField colourNumberText = new JFormattedTextField();
    private JLabel feedingRegionDepthLabel = new JLabel("Depth of feeding region:");
    private JFormattedTextField feedingRegionDepthText = new JFormattedTextField();
    private JLabel workerOperationsLabel = new JLabel("Number of workers' operations:");
    private JFormattedTextField workerOperationsText = new JFormattedTextField();
    private JLabel scoutOperationsLabel = new JLabel("Number of scouts' operations:");
    private JFormattedTextField scoutOperationsText = new JFormattedTextField();
    private JLabel hiveShuffleLabel = new JLabel("Hive shuffle iteration period:");
    private JFormattedTextField hiveShuffleText = new JFormattedTextField();
    private JLabel resultsLabel = new JLabel("Results:");
    private JLabel timeLabel = new JLabel("Execution time:");
    private JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private JLabel robustnessLabel = new JLabel("Robustness:");
    private JLabel validLabel = new JLabel("Colouring is valid:");
    private JButton runButton = new JButton("Run algorithm");

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Przycisk pszczoły");
        Map<String, Integer> colouring = this.controller.runBeeColouring();
        this.graphPanel.showColouredGraph(this.controller.graph, colouring);
        this.graphPanel.requestFocus();
    }

    public BeeColouringPanel(GraphColouringController controller, GraphPanel graphPanel) {
        this.controller = controller;
        this.graphPanel = graphPanel;
        setLayout(new GridLayout(22,1));
        runButton.addActionListener(this);
        this.workersNumberText.setValue(BeeColouringConstants.NUMBER_OF_BEE_WORKERS);
        this.scoutsNumberText.setValue(BeeColouringConstants.NUMBER_OF_BEE_SCOUTS);
        this.iterationsText.setValue(BeeColouringConstants.BEE_COLOURING_MAX_ITERATIONS);
        this.colourNumberText.setValue(BeeColouringConstants.MAXIMAL_ROBUST_COLOUR_NUMBER);
        this.feedingRegionDepthText.setValue(BeeColouringConstants.FEEDING_REGION_DEPTH);
        this.workerOperationsText.setValue(BeeColouringConstants.WORKER_OPERATIONAL_ITERATION_NUMBER);
        this.scoutOperationsText.setValue(BeeColouringConstants.SCOUT_OPERATIONAL_ITERATION_NUMBER);
        this.hiveShuffleText.setValue(BeeColouringConstants.HIVES_SHUFFLE_ITERATION_PERIOD);
        add(this.workersNumberLabel);
        add(this.workersNumberText);
        add(this.scoutsNumberLabel);
        add(this.scoutsNumberText);
        add(this.iterationsLabel);
        add(this.iterationsText);
        add(this.colourNumberLabel);
        add(this.colourNumberText);
        add(this.feedingRegionDepthLabel);
        add(this.feedingRegionDepthText);
        add(this.workerOperationsLabel);
        add(this.workerOperationsText);
        add(this.scoutOperationsLabel);
        add(this.scoutOperationsText);
        add(this.hiveShuffleLabel);
        add(this.hiveShuffleText);
        add(this.resultsLabel);
        add(this.timeLabel);
        add(this.cpuTimeLabel);
        add(this.robustnessLabel);
        add(this.validLabel);
        add(this.runButton);
    }
}
