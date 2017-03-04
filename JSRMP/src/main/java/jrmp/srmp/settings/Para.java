package jrmp.srmp.settings;

/**
 * 
 */

/**
 * @author micro
 *
 */
public class Para {

	/**
	 * General parameters
	 */
	
	public static int MAX_NUM_REF_PTS = 3;
	
	@Deprecated
	public static double WEIGHT_UPPER_BOUND = 0.99;
	
	@Deprecated
	public static double WEIGHT_LOWER_BOUND = 0.01; 
	
	/**
	 * Disaggregation process parameters
	 */
	public static double ESTIMATED_INCONS_LEVEL = 0.10;
	
	public static double AMPLIFI_SLACK = 1;
	
	public static double L = 1e5;
	
	public static double EPSILON = 1e-3;
	
	public static double ZERO = 1e-15;
	
	/**
	 * Generation process parameters
	 */
	
	public static int RATING_SCALE_MAX = 100;
	
	public static int RATING_SCALE_MIN = 0;
	
	public static int RATING_SCALE_INCREMENT = 1;
	
	
	public static int GENE_NUM_ALTERNATIVES = 100;
	
	public static int GENE_NUM_CRITERIA = 4;
	
	public static int GENE_NUM_REF_PTS = 3; //Should be less than MAX_NUM_PROFILES !!!
	
	
	public static double MAX_RATIO_PAIR_COMPS = 0.20;
	
	public static double MIN_RATIO_PAIR_COMPS = 0.10;

	
	public static double MAX_RATIO_INCONS = 0.10;
	
	public static double MIN_RATIO_INCONS = 0.05;
	
	
	public static int GENE_NUM_PAIR_COMPS = 20;
	
	public static int GENE_NUM_INDIFFS = 0;
	
	public static int GENE_NUM_PERTURBAS = 0;
	
	
	/**
	 * JSRMPII Project
	 * 
	 * @author micro
	 * @since 2013-12-3
	 * 
	 */
	
	public static double ADVANCED_THRESHOLD_EVALUATION_INDIFFERENCE = Para.EPSILON;
	
	public static double ADVANCED_THRESHOLD_SLACK_INDIFFERENCE = Para.EPSILON; // Never set to zero!!
	
	public static Integer ADVANCED_SIZE_POPULATION = 10;
	
	public static Integer ADVANCED_TARGET_NUM_REF_PTS = 3;
	
	public static Integer ADVANCED_ITERATION_NUMBER = 100;
	
}
