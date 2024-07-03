package pl.polsl.view.panels;

import pl.polsl.constants.CuckooSearchConstants;

import javax.swing.*;
import java.awt.*;

public class CuckooSearchPanel extends JPanel {

    private JLabel agentsNumberLabel = new JLabel("Number of artificial nests");
    private JFormattedTextField agentsNumberText = new JFormattedTextField();
    private JLabel iterationsLabel = new JLabel("Number of iterations to execute:");
    private JFormattedTextField iterationsText = new JFormattedTextField();
    private JLabel coloursNumLabel = new JLabel("Number of colours:");
    private JFormattedTextField coloursNumText = new JFormattedTextField();
    private JLabel randomWalkAlfaLabel = new JLabel("Lévy flight alfa (scale) parameter value:");
    private JFormattedTextField randomWalkAlfaText = new JFormattedTextField();
    private JLabel randomWalkBetaLabel = new JLabel("Lévy flight beta (distribution) parameter value:");
    private JFormattedTextField randomWalkBetaText = new JFormattedTextField();
    private JLabel parasitismProbabilityLabel = new JLabel("Parasitism occurrence probability:");
    private JFormattedTextField parasitismProbabilityText = new JFormattedTextField();
    private JLabel gaussianStandardDeviationLabel = new JLabel("Parasitism normal distribution standard deviation:");
    private JFormattedTextField gaussianStandardDeviationText = new JFormattedTextField();
    private JRadioButton validColouringsBox = new JRadioButton("Force only valid colourings", false);
    private JLabel resultsLabel = new JLabel("Results:");
    private JLabel timeLabel = new JLabel("Execution time:");
    private JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private JLabel robustnessLabel = new JLabel("Robustness:");
    private JLabel validLabel = new JLabel("Colouring is valid:");
    private JButton runButton = new JButton("Run algorithm");

    public CuckooSearchPanel() {
        setLayout(new GridLayout(21,1));
        this.agentsNumberText.setValue(CuckooSearchConstants.NUMBER_OF_AGENTS);
        this.iterationsText.setValue(CuckooSearchConstants.CUCKOO_SEARCH_MAX_ITERATIONS);
        this.coloursNumText.setValue(CuckooSearchConstants.MAXIMAL_ROBUST_COLOUR_NUMBER);
        this.randomWalkAlfaText.setValue(CuckooSearchConstants.ALFA_PROBLEM_SCALE_FACTOR);
        this.randomWalkBetaText.setValue(CuckooSearchConstants.BETA_DISTRIBUTION_INDEX_FACTOR);
        this.parasitismProbabilityText.setValue(CuckooSearchConstants.PARASITISM_OCCURRENCE_PROBABILITY);
        this.gaussianStandardDeviationText.setValue(
                CuckooSearchConstants.PARASITISM_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR);
        this.validColouringsBox.setSelected(CuckooSearchConstants.FORCE_HAVING_VALID_COLOURING);
        add(this.agentsNumberLabel);
        add(this.agentsNumberText);
        add(this.iterationsLabel);
        add(this.iterationsText);
        add(this.coloursNumLabel);
        add(this.coloursNumText);
        add(this.randomWalkAlfaLabel);
        add(this.randomWalkAlfaText);
        add(this.randomWalkBetaLabel);
        add(this.randomWalkBetaText);
        add(this.parasitismProbabilityLabel);
        add(this.parasitismProbabilityText);
        add(this.gaussianStandardDeviationLabel);
        add(this.gaussianStandardDeviationText);
        add(this.validColouringsBox);
        add(this.resultsLabel);
        add(this.timeLabel);
        add(this.cpuTimeLabel);
        add(this.robustnessLabel);
        add(this.validLabel);
        add(this.runButton);
    }
}
