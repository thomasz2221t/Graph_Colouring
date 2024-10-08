package pl.polsl.metaheuristics;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.util.MathUtil;
import pl.polsl.agents.CuckooAgent;
import pl.polsl.constants.CuckooSearchConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CuckooSearchHeuristic extends AbstractColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    public Double robustness = 100.0;
    public long systemTime;
    public long cpuTime;
    public boolean colouringValid;
    private int maxNumberOfColours;
    private List<CuckooAgent> cuckoos = new ArrayList<>();
    private int numberOfCorrectSolutions = 0;
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph(final int numberOfAgents, final long cuckooSearchMaxIterations, 
                                               final int maximalRobustColourNumber, final double alfaProblemScaleFactor, 
                                               final double betaDistributionIndexFactor, final double parasitismOccurrenceProbability,
                                               final int parasitismNormalDistributionDeviationFactor, final boolean forceHavingValidColouring) {
        this.resetVariables();
        this.maxNumberOfColours = maximalRobustColourNumber;
        this.init(numberOfAgents);

        long i = 0;

        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        long startTime = System.nanoTime();
        long cpuStartTime = threadMxBean.getCurrentThreadCpuTime();

        while(i < cuckooSearchMaxIterations) {
            for(int k=0; k < numberOfAgents; k++) {
                int M = this.choosingVerticesToModifyUsingDiscreteLevyDist(alfaProblemScaleFactor, betaDistributionIndexFactor);
                constructingBestLocalSolution(k, M);
            }

            for(int k=0; k < numberOfAgents; k++) {
                double randomNumber = Math.random();
                if(randomNumber <= parasitismOccurrenceProbability) {
                    int M = this.choosingVerticesToModifyUsingNormalDistribution(this.graph.vertexSet().size(), parasitismNormalDistributionDeviationFactor);
                    constructingBestLocalSolution(k, M);
                }
            }

            Map<String, Integer> bestColouring = this.evaluateBestCuckooSolution(this.cuckoos);
            if(forceHavingValidColouring) {
                if(this.checkGraphValidityAmongSolidEdges(this.graph, bestColouring))
                    this.verticesColourMap.putAll(bestColouring);
            } else {
                this.verticesColourMap.putAll(bestColouring);
            }

            this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
            if(this.checkGraphValidityAmongSolidEdges(this.graph, bestColouring)) {
                this.numberOfCorrectSolutions++;
            }
            i++;
        }

        long cpuEndTime = threadMxBean.getCurrentThreadCpuTime();
        long endTime = System.nanoTime();

        this.getMetaheuristicsStatistics(this.graph, this.verticesColourMap, robustness, startTime, cpuStartTime, cpuEndTime, endTime);
        Triple<Long, Long, Boolean> statistics = this.estimateMetaheuristicsStatistics(this.graph, this.verticesColourMap, startTime, cpuStartTime, cpuEndTime, endTime);
        this.systemTime = statistics.getLeft();
        this.cpuTime = statistics.getMiddle();
        this.colouringValid = statistics.getRight();
        return this.verticesColourMap;
    }

    private void constructingBestLocalSolution(int k, int M) {
        Map<String, Integer> cuckooSolution = cuckoos.get(k).getCuckooGeneticSolution();
        Map<String, Integer> mutatedSolution = this.vertexGeneticMutation(this.graph, cuckooSolution, M);
        cuckoos.get(k).setCuckooGeneticSolution(this.compareOriginalAndMutatedSolution(cuckooSolution, mutatedSolution, this.graph));
    }

    private void init(int numberOfAgents) {
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        this.initCuckoos(this.graph, this.cuckoos, numberOfAgents, maxNumberOfColours);
    }

    private Map<String, Integer> generateRandomSolution(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, int maxNumberOfColours) {
        List<String> verticesList = graph.vertexSet().stream().toList();
        String maxNumberOfEdgesVertex = verticesList.stream().max(
               (String v1, String v2) -> Integer.compare(
                       customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, v1).size(),
                       customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, v2).size())
               ).orElse(customWeightedGraphHelper.getRandomVertexFromGraph(graph));
        List<Integer> randomColourValues = new Random()
                .ints(1, maxNumberOfColours)
                .limit(verticesList.size())
                .boxed()
                .toList();
        Map<String, Integer> randomSolution = IntStream
                .range(0, verticesList.size())
                .boxed()
                .collect(Collectors.toMap(verticesList::get, randomColourValues::get));
        return randomSolution;
    }

    private void initCuckoos(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<CuckooAgent> cuckoos, int numberOfAgents, int maxNumberOfColours) {
        for(int i = 0; i < numberOfAgents; i++) {
            CuckooAgent cuckoo = new CuckooAgent();
            cuckoo.setCuckooGeneticSolution(this.generateRandomSolution(graph, maxNumberOfColours));
            cuckoos.add(cuckoo);
        }
    }

    private int choosingVerticesToModifyUsingDiscreteLevyDist(double alfa_ScaleFactor, double beta_IndexFactor) {
        double standardDeviationP = MathUtil.factorial((int) beta_IndexFactor) * Math.sin((Math.PI * beta_IndexFactor) / 2)
                / (MathUtil.factorial((int) (beta_IndexFactor / 2)) * beta_IndexFactor * Math.pow(2, (beta_IndexFactor - 1) / 2));
        NormalDistribution normalDistributionP = new NormalDistribution(0, standardDeviationP);
        NormalDistribution normalDistributionQ = new NormalDistribution(0, 1);
        double randomNormalVariableP = normalDistributionP.sample();
        double randomNormalVariableQ = normalDistributionQ.sample();
        double levyRandomFlight_by_beta = randomNormalVariableP / Math.pow(Math.abs(randomNormalVariableQ), 1 / beta_IndexFactor);
        double M = (alfa_ScaleFactor * levyRandomFlight_by_beta) + 1;
        if(M < 0) {
            M = Math.abs(M);
        }
        return (int) Math.floor(M);
    }

    private Map<String, Integer> vertexGeneticMutation(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> mapVerticesColours, int mutatingVerticesNumber) {
        List<String> vertexList = graph.vertexSet().stream().toList();
        if(mutatingVerticesNumber > graph.vertexSet().size())
            mutatingVerticesNumber = graph.vertexSet().size();
        if(mutatingVerticesNumber > 0) {
            List<Integer> randomVerticesIndexes = new Random()
                    .ints(0, mutatingVerticesNumber)
                    .limit(mutatingVerticesNumber)
                    .boxed()
                    .toList();
            Random random = new Random();
            for (Integer vertexIndex : randomVerticesIndexes) {
                String vertex = vertexList.get(vertexIndex);
                mapVerticesColours.put(vertex, random.nextInt(this.maxNumberOfColours) + 1);
            }
        }
        return mapVerticesColours;
    }

    private double countNumberOfErrorsInColouring(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> mapVerticesColours) {
        double graphPenaltiesSum = 0.0;
        for(CustomWeightedGraphHelper.CustomWeightedEdge edge : graph.edgeSet()) {
            Integer sourceVertexColour = mapVerticesColours.get(graph.getEdgeSource(edge));
            Integer targetVertexColour = mapVerticesColours.get(graph.getEdgeTarget(edge));
            if(Objects.equals(sourceVertexColour, targetVertexColour))
                graphPenaltiesSum += graph.getEdgeWeight(edge);
        }
        return graphPenaltiesSum;
    }

    private double calculateSolutionFitnessFunction(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> solution) {
        boolean isColouringValid = this.checkGraphValidityAmongSolidEdges(graph, solution);
        double colouringValidityFactor = isColouringValid
                ? 0.0
                : CuckooSearchConstants.FITNESS_FUNCTION_COLOURING_VALIDITY_FACTOR * countNumberOfErrorsInColouring(graph, solution);
        double robustnessFactor = CuckooSearchConstants.FITNESS_FUNCTION_ROBUSTNESS_FACTOR * calculateRobustness(graph, solution);
        return colouringValidityFactor + robustnessFactor;
    }

    private Map<String, Integer> compareOriginalAndMutatedSolution(Map<String, Integer> originalSolution, Map<String, Integer> mutatedSolution,
                                                                   DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        double originalSolutionFitness = this.calculateSolutionFitnessFunction(graph, originalSolution);
        double mutatedSolutionFitness = this.calculateSolutionFitnessFunction(graph, mutatedSolution);
        if(originalSolutionFitness > mutatedSolutionFitness) {
            return mutatedSolution;
        }
        return originalSolution;
    }

    private Map<String, Integer> evaluateBestCuckooSolution(List<CuckooAgent> cuckoos) {
        int bestSolutionIndex = 0;
        for(int k=1; k < cuckoos.size(); k++) {
                bestSolutionIndex = k;
        }
        return cuckoos.get(bestSolutionIndex).getCuckooGeneticSolution();
    }

    private void resetVariables() {
        this.verticesColourMap = new HashMap<>();
        this.robustness = 100.0;
        this.systemTime = 0;
        this.cpuTime = 0;
        this.colouringValid = false;
        this.maxNumberOfColours = 0;
        this.cuckoos = new ArrayList<>();
        this.numberOfCorrectSolutions = 0;
    }

    public CuckooSearchHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
