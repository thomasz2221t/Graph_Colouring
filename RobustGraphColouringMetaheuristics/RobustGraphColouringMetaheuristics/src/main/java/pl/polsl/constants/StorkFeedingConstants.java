package pl.polsl.constants;

public final class StorkFeedingConstants {

    private StorkFeedingConstants() {}

    public static final int NUMBER_OF_AGENTS = 3;
    public static final long STORK_FEEDING_MAX_ITERATIONS = 10000;
    public static final int MAXIMAL_ROBUST_COLOUR_NUMBER = 6;
    public static final double PERFECT_COLOURING_FITNESS = 1.0;
    public static final double GOOD_COLOURING_FITNESS = 0.8;
    public  static final double MODERATE_COLOURING_FITNESS = 0.3;
    public static final double LOW_COLOURING_FITNESS = 0.0;
    public static final int SIGHT_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR = 2;
    public static final double HEURISTIC_INFO_ROBUSTNESS_FACTOR = 0.2;
    public static final double HEURISTIC_INFO_VERTEX_VISITED_FACTOR = 0.2;
    public static final double HEURISTIC_INFO_VERTEX_FITNESS_FACTOR = 0.5;
    public static final double ROBUSTNESS_UPDATE_INTERVAL = 1000;
}
