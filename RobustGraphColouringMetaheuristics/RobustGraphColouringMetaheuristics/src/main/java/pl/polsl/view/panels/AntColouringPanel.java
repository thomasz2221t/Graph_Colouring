package pl.polsl.view.panels;

import pl.polsl.constants.AntColouringConstants;
import pl.polsl.controller.GraphColouringController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class AntColouringPanel extends JPanel implements ActionListener {

    private GraphColouringController controller;
    private GraphPanel graphPanel;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Przycisk mrówki");
        Map<String, Integer> colouring = this.controller.runAntColouring();
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
