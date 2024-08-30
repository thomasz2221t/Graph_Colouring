package pl.polsl.view.panels;

import pl.polsl.constants.CuckooSearchConstants;
import pl.polsl.controller.GraphColouringController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Map;

public class CuckooSearchPanel extends JPanel implements ActionListener {

    private final GraphColouringController controller;
    private final GraphPanel graphPanel;
    private final JLabel agentsNumberLabel = new JLabel("Number of artificial nests");
    private final JFormattedTextField agentsNumberText = new JFormattedTextField();
    private final JLabel iterationsLabel = new JLabel("Number of iterations to execute:");
    private final JFormattedTextField iterationsText = new JFormattedTextField();
    private final JLabel coloursNumLabel = new JLabel("Number of colours:");
    private final JFormattedTextField coloursNumText = new JFormattedTextField();
    private final JLabel randomWalkAlfaLabel = new JLabel("Lévy flight alfa (scale) parameter value:");
    private final JFormattedTextField randomWalkAlfaText = new JFormattedTextField();
    private final JLabel randomWalkBetaLabel = new JLabel("Lévy flight beta (distribution) parameter value:");
    private final JFormattedTextField randomWalkBetaText = new JFormattedTextField();
    private final JLabel parasitismProbabilityLabel = new JLabel("Parasitism occurrence probability:");
    private final JFormattedTextField parasitismProbabilityText = new JFormattedTextField();
    private final JLabel gaussianStandardDeviationLabel = new JLabel("Parasitism normal distribution standard deviation:");
    private final JFormattedTextField gaussianStandardDeviationText = new JFormattedTextField();
    private final JRadioButton validColouringsBox = new JRadioButton("Force only valid colourings", false);
    private final JLabel resultsLabel = new JLabel("Results:");
    private final JLabel timeLabel = new JLabel("Execution time:");
    private final JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private final JLabel robustnessLabel = new JLabel("Robustness:");
    private final JLabel validLabel = new JLabel("Colouring is valid:");
    private final JButton runButton = new JButton("Run algorithm");

    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, Integer> colouring = this.controller.runCuckooSearch((Integer) this.agentsNumberText.getValue(),
                (Long) this.iterationsText.getValue(), (Integer) this.coloursNumText.getValue(), (Double) this.randomWalkAlfaText.getValue(),
                (Double) this.randomWalkBetaText.getValue(), (Double) this.parasitismProbabilityText.getValue(),
                (Integer) this.gaussianStandardDeviationText.getValue(), this.validColouringsBox.isSelected());
        DecimalFormat df = new DecimalFormat("#.####");
        this.timeLabel.setText("Execution time: " + df.format(this.controller.getCuckooSearchHeuristic().systemTime / Math.pow(10,9)) + "[s]");
        this.cpuTimeLabel.setText("CPU execution time: " + df.format(this.controller.getCuckooSearchHeuristic().cpuTime / Math.pow(10,9)) + "[s]");
        this.robustnessLabel.setText("Robustness: " + df.format(this.controller.getCuckooSearchHeuristic().robustness));
        this.validLabel.setText(this.controller.getCuckooSearchHeuristic().colouringValid ? "Colouring is valid" : "Colouring is invalid");
        this.graphPanel.showColouredGraph(this.controller.graph, colouring, true);
        this.graphPanel.requestFocus();
    }

    public CuckooSearchPanel(GraphColouringController controller, GraphPanel graphPanel) {
        this.controller = controller;
        this.graphPanel = graphPanel;
        setLayout(new GridLayout(22, 1));
        runButton.addActionListener(this);
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
