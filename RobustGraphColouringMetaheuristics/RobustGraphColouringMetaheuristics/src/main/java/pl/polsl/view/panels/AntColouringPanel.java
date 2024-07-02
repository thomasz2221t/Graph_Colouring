package pl.polsl.view.panels;

import pl.polsl.constants.AntColouringConstants;

import javax.swing.*;
import java.awt.*;

public class AntColouringPanel extends JPanel {

    private JLabel agentsNumberLabel = new JLabel("Number of artificial ants:");
    private JFormattedTextField agentsNumberText = new JFormattedTextField();
    private JLabel iterationsLabel = new JLabel("Number of iterations to execute:");
    private JFormattedTextField iterationsText = new JFormattedTextField();
    private JLabel coloursNumLabel = new JLabel("Number of colours:");
    private JFormattedTextField coloursNumText = new JFormattedTextField();
    private JLabel pheromoneEvaporationLabel = new JLabel("Pheromone evaporation factor:");
    private JFormattedTextField pheromoneEvaporationText = new JFormattedTextField();
    private JLabel resultsLabel = new JLabel("Results:");
    private JLabel timeLabel = new JLabel("Execution time:");
    private JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private JLabel robustnessLabel = new JLabel("Robustness:");
    private JLabel validLabel = new JLabel("Colouring is valid:");
    private JButton runButton = new JButton("Run algorithm");

    public AntColouringPanel() {
        setLayout(new GridLayout(14,1));
        this.runButton.setPreferredSize(new Dimension(100,50));
        this.agentsNumberText.setText(String.valueOf(AntColouringConstants.NUMBER_OF_AGENTS));
        this.iterationsText.setText(String.valueOf(AntColouringConstants.ANT_COLOURING_MAX_ITERATIONS));
        this.coloursNumText.setText(String.valueOf(AntColouringConstants.MINIMAL_ROBUST_COLOUR_NUMBER));
        this.pheromoneEvaporationText.setText(String.valueOf(AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT));
        add(this.agentsNumberLabel);
        add(this.agentsNumberText);
        add(this.iterationsLabel);
        add(this.iterationsText);
        add(this.coloursNumLabel);
        add(this.coloursNumText);
        add(this.pheromoneEvaporationLabel);
        add(this.pheromoneEvaporationText);
        add(this.resultsLabel);
        add(this.timeLabel);
        add(this.cpuTimeLabel);
        add(this.robustnessLabel);
        add(this.validLabel);
        add(this.runButton);
    }
}
