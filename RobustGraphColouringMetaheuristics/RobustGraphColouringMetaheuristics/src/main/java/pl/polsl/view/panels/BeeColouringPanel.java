package pl.polsl.view.panels;

import pl.polsl.constants.BeeColouringConstants;
import pl.polsl.controller.GraphColouringController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Map;

public class BeeColouringPanel extends JPanel implements ActionListener {

    private final GraphColouringController controller;
    private final GraphPanel graphPanel;
    private final JLabel workersNumberLabel = new JLabel("Number of bees workers:");
    private final JFormattedTextField workersNumberText = new JFormattedTextField();
    private final JLabel scoutsNumberLabel = new JLabel("Number of bees scouts:");
    private final JFormattedTextField scoutsNumberText = new JFormattedTextField();
    private final JLabel iterationsLabel = new JLabel("Number of iteration to execute:");
    private final JFormattedTextField iterationsText = new JFormattedTextField();
    private final JLabel colourNumberLabel = new JLabel("Number of colours:");
    private final JFormattedTextField colourNumberText = new JFormattedTextField();
    private final JLabel feedingRegionDepthLabel = new JLabel("Depth of feeding region:");
    private final JFormattedTextField feedingRegionDepthText = new JFormattedTextField();
    private final JLabel workerOperationsLabel = new JLabel("Number of workers' operations:");
    private final JFormattedTextField workerOperationsText = new JFormattedTextField();
    private final JLabel scoutOperationsLabel = new JLabel("Number of scouts' operations:");
    private final JFormattedTextField scoutOperationsText = new JFormattedTextField();
    private final JLabel hiveShuffleLabel = new JLabel("Hive shuffle iteration period:");
    private final JFormattedTextField hiveShuffleText = new JFormattedTextField();
    private final JLabel resultsLabel = new JLabel("Results:");
    private final JLabel timeLabel = new JLabel("Execution time:");
    private final JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private final JLabel robustnessLabel = new JLabel("Robustness:");
    private final JLabel validLabel = new JLabel("Colouring is valid:");
    private final JButton runButton = new JButton("Run algorithm");

    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, Integer> colouring = this.controller.runBeeColouring((Integer) this.workersNumberText.getValue(),
                (Integer) this.scoutsNumberText.getValue(), (Long) this.iterationsText.getValue(),
                (Integer) this.colourNumberText.getValue(), (Integer) this.feedingRegionDepthText.getValue(),
                (Integer) this.workerOperationsText.getValue(), (Integer) this.scoutOperationsText.getValue(),
                (Integer) this.hiveShuffleText.getValue());
        DecimalFormat df = new DecimalFormat("#.####");
        this.timeLabel.setText("Execution time: " + df.format(this.controller.getBeeColouringHeuristic().systemTime / Math.pow(10,9)) + "[s]");
        this.cpuTimeLabel.setText("CPU execution time: " + df.format(this.controller.getBeeColouringHeuristic().cpuTime / Math.pow(10,9)) + "[s]");
        this.robustnessLabel.setText("Robustness: " + df.format(this.controller.getBeeColouringHeuristic().robustness));
        this.validLabel.setText(this.controller.getBeeColouringHeuristic().colouringValid ? "Colouring is valid" : "Colouring is invalid");
        this.graphPanel.showColouredGraph(this.controller.graph, colouring, true);
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
