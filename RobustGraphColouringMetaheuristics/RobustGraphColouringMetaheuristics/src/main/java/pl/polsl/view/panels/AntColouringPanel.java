package pl.polsl.view.panels;

import pl.polsl.constants.AntColouringConstants;
import pl.polsl.controller.GraphColouringController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Map;

public class AntColouringPanel extends JPanel implements ActionListener {

    private final GraphColouringController controller;
    private final GraphPanel graphPanel;
    private final JLabel agentsNumberLabel = new JLabel("Number of artificial ants:");
    private final JFormattedTextField agentsNumberText = new JFormattedTextField();
    private final JLabel iterationsLabel = new JLabel("Number of iterations to execute:");
    private final JFormattedTextField iterationsText = new JFormattedTextField();
    private final JLabel coloursNumLabel = new JLabel("Number of colours:");
    private final JFormattedTextField coloursNumText = new JFormattedTextField();
    private final JLabel pheromoneEvaporationLabel = new JLabel("Pheromone evaporation factor:");
    private final JFormattedTextField pheromoneEvaporationText = new JFormattedTextField();
    private final JLabel resultsLabel = new JLabel("Results:");
    private final JLabel timeLabel = new JLabel("Execution time:");
    private final JLabel cpuTimeLabel = new JLabel("CPU execution time:");
    private final JLabel robustnessLabel = new JLabel("Robustness:");
    private final JLabel validLabel = new JLabel("Colouring is valid:");
    private final JButton runButton = new JButton("Run algorithm");

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Przycisk mrówki");
        Map<String, Integer> colouring = this.controller.runAntColouring((Integer) this.agentsNumberText.getValue(), (Long) this.iterationsText.getValue(), (Integer) this.coloursNumText.getValue(), (Double) this.pheromoneEvaporationText.getValue());
        DecimalFormat df = new DecimalFormat("#.####");
        this.timeLabel.setText("Execution time: " + df.format(this.controller.getAntColouringHeuristic().systemTime / Math.pow(10,9)) + "[s]");
        this.cpuTimeLabel.setText("CPU execution time: " + df.format(this.controller.getAntColouringHeuristic().cpuTime / Math.pow(10,9)) + "[s]");
        this.robustnessLabel.setText("Robustness: " + df.format(this.controller.getAntColouringHeuristic().robustness));
        this.validLabel.setText(this.controller.getAntColouringHeuristic().colouringValid ? "Colouring is valid" : "Colouring is invalid");
        this.graphPanel.showColouredGraph(this.controller.graph, colouring);
        this.graphPanel.requestFocus();
    }

    public AntColouringPanel(GraphColouringController controller, GraphPanel graphPanel) {
        this.controller = controller;
        this.graphPanel = graphPanel;
        setLayout(new GridLayout(22,1));
        runButton.addActionListener(this);
//        this.runButton.setPreferredSize(new Dimension(100,50));
        this.agentsNumberText.setValue(AntColouringConstants.NUMBER_OF_AGENTS);
        this.iterationsText.setValue(AntColouringConstants.ANT_COLOURING_MAX_ITERATIONS);
        this.coloursNumText.setValue(AntColouringConstants.MINIMAL_ROBUST_COLOUR_NUMBER);
        this.pheromoneEvaporationText.setValue(AntColouringConstants.PHEROMONE_EVAPORATION_WEIGHT);
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
