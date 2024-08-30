package pl.polsl.controller;

import lombok.Getter;
import lombok.Setter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import pl.polsl.constants.AntColouringConstants;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.metaheuristics.AntColouringHeuristic;
import pl.polsl.metaheuristics.BeeColouringHeuristic;
import pl.polsl.metaheuristics.CuckooSearchHeuristic;
import pl.polsl.metaheuristics.StorkFeedingHeuristic;

import java.util.Map;

@Getter
public class GraphColouringController {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph;
    private AntColouringHeuristic antColouringHeuristic;
    private CuckooSearchHeuristic cuckooSearchHeuristic;
    private BeeColouringHeuristic beeColouringHeuristic;
    private StorkFeedingHeuristic storkFeedingHeuristic;
    private CustomWeightedGraphHelper customWeightedGraphHelper;

    public Map<String, Integer> runAntColouring(final int numberOfAgents, final long antColouringMaxIterations,
                                                final int maximalRobustColourNumber, double pheromoneEvaporationWeight) {
        this.antColouringHeuristic.graph = this.graph;
        return this.antColouringHeuristic.colourTheGraph(numberOfAgents, antColouringMaxIterations, maximalRobustColourNumber, maximalRobustColourNumber, pheromoneEvaporationWeight);
    }

    public Map<String, Integer> runCuckooSearch(final int numberOfAgents, final long cuckooSearchMaxIterations,
                                                final int maximalRobustColourNumber, final double alfaProblemScaleFactor,
                                                final double betaDistributionIndexFactor, final double parasitismOccurrenceProbability,
                                                final int parasitismNormalDistributionDeviationFactor, final boolean forceHavingValidColouring) {
        this.cuckooSearchHeuristic.graph = this.graph;
        return  this.cuckooSearchHeuristic.colourTheGraph(numberOfAgents, cuckooSearchMaxIterations, maximalRobustColourNumber,
                alfaProblemScaleFactor, betaDistributionIndexFactor, parasitismOccurrenceProbability,
                parasitismNormalDistributionDeviationFactor, forceHavingValidColouring);
    }

    public Map<String, Integer> runBeeColouring(final int numberOfBeeWorkers, final int numberOfBeeScouts,
                                                final long beeColouringMaxIterations, final int maximalRobustColourNumber,
                                                final int feedingRegionDepth, final int workerOperationalIterationNumber,
                                                final int scoutOperationalIterationNumber, final int hivesShuffleIterationPeriod) {
        this.beeColouringHeuristic.graph = this.graph;
        return this.beeColouringHeuristic.colourTheGraph(numberOfBeeWorkers, numberOfBeeScouts, beeColouringMaxIterations,
                maximalRobustColourNumber, feedingRegionDepth, workerOperationalIterationNumber,
                scoutOperationalIterationNumber, hivesShuffleIterationPeriod);
    }

    public Map<String, Integer> runStorkFeedingColouring(final int numberOfAgents, final long storkFeedingMaxIterations,
                                                         final int maximalRobustColourNumber, final double goodColouringFitness,
                                                         final double moderateColouringFitness, final int sightNormalDistributionDeviationFactor) {
        this.storkFeedingHeuristic.graph = this.graph;
        return this.storkFeedingHeuristic.colourTheGraph(numberOfAgents, storkFeedingMaxIterations, maximalRobustColourNumber,
                goodColouringFitness, moderateColouringFitness, sightNormalDistributionDeviationFactor);
    }

    public void importGraphFromPath(String path) {
        System.out.println(path);
        DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph = customWeightedGraphHelper.importDIMACSUnweightedGraphAsWeighted(path);
        this.graph = customWeightedGraphHelper.imposeUncertaintyToGraph(graph,
                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);
    }

    public GraphColouringController(DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph) {
        this.graph = graph;
        this.antColouringHeuristic = new AntColouringHeuristic(graph);
        this.cuckooSearchHeuristic = new CuckooSearchHeuristic(graph);
        this.beeColouringHeuristic = new BeeColouringHeuristic(graph);
        this.storkFeedingHeuristic = new StorkFeedingHeuristic(graph);
        this.customWeightedGraphHelper = new CustomWeightedGraphHelper();
    }
}
