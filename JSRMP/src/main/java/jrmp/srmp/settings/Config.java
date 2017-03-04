package jrmp.srmp.settings;



/**
 * 
 */

/**
 * @author micro
 *
 */
public class Config {
	
	/**
	 * General Settings
	 */
	
	public static String DEFAULT_EXP_ID = "default";
	
	public static String DEFAULT_EXP_NAME = "default";
	
	@Deprecated
	public static int CPLEX_MIP_EMPHASIS_LEVEL = 2;
	
	/**
	 * Aggregation/Desaggregation Settings
	 */
	
	public static boolean CHECK_ALL_LEXICOS = false;
	
	public static boolean AGGREGATION_MODE_ON = false;
	
	public static boolean RESULTS_CHECKING_ON = false;
	
	
	/**
	 * Generation Settings
	 */
	public static boolean OVERWRITE_INTERACT_OPTION = true;
	
	public static boolean ALWAYS_MAXIMIZE = false;
	
	public static boolean CONTINUOUS_SCALE_MODE_ON = false;
	
	public static boolean INCLUDE_INDIFFERENCE = false;
	
	
	public static boolean NUM_REF_PTS_DETERMINED = true;
	
	
	public static boolean NUM_PAIR_COMPS_DETERMINED = true;
	
	public static boolean RATIO_PAIR_COMPS_DETERMINED = false;
	
	
	public static boolean NUM_INCONS_DETERMINED = true;
	
	public static boolean RATIO_INCONS_DETERMINED = false;
	
	/**
	 * System Settings
	 */
	public static String RESOURCES_FOLDER = "src/main/resources/";
	
	public static String INPUT_FOLDER = "/" + DEFAULT_EXP_NAME + "/inputs";
	
	public static String OUTPUT_FOLDER = "/" + DEFAULT_EXP_NAME + "/outputs";
	
	public static String MODEL_FOLDER = OUTPUT_FOLDER + "/models";
	
	public static String SOL_FOLDER = OUTPUT_FOLDER + "/sols";
	
	public static String XML_FOLDER = OUTPUT_FOLDER + ""; //Just put all the output files in the output folder directly
	
	public static String GENERATION_FOLDER = INPUT_FOLDER;
	
}
