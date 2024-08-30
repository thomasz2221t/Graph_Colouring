package pl.polsl.view.panels;

import pl.polsl.constants.StorkFeedingConstants;
import pl.polsl.controller.GraphColouringController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Map;

public class StorkFeedingPanel extends JPanel implements ActionListener {

    private final GraphColouringController controller;
    private final GraphPanel graphPanel;
    private final JLabel agentsNumberLabel = new JLabel("Number of artificial storks:");
    private final JFormattedTextField agentsNumberText = new JFormattedTextField();
    private final JLabel iterationsLabel = new JLabel("Number of iterations to execute:");
    private final JFormattedTextField iterationsText = new JFormattedTextField();
    private final JLabel coloursNumLabel = new JLabel("Number of colours:");
    private final JFormattedTextField coloursNumText = new JFormattedTextField();
    private final JLabel goodFitnessLabel = new JLabel("Fitness function for good colouring:");
    private final JFormattedTextField goodFitnessText = new JFormattedTextField();
    private final JLabel moderateFitnessLabel = new JLabel("Fitness function for moderate colouring:");
    private final JFormattedTextField moderateFitnessText = new JFormattedTextField();
    private final JLabel gaussianAnimalSightDeviationLabel = new JLabel("Animal sight normal distribution deviation:");
    private final JFormattedTextField gaussianAnimalSightDeviationText = new JFormattedTextField();
    private final JLabel resultsLabel = new JLabel("Results:");
    private final JLabel timeLabel = new JLabel("Execution time:");
    private final JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private final JLabel robustnessLabel = new JLabel("Robustness:");
    private final JLabel validLabel = new JLabel("Colouring is valid:");
    private final JButton runButton = new JButton("Run algorithm");

    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, Integer> colouring = this.controller.runStorkFeedingColouring((Integer) this.agentsNumberText.getValue(),
                (Long) this.iterationsText.getValue(), (Integer) this.coloursNumText.getValue(),
                (Double) this.goodFitnessText.getValue(), (Double) this.moderateFitnessText.getValue(),
                (Integer) this.gaussianAnimalSightDeviationText.getValue());
        DecimalFormat df = new DecimalFormat("#.####");
        this.timeLabel.setText("Execution time: " + df.format(this.controller.getStorkFeedingHeuristic().systemTime / Math.pow(10,9)) + "[s]");
        this.cpuTimeLabel.setText("CPU execution time: " + df.format(this.controller.getStorkFeedingHeuristic().cpuTime / Math.pow(10,9)) + "[s]");
        this.robustnessLabel.setText("Robustness: " + df.format(this.controller.getStorkFeedingHeuristic().robustness));
        this.validLabel.setText(this.controller.getStorkFeedingHeuristic().colouringValid ? "Colouring is valid" : "Colouring is invalid");
        this.graphPanel.showColouredGraph(this.controller.graph, colouring, true);
        this.graphPanel.requestFocus();
    }

    public StorkFeedingPanel(GraphColouringController controller, GraphPanel graphPanel) {
        this.controller = controller;
        this.graphPanel = graphPanel;
        setLayout(new GridLayout(22,1));
        this.runButton.addActionListener(this);
        this.agentsNumberText.setValue(StorkFeedingConstants.NUMBER_OF_AGENTS);
        this.iterationsText.setValue(StorkFeedingConstants.STORK_FEEDING_MAX_ITERATIONS);
        this.coloursNumText.setValue(StorkFeedingConstants.MAXIMAL_ROBUST_COLOUR_NUMBER);
        this.goodFitnessText.setValue(StorkFeedingConstants.GOOD_COLOURING_FITNESS);
        this.moderateFitnessText.setValue(StorkFeedingConstants.MODERATE_COLOURING_FITNESS);
        this.gaussianAnimalSightDeviationText.setValue(StorkFeedingConstants.SIGHT_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR);
        add(this.agentsNumberLabel);
        add(this.agentsNumberText);
        add(this.iterationsLabel);
        add(this.iterationsText);
        add(this.coloursNumLabel);
        add(this.coloursNumText);
        add(this.goodFitnessLabel);
        add(this.goodFitnessText);
        add(this.moderateFitnessLabel);
        add(this.moderateFitnessText);
        add(this.gaussianAnimalSightDeviationLabel);
        add(this.gaussianAnimalSightDeviationText);
        add(this.resultsLabel);
        add(this.timeLabel);
        add(this.cpuTimeLabel);
        add(this.robustnessLabel);
        add(this.validLabel);
        add(this.runButton);
    }
}
