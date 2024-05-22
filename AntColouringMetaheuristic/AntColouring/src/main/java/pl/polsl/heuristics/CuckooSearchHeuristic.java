package pl.polsl.heuristics;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.util.MathUtil;
import pl.polsl.agents.CuckooAgent;
import pl.polsl.constants.CuckooSearchConstants;
import pl.polsl.constants.GraphConstants;
import pl.polsl.graphs.CustomWeightedGraphHelper;
import pl.polsl.graphs.CustomWeightedGraphHelper.CustomWeightedEdge;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CuckooSearchHeuristic extends ColouringHeuristic {
    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph;
    public Map<String, Integer> verticesColourMap = new HashMap<>();
    private List<CuckooAgent> cuckoos = new ArrayList<>();
    private Double robustness = Double.valueOf(100);
    private final CustomWeightedGraphHelper customWeightedGraphHelper = new CustomWeightedGraphHelper();

    public Map<String, Integer> colourTheGraph() {

        this.graph = customWeightedGraphHelper.imposeUncertaintyToGraph(this.graph,
                GraphConstants.PROPORTION_EDGES_TO_FUZZ,
                GraphConstants.LOWER_BOUNDARY_OF_UNCERTAINTY);
        //only for testing
        customWeightedGraphHelper.savingGraphVisualizationToFile(this.graph,
                GraphConstants.GRAPH_VISUALISATION_SAVING_DIRECTORY + "cuckoo.png");

        this.init();

        long i = 0;
        while(i < CuckooSearchConstants.CUCKOO_SEARCH_MAX_ITERATIONS) {
            for(int k=0; k < CuckooSearchConstants.NUMBER_OF_AGENTS; k++) {
                //wybranie M używając rozkładu Discrete Levi
                int M = choosingVerticesToModifyUsingDiscreteLevyDist(CuckooSearchConstants.ALFA_SCALE_FACTOR, CuckooSearchConstants.BETA_INDEX_FACTOR);
                vertexGeneticMutation(this.verticesColourMap, M);
            }
            i++;
        }
        return this.verticesColourMap;
    }

    private void init() {
        //Przygotowanie mapy koloru
        this.initVerticesColourMap(this.graph, this.verticesColourMap);
        //Przygotowanie agentow
        this.initCuckoos(this.graph, this.cuckoos, CuckooSearchConstants.NUMBER_OF_AGENTS);
    }

    private Map<String, Integer> generateRandomSolution(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        //określenie minimalnej lb kolorow (okreslenie wierzcholka posiadajacego maksymlana ilosc krawedzi a jak nie uda sie to
        //wybor losowego wierzcholka
        List<String> verticesList = graph.vertexSet().stream().toList();
        String maxNumberOfEdgesVertex = verticesList.stream().max(
               (String v1, String v2) -> Integer.compare(
                       customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, v1).size(),
                       customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, v2).size())
               ).orElse(customWeightedGraphHelper.getRandomVertexFromGraph(graph));
        int maxNumberOfAttachedEdges = customWeightedGraphHelper.getNeighbourhoodListOfVertex(graph, maxNumberOfEdgesVertex).size();
        //generacja losowych wartości kolorów dla każdego wierzchołka
        List<Integer> randomColourValues = new Random()
                .ints(1, maxNumberOfAttachedEdges)
                .limit(verticesList.size())
                .boxed()
                .collect(Collectors.toList());
        //łaczenie listy wierzcholkow grafu oraz listy wylosowanych wartosci w mape
        Map<String, Integer> randomSolution = IntStream
                .range(0, verticesList.size())
                .boxed()
                .collect(Collectors.toMap(verticesList::get, randomColourValues::get));
        return randomSolution;
    }

    private List<CuckooAgent> initCuckoos(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, List<CuckooAgent> cuckoos, int numberOfAgents) {
        for(int i = 0; i < numberOfAgents; i++) {
            CuckooAgent cuckoo = new CuckooAgent();
            //wygenerowanie randomowego rozwiązania
            cuckoo.setCuckooGeneticSolution(this.generateRandomSolution(graph));
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
        System.out.println("Discrete Levy Flight calculated by hand: " + M);
        //using library
        Random random = new Random();
        LevyDistribution levyDistribution = new LevyDistribution(beta_IndexFactor, alfa_ScaleFactor); //mu - location parameter, c - scale parameter
        double MTest = levyDistribution.cumulativeProbability(random.nextInt(2)) + 1;
        System.out.println("Discrete Levy Flight calculated by library: " + MTest);
        return (int) Math.floor(M);
    }

    private void vertexGeneticMutation(Map<String, Integer> verticesColourMap, int mutatingVerticesNumber) {
        //generacja losowych indeksów wierzchołków o liczbie M
        List<Integer> randomVerticesIndexes = new Random()
                .ints(1, mutatingVerticesNumber)
                .limit(mutatingVerticesNumber)
                .boxed()
                .collect(Collectors.toList());
        Random random = new Random();
        for (vertex : randomVerticesIndexes) {

            verticesColourMap.put(vertex, )
        }
    }


    public CuckooSearchHeuristic(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        this.graph = graph;
    }
}
