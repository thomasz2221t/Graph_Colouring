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

    private GraphColouringController controller;
    private GraphPanel graphPanel;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Przycisk kukułka");
        Map<String, Integer> colouring = this.controller.runCuckooSearch((Integer) this.agentsNumberText.getValue(),
                (Long) this.iterationsText.getValue(), (Integer) this.coloursNumText.getValue(), (Double) this.randomWalkAlfaText.getValue(),
                (Double) this.randomWalkBetaText.getValue(), (Double) this.parasitismProbabilityText.getValue(),
                (Integer) this.gaussianStandardDeviationText.getValue(), this.validColouringsBox.isSelected());
        DecimalFormat df = new DecimalFormat("#.####");
        this.timeLabel.setText("Execution time: " + df.format(this.controller.getCuckooSearchHeuristic().systemTime / Math.pow(10,9)) + "[s]");
        this.cpuTimeLabel.setText("CPU execution time: " + df.format(this.controller.getCuckooSearchHeuristic().cpuTime / Math.pow(10,9)) + "[s]");
        this.robustnessLabel.setText("Robustness: " + df.format(this.controller.getCuckooSearchHeuristic().robustness));
        this.validLabel.setText(this.controller.getCuckooSearchHeuristic().colouringValid ? "Colouring is valid" : "Colouring is invalid");
        this.graphPanel.showColouredGraph(this.controller.graph, colouring);
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
