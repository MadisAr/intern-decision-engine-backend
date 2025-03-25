package ee.taltech.inbankbackend.config;

import java.util.Map;

/**
 * Holds all necessary constants for the decision engine.
 */
public class DecisionEngineConstants {
    public static final Integer MINIMUM_LOAN_AMOUNT = 2000;
    public static final Integer MAXIMUM_LOAN_AMOUNT = 10000;
    public static final Integer MAXIMUM_LOAN_PERIOD = 48;
    public static final Integer MINIMUM_LOAN_PERIOD = 12;
    public static final double MINIMUM_CREDIT_SCORE = 0.1;
    public static final Integer MINIMUM_AGE = 18;
    public static final Integer EXPECTED_LIFESPAN = 78;
    public static final Map<Integer, Integer> CREDIT_MODIFIERS = Map.of(
            965, 0,
            976, 100,
            987, 300,
            998, 1000,
            115, 100
    );

}
