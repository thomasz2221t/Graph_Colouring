package pl.polsl.agents;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CuckooAgent {
    private Map<String, Integer> cuckooGeneticSolution;
    private double solutionFitting = 0.0;
}
