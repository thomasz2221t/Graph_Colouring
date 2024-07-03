package pl.polsl.view.panels;

import pl.polsl.constants.StorkFeedingConstants;

import javax.swing.*;
import java.awt.*;

public class StorkFeedingPanel extends JPanel {

    private JLabel agentsNumberLabel = new JLabel("Number of artificial storks:");
    private JFormattedTextField agentsNumberText = new JFormattedTextField();
    private JLabel iterationsLabel = new JLabel("Number of iterations to execute:");
    private JFormattedTextField iterationsText = new JFormattedTextField();
    private JLabel coloursNumLabel = new JLabel("Number of colours:");
    private JFormattedTextField coloursNumText = new JFormattedTextField();
    private JLabel goodFitnessLabel = new JLabel("Fitness function for good colouring:");
    private JFormattedTextField goodFitnessText = new JFormattedTextField();
    private JLabel moderateFitnessLabel = new JLabel("Fitness function for moderate colouring:");
    private JFormattedTextField moderateFitnessText = new JFormattedTextField();
    private JLabel gaussianAnimalSightDeviationLabel = new JLabel("Animal sight normal distribution deviation:");
    private JFormattedTextField gaussianAnimalSightDeviationText = new JFormattedTextField();
    private JLabel resultsLabel = new JLabel("Results:");
    private JLabel timeLabel = new JLabel("Execution time:");
    private JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private JLabel robustnessLabel = new JLabel("Robustness:");
    private JLabel validLabel = new JLabel("Colouring is valid:");
    private JButton runButton = new JButton("Run algorithm");

    public StorkFeedingPanel() {
        setLayout(new GridLayout(18,1));
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
