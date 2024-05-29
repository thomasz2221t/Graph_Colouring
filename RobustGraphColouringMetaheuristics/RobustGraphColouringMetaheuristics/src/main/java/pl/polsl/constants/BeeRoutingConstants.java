package pl.polsl.constants;

public final class BeeRoutingConstants {

    private BeeRoutingConstants() {}

    public static final int NUMBER_OF_BEE_WORKERS = 2;
    public static final int NUMBER_OF_BEE_SCOUTS = 1;
    public static final int NUMBER_OF_AGENTS = NUMBER_OF_BEE_WORKERS + NUMBER_OF_BEE_SCOUTS;
    public static final long BEE_COLOURING_MAX_ITERATIONS = 10000;

    //public static final int MINIMAL_ROBUST_COLOUR_NUMBER = 3;
    public static final int MAXIMAL_ROBUST_COLOUR_NUMBER = 5;
    public static final int FEEDING_REGION_DEPTH = 3;
}

