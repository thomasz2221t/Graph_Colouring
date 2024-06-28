package pl.polsl.metaheuristics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.util.MathUtil;
import pl.polsl.agents.CuckooAgent;
import pl.polsl.constants.CuckooSearchConstants;
import pl.polsl.constants.GraphConstants;
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
    private int maxNumberOfColours = CuckooSearchConstants.MAXIMAL_ROBUST_COLOUR_NUMBER;
    private List<CuckooAgent> cuckoos = new ArrayList<>();
    private Double robustness = 100.0;
    private int numberOfCorrectSolutions = 0;
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph() {

//        this.graph = customWeightedGraphHelper.imposeUncertaintyToGraph(this.graph,
//                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
//                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);
//        //only for testing
//        customWeightedGraphHelper.savingGraphVisualizationToFile(this.graph,
//                GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY+"cuckoo.png");

        this.init();

        long i = 0;

        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        long startTime = System.nanoTime();
        long cpuStartTime = threadMxBean.getCurrentThreadCpuTime();

        while(i < CuckooSearchConstants.CUCKOO_SEARCH_MAX_ITERATIONS) {
            //Calculating solution using Lévy flight operator
            for(int k=0; k < CuckooSearchConstants.NUMBER_OF_AGENTS; k++) {
                //wybranie M używając rozkładu Discrete Levi
                int M = this.choosingVerticesToModifyUsingDiscreteLevyDist(CuckooSearchConstants.ALFA_PROBLEM_SCALE_FACTOR, CuckooSearchConstants.BETA_DISTRIBUTION_INDEX_FACTOR);
                //modyfikacja M wierzchołków
                constructingBestLocalSolution(k, M);
            }

            //Parasitism solution calculation
            for(int k=0; k < CuckooSearchConstants.NUMBER_OF_AGENTS; k++) {
                //picking number from [0.0, 1.0)
                double randomNumber = Math.random();
                if(randomNumber <= CuckooSearchConstants.PARASITISM_OCCURRENCE_PROBABILITY) {
                    int M = this.choosingVerticesToModifyUsingNormalDistribution(this.graph.vertexSet().size(), CuckooSearchConstants.PARASITISM_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR);
                    constructingBestLocalSolution(k, M);
                }
            }

            //Opracowanie wyników każdej iteracji
            Map<String, Integer> bestColouring = this.evaluateBestCuckooSolution(this.cuckoos);
            if(CuckooSearchConstants.FORCE_HAVING_VALID_COLOURING) {
                if(this.checkGraphValidityAmongSolidEdges(this.graph, bestColouring))
                    this.verticesColourMap.putAll(bestColouring);
            } else {
                this.verticesColourMap.putAll(bestColouring);
            }

            //policzenie wartości robustness
            this.robustness = this.calculateRobustness(this.graph, this.verticesColourMap);
            if(this.checkGraphValidityAmongSolidEdges(this.graph, bestColouring)) {
                this.numberOfCorrectSolutions++;
            }
            i++;
        }

        long cpuEndTime = threadMxBean.getCurrentThreadCpuTime();
        long endTime = System.nanoTime();

        System.out.println("Good colourings: " + numberOfCorrectSolutions);
        getMetaheuristicsStatistics(this.graph, this.verticesColourMap, robustness, startTime, cpuStartTime, cpuEndTime, endTime);

        return this.verticesColourMap;
    }

    private void constructingBestLocalSolution(int k, int M) {
        //modyfikacja M wierzchołków
        Map<String, Integer> cuckooSolution = cuckoos.get(k).getCuckooGeneticSolution();
        Map<String, Integer> mutatedSolution = this.vertexGeneticMutation(this.graph, cuckooSolution, M);
        //porównanie rozwiązań, obliczenie funkcji fitnessu obu rozwiązań
        cuckoos.get(k).setCuckooGeneticSolution(this.compareOriginalAndMutatedSolution(cuckooSolution, mutatedSolution, this.graph));
    }

    private void init() {
        //Przygotowanie mapy koloru
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        //Przygotowanie agentow
        this.initCuckoos(this.graph, this.cuckoos, CuckooSearchConstants.NUMBER_OF_AGENTS, maxNumberOfColours);
    }

    private Map<String, Integer> generateRandomSolution(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, int maxNumberOfColours) {
        //określenie minimalnej lb kolorow (okreslenie wierzcholka posiadajacego maksymlana ilosc krawedzi a jak nie uda sie to
        //wybor losowego wierzcholka
        List<String> verticesList = graph.vertexSet().stream().toList();
        String maxNumberOfEdgesVertex = verticesList.stream().max(
               (String v1, String v2) -> Integer.compare(
                       customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, v1).size(),
                       customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, v2).size())
               ).orElse(customWeightedGraphHelper.getRandomVertexFromGraph(graph));
        //generacja losowych wartości kolorów dla każdego wierzchołka
        List<Integer> randomColourValues = new Random()
                .ints(1, maxNumberOfColours)
                .limit(verticesList.size())
                .boxed()
                .toList();
        //łaczenie listy wierzcholkow grafu oraz listy wylosowanych wartosci w mape
        Map<String, Integer> randomSolution = IntStream
                .range(0, verticesList.size())
                .boxed()
                .collect(Collectors.toMap(verticesList::get, randomColourValues::get));
        return randomSolution;
    }

    private List<CuckooAgent> initCuckoos(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<CuckooAgent> cuckoos, int numberOfAgents, int maxNumberOfColours) {
        for(int i = 0; i < numberOfAgents; i++) {
            CuckooAgent cuckoo = new CuckooAgent();
            //wygenerowanie randomowego rozwiązania
            cuckoo.setCuckooGeneticSolution(this.generateRandomSolution(graph, maxNumberOfColours));
            //przydzielenie mrówce
            cuckoos.add(cuckoo);
        }
        return cuckoos;
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
        //Protecting from overflowing the indexes
        if(mutatingVerticesNumber > graph.vertexSet().size())
            mutatingVerticesNumber = graph.vertexSet().size();
        if(mutatingVerticesNumber > 0) {
            //generacja losowych indeksów wierzchołków o liczbie M
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

    //basically counting robustness for all edges
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
        //obliczenie wartości funkcji fintessu dla oryginalnego kolorowania
        double originalSolutionFitness = this.calculateSolutionFitnessFunction(graph, originalSolution);
        //obliczenie wartości funkcji fitnessu dla mutowanego kolorowania
        double mutatedSolutionFitness = this.calculateSolutionFitnessFunction(graph, mutatedSolution);
        if(originalSolutionFitness > mutatedSolutionFitness) {
            return mutatedSolution;//przepisanie do zmiennej klasowej, musi zostać
        }
        return originalSolution;
    }

    private int choosingVerticesToModifyUsingNormalDistribution(int numberOfVerticesInGraph, int standardDeviationDivisionFactor) {
        double arithmeticalMean = (double) numberOfVerticesInGraph / 2;
        double standardDeviation = (double) numberOfVerticesInGraph / standardDeviationDivisionFactor;
        NormalDistribution vertexNumberNormalDistribution = new NormalDistribution(arithmeticalMean, standardDeviation);
        double M = vertexNumberNormalDistribution.sample();
        return (int) Math.floor(M);
    }

    private Map<String, Integer> evaluateBestCuckooSolution(List<CuckooAgent> cuckoos) {
        double bestFitnessValue = 0;//cuckoos.get(0).getSolutionFitting();
        int bestSolutionIndex = 0;
        for(int k=1; k < cuckoos.size(); k++) {
            //if(cuckoos.get(k).getSolutionFitting() < bestFitnessValue) {
                bestSolutionIndex = k;
            //}
        }
        return cuckoos.get(bestSolutionIndex).getCuckooGeneticSolution();
    }

    public CuckooSearchHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
