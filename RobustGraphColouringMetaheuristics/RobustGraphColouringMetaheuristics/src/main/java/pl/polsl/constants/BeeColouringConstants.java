package pl.polsl.constants;

public final class BeeColouringConstants {

    private BeeColouringConstants() {}

    public static final int NUMBER_OF_BEE_WORKERS = 2;
    public static final int NUMBER_OF_BEE_SCOUTS = 1;
    public static final int NUMBER_OF_AGENTS = NUMBER_OF_BEE_WORKERS + NUMBER_OF_BEE_SCOUTS;
    public static final long BEE_COLOURING_MAX_ITERATIONS = 30;//10000;

    //public static final int MINIMAL_ROBUST_COLOUR_NUMBER = 3;
    public static final int MAXIMAL_ROBUST_COLOUR_NUMBER = 5;
    public static final int FEEDING_REGION_DEPTH = 3;
    public static final int WORKER_OPERATIONAL_ITERATION_NUMBER = 2;//3;
    public static final int SCOUT_OPERATIONAL_ITERATION_NUMBER = 5;//10;
    public static final double HEURISTIC_INFO_COLOURING_VALIDITY_FACTOR = 0.5;
    public static final double HEURISTIC_INFO_ROBUSTNESS_FACTOR = 0.2;
    public static final double HEURISTIC_INFO_VERTEX_VISITED_FACTOR = 0.2;
    public static final int HIVES_SHUFFLE_ITERATION_PERIOD = 1000;
    public static final double ROBUSTNESS_UPDATE_INTERVAL = 1000;
}

