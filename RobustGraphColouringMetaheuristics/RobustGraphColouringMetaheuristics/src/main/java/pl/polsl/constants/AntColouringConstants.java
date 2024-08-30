package pl.polsl.constants;

public final class AntColouringConstants {

    private AntColouringConstants() {}
    public static final int NUMBER_OF_AGENTS = 3;
    public static final long ANT_COLOURING_MAX_ITERATIONS = 10000;
    public static final double ANT_COLOURING_MINIMAL_ROBUSTNESS = 0.9;
    public static final int MINIMAL_ROBUST_COLOUR_NUMBER = 11;
    public static final int MAXIMAL_ROBUST_COLOUR_NUMBER = 12;
    public static final double PASSING_PROBABILITY_HEURISTIC_WEIGHT = 0.35;
    public static final double PASSING_PROBABILITY_PHEROMONE_WEIGHT = 1.0;
    public static final double PHEROMONE_EVAPORATION_WEIGHT = 0.7;
    public static final double ROBUSTNESS_PENALTY_COLOURING_INVALID_ACCEPTABLE = 1.0;
    public static final double ROBUSTNESS_UPDATE_INTERVAL = 1000;
}
