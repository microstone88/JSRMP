/**
 * 
 */
package jrmp.srmp.base;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.interval.Intervals;
import org.decision_deck.jmcda.structure.interval.PreferenceDirection;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;

import com.google.common.collect.Table.Cell;

import jrmp.srmp.math.Factorial;
import jrmp.srmp.math.Permutations;
import jrmp.srmp.settings.Config;
import jrmp.srmp.settings.Para;
import jrmp.srmp.utils.OutputUtils;
import jrmp.srmp.utils.RankingUtils;

/**
 * @author micro
 *
 */
public class XSRMPgenerator extends XSRMPmodeler {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private Integer numOfPcomps = Para.GENE_NUM_PAIR_COMPS;
	
	private Integer numOfIndiff = Para.GENE_NUM_INDIFFS;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public XSRMPgenerator() {
		
		this.setStatus(XSRMPmodeler.Status.initialized);
		
	}
	
	///////////////////
	////  METHODS  ////
	///////////////////
	
	public Integer generate() {
		
		OutputUtils.lsln("\n[Generation Process]");

		int re = 0;
		re += this.prepareAlternatives(false);
		re += this.prepareCriteria(false);
		re += this.prepareEvaluations(false);
		re += this.prepareWeights(false);
		re += this.prepareRefPts(false);
		re += this.prepareRefComps(false);
		
		if (re == 9) {
			OutputUtils.lsln("[i] Generation successfully completed!");
		}else
			OutputUtils.lscln("[i] Something is missing.");
		
		this.setStatus(XSRMPmodeler.Status.generated);
		return re;
	}
	
	public Integer generate(boolean display) {
		
		OutputUtils.lscln("\n[Generation Process]");
		
		int re = 0;
		re += this.prepareAlternatives(display);
		re += this.prepareCriteria(display);
		re += this.prepareEvaluations(display);
		re += this.prepareWeights(display);
		re += this.prepareRefPts(display);
		re += this.prepareRefComps(display);
		
		if (re == 9) {
			OutputUtils.lscln("[i] Generation successfully completed!");
		}else{
			OutputUtils.lscln("[i] Something is missing.");
		}
		
		this.setStatus(XSRMPmodeler.Status.generated);
		return re;
	}
	
	
	public Integer generateWithoutAggregation(boolean display) {
		
		int re = 0;
		
		long start = System.currentTimeMillis();
		
		re += this.prepareAlternatives(display);
		re += this.prepareCriteria(display);
		re += this.prepareEvaluations(display);
		re += this.prepareWeights(display);
		re += this.prepareRefPts(display);
//		re += this.prepareRefComps(display);
		
		long end = System.currentTimeMillis();
		long duration = end - start;
		
		if (re == 6) {
			OutputUtils.lscln("[i] Generation without selecting pairwise comparisons completed!"
					+ " (Elapsed time: " + duration + " ms)");
		}else{
			OutputUtils.lscln("[i] Some data is missing.");
		}
		
		this.setStatus(XSRMPmodeler.Status.generated);
		return re;
	}
	
	private Integer prepareAlternatives(boolean display) {
		
		if (display) OutputUtils.lc("[i] Generating the alternatives (1/9)... ");
		else OutputUtils.log("[i] Generating the alternatives (1/9)... ");
		
		this.setAlteSet(this.constructAlteSet());
		this.setAlteNames(this.constructAlteNames());

		if (this.getAlteSet() != null) {
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			OutputUtils.lsln("[i] " + this.getNumOfAlternatives() + " alternatives have been generated.");
			if (display)
				this.displayAlternatives();
			else
				this.outputAlternatives();
			return 1;
		}else{
			OutputUtils.lcln("Error!");
			return 0;
		}
		
	}
	
	private Integer prepareCriteria(boolean display) {
		
		if (display) OutputUtils.lc("[i] Generating the criteria (2/9)... ");
		else OutputUtils.log("[i] Generating the criteria (2/9)... ");
		
		this.setCritSet(this.constructCritSet());
		this.setCritNames(this.constructCritNames());
		this.setCritScales(this.constructCritScales());

		if (this.getCritSet() != null) {
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			OutputUtils.lsln("[i] " + this.getNumOfCriteria() + " criteria have been generated.");
			if (display)
				this.displayCriteria();
			else
				this.outputCriteria();
			return 1;
		}else{
			OutputUtils.lcln("Error!");
			return 0;
		}
		
	}
	
	private Integer prepareEvaluations(boolean display) {
		
		if (display) OutputUtils.lc("[i] Generating the evaluations of alternatives (3/9)... ");
		else OutputUtils.log("[i] Generating the evaluations of alternatives (3/9)... ");
		
		this.setAlteValues(this.constructAlteValues());

		if (this.getAlteValues() != null) {
			
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			
			if (Config.CONTINUOUS_SCALE_MODE_ON) {
				OutputUtils.lsln("[i] The alternatives are evaluated on continuous scale " +
						"and the values are randomized between [" + Para.RATING_SCALE_MIN + ","
						+ Para.RATING_SCALE_MAX + "].");
			}else{
				OutputUtils.lsln("[i] The alternatives are evaluated on discrete scale " +
						"and the values are randomized between [" + Para.RATING_SCALE_MIN + ","
						+ Para.RATING_SCALE_MAX + "] and " +
						"the rating scale increment is " + Para.RATING_SCALE_INCREMENT + ".");
			}
			
			if (display)
				this.displayEvaluations();
			else
				this.outputEvaluations();
			return 1;
			
		}else{
			OutputUtils.lcln("Error!");
			return 0;
		}
		
	}
	
	private Integer prepareWeights(boolean display) {
		
		if (display) OutputUtils.lc("[i] Generating the weights of criteria (4/9)... ");
		else OutputUtils.log("[i] Generating the weights of criteria (4/9)... ");
		
		this.setWeights(this.constructCritWeights());

		if (this.getWeights() != null) {
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			OutputUtils.lsln("[i] The weights of criteria have been randomized between " +
					"[" + Para.WEIGHT_LOWER_BOUND + "," + Para.WEIGHT_UPPER_BOUND + "] " +
					"and normalized to 1.");
			if (display)
				this.displayWeights();
			else
				this.outputWeights();
			return 1;
		}else{
			OutputUtils.lcln("Error!");
			return 0;
		}
		
	}
	
	private Integer prepareRefPts(boolean display) {
		
		if (display) OutputUtils.lc("[i] Generating the reference points (5/9)... ");
		else OutputUtils.log("[i] Generating the reference points (5/9)... ");
		
		this.setRefPtsSet(this.constructRefPtsSet());
		this.setRefPtsValues(this.constructRefPtsValues());
		
		int re = 0;
		if (this.getRefPtsSet() != null) {
			
			re++;
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			
			if (Config.NUM_REF_PTS_DETERMINED) {
				OutputUtils.lsln("[i] The number of reference points is determined. " 
						+ Para.GENE_NUM_REF_PTS + " reference point(s) have/has been generated.");
			}else{
				OutputUtils.lsln("[i] The number of reference points is randomized between "
						+ "[1," + Para.MAX_NUM_REF_PTS + "]. " + this.getNumOfRefPts() 
						+ " reference point(s) have/has been generated.");
			}
			
			if (display)
				this.displayRefPts();
			else
				this.outputRefPts();
			
		}else{
			OutputUtils.lcln("Error!");
		}
		

		/////////////////////////////////////////////////////////////////////////////////
		
		if (display) OutputUtils.lc("[i] Generating the lexicographic order for reference points (6/9)... ");
		else OutputUtils.log("[i] Generating the lexicographic order for reference points (6/9)... ");
		
		this.setLexico(this.constructLexico());

		if (this.getLexico() != null) {
			re++;
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			OutputUtils.lsln("[i] The lexicographic order of reference points is randomly selected.");
			if (display)
				this.displayLexico();
			else
				this.outputLexico();
		}else{
			OutputUtils.lcln("Error!");
		}
		return re;
	}
	
	private Integer prepareRefComps(boolean display) {
		
		int re = 0;
		if (display) OutputUtils.lc("[i] Aggregating the complete ranking with the generated S-RMP model (7/9)... ");
		else OutputUtils.log("[i] Aggregating the complete ranking with the generated S-RMP model (7/9)... ");
		
		this.setAllComps(this.constructAllComps());
		
		if (this.getAllComps() != null) {
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			re++;
		}else{
			OutputUtils.lcln("Error!");
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////
		
		if (display) OutputUtils.lc("[i] Selecting the pairwise comparisons (8/9)... ");
		else OutputUtils.log("[i] Selecting the pairwise comparisons (8/9)... ");
		
		this.setRefComps(this.constructRefComps());
		
		if (this.getNumOfPairComps() > 0) {
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			re++;
			OutputUtils.lsln("[i] " + this.getNumOfPairComps()
					+ " pairs of alternatives have been randomly selected.");
		}else{
			OutputUtils.lcln("Error!");
		}
		
		/////////////////////////////////////////////////////////////////////////////////
		
		if (display) OutputUtils.lc("[i] Generating the pairwise comparisons (9/9)... ");
		else OutputUtils.log("[i] Generating the pairwise comparisons (9/9)... ");
		
		this.setAlteInPCs(this.constructAlteInPCsSet());
		
		if (this.getNumOfAltesInPCs() > 0) {
			if (display) OutputUtils.lcln("OK!");
			else OutputUtils.logln("OK!");
			re++;
			OutputUtils.lsln("[i] "	+ this.getNumOfAltesInPCs()
							+ " different alternatives are involved in the pairwise comparisons.");
		}else{
			OutputUtils.lcln("Error!");
		}
		
		if (display) {
			this.displayRefComps();
		}else
			this.outputRefComps();
		
		return re;
	
	}
	
	private Set<Criterion> constructCritSet() {
		
		Set<Criterion> re = new LinkedHashSet<Criterion>(Para.GENE_NUM_CRITERIA);
		for (int i = 0; i < Para.GENE_NUM_CRITERIA; i++) {
			re.add(new Criterion("c" + (i+1)));
		}
		return re;
		
	}
	
	private Map<Criterion, String> constructCritNames() {
		
		Map<Criterion, String> re = new LinkedHashMap<Criterion, String>(Para.GENE_NUM_CRITERIA);
		if (!this.getCritSet().isEmpty()) {
			for (Iterator<Criterion> it = this.getCritSet().iterator(); it.hasNext();) {
				Criterion crit = it.next();
				re.put(crit, "Unknown");
			}
		}
		return re;
		
	}
	
	private Map<Criterion, Interval> constructCritScales() {
		
		Random random = new Random(System.currentTimeMillis());
		
		Map<Criterion, Interval> re = new LinkedHashMap<Criterion, Interval>(Para.GENE_NUM_CRITERIA);
		if (!this.getCritSet().isEmpty()) {
			for (Iterator<Criterion> it = this.getCritSet().iterator(); it.hasNext();) {
				Criterion crit = it.next();
				Interval interval;
				if (random.nextBoolean()) {
					interval = Intervals.newUnrestrictedInterval(PreferenceDirection.MAXIMIZE, 
						Para.RATING_SCALE_MIN, Para.RATING_SCALE_MAX);
				}
				else {
					if (Config.ALWAYS_MAXIMIZE) {
						interval = Intervals.newUnrestrictedInterval(PreferenceDirection.MAXIMIZE, 
								Para.RATING_SCALE_MIN, Para.RATING_SCALE_MAX);
					}else{
						interval = Intervals.newUnrestrictedInterval(PreferenceDirection.MINIMIZE,
								Para.RATING_SCALE_MIN, Para.RATING_SCALE_MAX);
					}
				}
				re.put(crit, interval);
			}
		}
		return re;
		
	}
	
	private Map<Criterion, Double> constructCritWeights() {

		Random random = new Random(System.currentTimeMillis()); // Uniform distribution
		
		Map<Criterion, Double> re = new LinkedHashMap<Criterion, Double>(Para.GENE_NUM_CRITERIA);
		if (!this.getCritSet().isEmpty()) {
			
			// Butler and al. ,1997
			ArrayList<Integer> tempoList = new ArrayList<Integer>();
			tempoList.add(0);
			tempoList.add(100);
			
			for (int i = 0; i < this.getCritSet().size()-1; i++) {
				tempoList.add(random.nextInt(100));
			}
			Collections.sort(tempoList);
			
			int c = 0;
			for (Criterion crit : this.getCritSet()) {
				Double value = 1e-2 * (tempoList.get(c+1)- tempoList.get(c));
				re.put(crit, value);
				c++;
			}
			
//			float sum = 0;
//			ArrayList<Integer> tempoList = new ArrayList<Integer>();
//			for (int i = 0; i < this.getCritSet().size(); i++) {
//				int max = 490;
//				int min = 10;
//				int weight = min + random.nextInt(max);
//				sum += weight;
//				tempoList.add(weight);
//			}
//			
//			// Normalization
//			Iterator<Criterion> it = this.getCritSet().iterator();
//			for (int i = 0; i < this.getCritSet().size(); i++) {
//				Criterion crit = it.next();
//				double normalizedWeight = tempoList.get(i)/sum;
//				int tempo = new Double(Math.round(normalizedWeight * 1e4)).intValue();
//				re.put(crit, tempo*1e-4);
//			}
			
		}
		return re;
		
	}
	
	private Set<Alternative> constructAlteSet() {
		
		DecimalFormat df = new DecimalFormat("0000");
		Set<Alternative> re = new LinkedHashSet<Alternative>(Para.GENE_NUM_ALTERNATIVES);
		for (int i = 0; i < Para.GENE_NUM_ALTERNATIVES; i++) {
			String alteId = "a" + df.format(i+1);
			re.add(new Alternative(alteId));
		}
		return re;
		
	}
	
	private Map<Alternative, String> constructAlteNames() {
		
		Map<Alternative, String> re = new LinkedHashMap<Alternative, String>(Para.GENE_NUM_ALTERNATIVES);
		if (!getAlteSet().isEmpty()) {
			for (Iterator<Alternative> it = getAlteSet().iterator(); it.hasNext();) {
				Alternative alte = it.next();
				re.put(alte, "Unknown");
			}
		}
		return re;
		
	}
	
	private Evaluations constructAlteValues() {
		
		Random random = new Random(System.currentTimeMillis());// Uniform distribution
		
		Evaluations re = EvaluationsUtils.newEvaluationMatrix();
		
		if (!this.getAlteSet().isEmpty() && !this.getCritSet().isEmpty()) {
			
			for (Iterator<Alternative> ita = this.getAlteSet().iterator(); ita.hasNext();) {
				Alternative alternative = ita.next();
				
				for (Iterator<Criterion> itc = this.getCritSet().iterator(); itc.hasNext();) {
					Criterion criterion = itc.next();
					
					if (Config.CONTINUOUS_SCALE_MODE_ON) {
						
						double max = Para.RATING_SCALE_MAX;
						double min = Para.RATING_SCALE_MIN;
						double range = max - min;
						double value = max - range * random.nextDouble();
						re.put(alternative, criterion, value);
						
					} else {
						
						int max = (int) (Para.RATING_SCALE_MAX/Para.RATING_SCALE_INCREMENT);
						int min = (int) (Para.RATING_SCALE_MIN/Para.RATING_SCALE_INCREMENT);
						int range = max - min;
						double value =  Para.RATING_SCALE_INCREMENT * (double) (max - random.nextInt(range + 1));
						re.put(alternative, criterion, value);
						
					}
				}
			}
		}
		return re;
		
	}
	
	private Set<Alternative> constructRefPtsSet() {
		
		Set<Alternative> re = new LinkedHashSet<Alternative>();
		
		if (Config.NUM_REF_PTS_DETERMINED) {
			
			for (int i = 0; i < Para.GENE_NUM_REF_PTS; i++) {
				String id = "p" + (i + 1);
				re.add(new Alternative(id));
			}
			
		} else {
			
			Random random = new Random(System.currentTimeMillis());
			int num = Para.MAX_NUM_REF_PTS - random.nextInt(Para.MAX_NUM_REF_PTS);
			for (int i = 0; i < num; i++) {
				String id = "p" + (i + 1);
				re.add(new Alternative(id));
			}
			
		}
		return re;
		
	}
	
	private Evaluations constructRefPtsValues() {
		
		Random random = new Random(System.currentTimeMillis());// Uniform distribution
		
		Evaluations re = EvaluationsUtils.newEvaluationMatrix();
		
		if (!this.getRefPtsSet().isEmpty() && !this.getCritSet().isEmpty()) {
			
			for (Iterator<Criterion> itc = this.getCritSet().iterator(); itc.hasNext();) {
				Criterion criterion = itc.next();
				ArrayList<Double> list = new ArrayList<Double>(this.getRefPtsSet().size());
				
				for (int p = 0; p < this.getRefPtsSet().size(); p++) {
					
					/**
					 * the profiles should dominate each other
					 */
					double value = -1;
					if (Config.CONTINUOUS_SCALE_MODE_ON) {
						
						double max = Para.RATING_SCALE_MAX;
						double min = Para.RATING_SCALE_MIN;
						double range = max - min;
						value = max - range * random.nextDouble();
						
					} else {
						
						double max = Para.RATING_SCALE_MAX / Para.RATING_SCALE_INCREMENT;
						double min = Para.RATING_SCALE_MIN / Para.RATING_SCALE_INCREMENT;
						double range = max - min;
						double tempo = max - range * random.nextDouble();
						value = Para.RATING_SCALE_INCREMENT * Math.rint(tempo);
						
					}
					list.add(value);
				}
				
				Collections.sort(list);
				
				int index = 0;
				for (Iterator<Alternative> itp = this.getRefPtsSet().iterator(); itp.hasNext();) {
					Alternative profile = itp.next();
					
					if (this.getCritScales().get(criterion).getPreferenceDirection() == PreferenceDirection.MAXIMIZE) {
						re.put(profile, criterion, list.get(index));
					}
					if (this.getCritScales().get(criterion).getPreferenceDirection() == PreferenceDirection.MINIMIZE) {
						re.put(profile, criterion, list.get(this.getRefPtsSet().size()-1-index));
					}
					index++;
					
				}
			}
		}
		return re;

	}
	
	private ArrayList<Integer> constructLexico() {
		
		ArrayList<ArrayList<Integer>> list =  Permutations.getAll(this.getRefPtsSet().size());
		Random random = new Random(System.currentTimeMillis());// Uniform distribution
		int index = random.nextInt(Factorial.fac(this.getRefPtsSet().size()));
		return list.get(index);
		
	}
	
	private SparseMatrixD<Alternative,Alternative> constructAllComps() {
		
		SRMPaggregator aggr = new SRMPaggregator(this);
		aggr.execute();
		return aggr.getGlobalPrefMatrix();
	
	}
	
	private SparseMatrixD<Alternative,Alternative> constructRefComps() {
		
		Random random = new Random(System.currentTimeMillis());
		
		SparseMatrixD<Alternative, Alternative> re = Matrixes.newSparseD();
		
		ArrayList<Cell<Alternative,Alternative,Double>> cellList 
		= new ArrayList<Cell<Alternative,Alternative,Double>>(this.getAllComps().asTable().cellSet());
		ArrayList<Alternative> altList = this.getListOfAlternatives();
		
		if (RankingUtils.getIndifferenceCount(this.getAllComps()) >= this.numOfIndiff) {
			
			do {
				
				int pair = random.nextInt(cellList.size());
				Double val = cellList.get(pair).getValue();
				
				if (!val.equals(0.00)) {
					int row = altList.indexOf(cellList.get(pair).getRowKey());
					int col = altList.indexOf(cellList.get(pair).getColumnKey());
					if (row != col && !re.asTable().contains(altList.get(col), altList.get(row))) { // (col,row)
						
						re.put(altList.get(row), altList.get(col), val); // (row,col)

					}
				}
				
				
			} while (re.getValueCount() < this.numOfIndiff);
			
		}
		
		if (this.numOfIndiff < this.numOfPcomps) {
			
			do {

				int pair = random.nextInt(cellList.size());
				Double val = cellList.get(pair).getValue();
				int row = altList.indexOf(cellList.get(pair).getRowKey());
				int col = altList.indexOf(cellList.get(pair).getColumnKey());

				if (!val.equals(0.00) && val != null) {
					re.put(altList.get(row), altList.get(col), val);
				}

//				System.out.println("["+row + "," + col + "]=" +val + " " +
//						"pcomps: " + re.getValueCount() + "/" +this.numOfPcomps);
				
			} while (re.getValueCount() < this.numOfPcomps);
			
		}
		return re;
		
	}
	
	private Set<Alternative> constructAlteInPCsSet() {
		
		Set<Alternative> re = CollectionUtils.newHashSetNoNull();
		re.addAll(this.getRefComps().getRows());
		re.addAll(this.getRefComps().getColumns());

		return re;
	}
	
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////

	/**
	 * @return the numOfpcomps
	 */
	public Integer getNumOfpcomps() {
		return numOfPcomps;
	}

	/**
	 * @param numOfpcomps the numOfpcomps to set
	 */
	public void setNumOfpcomps(Integer numOfpcomps) {
		this.numOfPcomps = numOfpcomps;
	}

	/**
	 * @return the numOfIndiff
	 */
	public Integer getNumOfIndiff() {
		return numOfIndiff;
	}

	/**
	 * @param numOfIndiff the numOfIndiff to set
	 */
	public void setNumOfIndiff(Integer numOfIndiff) {
		this.numOfIndiff = numOfIndiff;
	}

}
