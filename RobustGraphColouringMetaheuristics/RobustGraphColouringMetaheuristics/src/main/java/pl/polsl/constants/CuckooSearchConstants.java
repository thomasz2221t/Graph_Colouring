package pl.polsl.constants;

public final class CuckooSearchConstants {

    private CuckooSearchConstants() {}

    public static final int NUMBER_OF_AGENTS = 3;
    public static final long CUCKOO_SEARCH_MAX_ITERATIONS = 10000;
    public static final int MAXIMAL_ROBUST_COLOUR_NUMBER = 6;
    public static final double ALFA_PROBLEM_SCALE_FACTOR = 1.2;
    public static final double BETA_DISTRIBUTION_INDEX_FACTOR = 0.9;
    public static final double FITNESS_FUNCTION_COLOURING_VALIDITY_FACTOR = 3.5;
    public static final double FITNESS_FUNCTION_ROBUSTNESS_FACTOR = 0.2;
    public static final double PARASITISM_OCCURRENCE_PROBABILITY = 0.20;
    public static final int PARASITISM_NORMAL_DISTRIBUTION_STANDARD_DEVIATION_FACTOR = 4;
    public static final boolean FORCE_HAVING_VALID_COLOURING = false;
}
