package jrmp.srmp.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jrmp.srmp.utils.OutputUtils;
import jrmp.srmp.utils.RankingUtils;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.relation.graph.Preorder;

import com.google.common.collect.Table.Cell;

/**
 * @author micro
 *
 */
public class XSRMPmodeler {
	
	public enum Status {
		initialized, loaded, generated, overwritten
	}
	
	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private Status status; //"initialized", "loaded", "generated" or "overwritten"
	
	private Set<Alternative> alteSet;
	private Map<Alternative, String> alteNames;
	private Evaluations alteValues;
	
	private Set<Criterion> critSet;
	private Map<Criterion, String> critNames;
	private Map<Criterion, Interval> critScales;
	
	private Map<Criterion, Double> weights;
	private Set<Alternative> refPtsSet;
	private Evaluations refPtsValues;
	private ArrayList<Integer> lexico;
	
	/**
	 * @author micro
	 * @since 04-dec-2013
	 * Use XMCDA "alternativeValue" tag for storing lexicography
	 */
	private Preorder<Alternative> lexicography;

	private Set<Alternative> alteInPCs;
	private SparseMatrixD<Alternative, Alternative> allComps;
	private SparseMatrixD<Alternative, Alternative> refComps;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public XSRMPmodeler() {
		
		this.status = XSRMPmodeler.Status.initialized;
		
		this.alteSet = null;
		this.alteNames = null;
		this.alteValues = null;
		
		this.critSet = null;
		this.critNames = null;
		this.critScales = null;
		
		this.weights = null;
		this.refPtsSet = null;
		this.refPtsValues = null;
		this.lexico = null;
		
		this.alteInPCs = null;
		this.allComps = null;
		this.refComps = null;
		
	}
	
	public XSRMPmodeler(XSRMPmodeler model) {

		this.copy(model);
		
	}
	
	///////////////////
	////  METHODS  ////
	///////////////////
	
	public void copy(XSRMPmodeler model) {
		
		if (model.getStatus() != null) this.setStatus(model.getStatus());
		
		if (model.getAlteSet() != null) this.setAlteSet(model.getAlteSet());
		if (model.getAlteNames() != null) this.setAlteNames(model.getAlteNames());
		if (model.getAlteValues() != null) this.setAlteValues(model.getAlteValues());
		
		if (model.getCritSet() != null) this.setCritSet(model.getCritSet());
		if (model.getCritNames() != null) this.setCritNames(model.getCritNames());
		if (model.getCritScales() != null) this.setCritScales(model.getCritScales());
		
		if (model.getWeights() != null) this.setWeights(model.getWeights());
		if (model.getRefPtsSet() != null) this.setRefPtsSet(model.getRefPtsSet());
		if (model.getRefPtsValues() != null) this.setRefPtsValues(model.getRefPtsValues());
		if (model.getLexico() != null) this.setLexico(model.getLexico());
		
		if (model.getAlteInPCs() != null) this.setAlteInPCs(model.getAlteInPCs());
		if (model.getAllComps() != null) this.setAllComps(model.getAllComps());
		if (model.getRefComps() != null) this.setRefComps(model.getRefComps());
		
	}
	
	/**
	 * @return true,if the model is ready for aggregating. False,if not.
	 */
	public boolean isReadyForAggr() {
		if ((this.getAlteSet() != null)
				&& (this.getAlteValues() != null)
				&& (this.getCritSet() != null)
				&& (this.getCritScales() != null)
				&& (this.getWeights() != null)
				&& (this.getRefPtsSet() != null)
				&& (this.getRefPtsValues() != null)
				&& (this.getLexico() != null)) 
			return true;
		else return false;
	}
	
	/**
	 * @return true,if the model is ready for disaggregating. False,if not.
	 */
	public boolean isReadyForDisag() {
		if ((this.getAlteSet() != null)
				&& (this.getAlteValues() != null)
				&& (this.getCritSet() != null)
				&& (this.getCritScales() != null)
				&& (this.getRefComps() != null)
				&& (this.getAlteInPCs() != null))
			return true;
		else return false;
	}
	
	public Integer getNumOfAlternatives() {
		return this.getAlteSet().size();
	}
	
	public Integer getNumOfCriteria() {
		return this.getCritSet().size();
	}
	
	public Integer getNumOfRefPts() {
		return this.getRefPtsSet().size();
	}
	
	public Integer getNumOfPairComps() {
		return this.getRefComps().getValueCount();
	}
	
	public Integer getNumOfAltesInPCs() {
		return this.getAlteInPCs().size();
	}

	public Double getEvaluationValue(Alternative alternative, Criterion criterion) {
		return this.getAlteValues().getValue(alternative, criterion);
	}
	
	public Integer getPrefDirValue(Criterion criterion) {
		String dir = this.getCritScales().get(criterion).getPreferenceDirection().toString();
		if ( dir.toLowerCase().contains("max")) return 1;
		if ( dir.toLowerCase().contains("min")) return -1;
		else return 0;
	}
	
	public Integer getCompType(Alternative right, Alternative left) {
		return (int) this.getRefComps().getValue(right, left);
	}
	
	public ArrayList<Alternative> getListOfAlternatives() {
		return new ArrayList<Alternative>(this.getAlteSet());
	}
	
	public ArrayList<Criterion> getListOfCriteria() {
		return new ArrayList<Criterion>(this.getCritSet());
	}
	
	public ArrayList<Alternative> getListOfRefPts() {
		return new ArrayList<Alternative>(this.getRefPtsSet());
	}
	
	public ArrayList<Double> getListOfWeights() {
		return new ArrayList<Double>(this.getWeights().values());
	}
	
	public ArrayList<Alternative> getListOfAltesInPCs() {
		return new ArrayList<Alternative>(this.getAlteInPCs());
	}
	
	//@Jinyan
	public ArrayList<Cell<Alternative,Alternative,Double>> getListOfPairComps() {
		return new ArrayList<Cell<Alternative,Alternative,Double>>(this.getRefComps().asTable().cellSet());
	}
	
	////////////////////////////
	////  DISPLAY & OUTPUT  ////
	////////////////////////////
	public void displayAlternatives() {
		
		OutputUtils.lscln("[Re] Alternatives:");
		if (this.getAlteSet() != null) {
			Iterator<Alternative> it = this.getAlteSet().iterator();
			while (it.hasNext()) {
				Alternative alte = it.next();
				String id = alte.getId();
				OutputUtils.lscln("    Id: " + id + ": "
						+ this.getAlteNames().get(alte));
			}
		}
		
	}
	
	public void outputAlternatives() {
		
		OutputUtils.logln("[Re] Alternatives:");
		if (this.getAlteSet() != null) {
			Iterator<Alternative> it = this.getAlteSet().iterator();
			while (it.hasNext()) {
				Alternative alte = it.next();
				String id = alte.getId();
				OutputUtils.logln("    Id: " + id + ": "
						+ this.getAlteNames().get(alte));
			}
		}
		
	}
	
	public void displayCriteria() {
		
		OutputUtils.lscln("[Re] Criteria:");
		if (this.getCritSet() != null) {
			Iterator<Criterion> it = this.getCritSet().iterator();
			while (it.hasNext()) {
				Criterion crit = it.next();
				String id = crit.getId();
				OutputUtils.lscln("    Id: "
						+ id
						+ ": "
						+ this.getCritNames().get(crit)
						+ "("
						+ this.getCritScales().get(crit)
								.getPreferenceDirection().toString()
								.toLowerCase() + " Max = "
						+ this.getCritScales().get(crit).getMaximum()
						+ " Min = "
						+ this.getCritScales().get(crit).getMinimum() + ")");
			}
		}

	}
	
	public void outputCriteria() {
		
		OutputUtils.logln("[Re] Criteria:");
		if (this.getCritSet() != null) {
			Iterator<Criterion> it = this.getCritSet().iterator();
			while (it.hasNext()) {
				Criterion crit = it.next();
				String id = crit.getId();
				OutputUtils.logln("    Id: "
						+ id
						+ ": "
						+ this.getCritNames().get(crit)
						+ "("
						+ this.getCritScales().get(crit)
								.getPreferenceDirection().toString()
								.toLowerCase() + " Max = "
						+ this.getCritScales().get(crit).getMaximum()
						+ " Min = "
						+ this.getCritScales().get(crit).getMinimum() + ")");
			}
		}

	}
	
	public void displayEvaluations() {
		
		OutputUtils.lsc("[Re] Alternative performance table:\n    	");
		if (this.getAlteValues() != null) {
			Iterator<Criterion> itc = this.getAlteValues().getColumns()
					.iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				OutputUtils.lsc("	" + crit.getId());
			}
			OutputUtils.lscln("");
			Iterator<Alternative> ita = this.getAlteValues().getRows()
					.iterator();
			while (ita.hasNext()) {
				Alternative alte = ita.next();
				OutputUtils.lsc("	" + alte.getId());
				Iterator<Criterion> itac = this.getAlteValues().getColumns()
						.iterator();
				while (itac.hasNext()) {
					Criterion crit = itac.next();
					OutputUtils.lsc("	"
							+ this.getAlteValues().getValue(alte, crit));
				}
				OutputUtils.lscln("");
			}
		}
		
	}
	
	public void outputEvaluations() {
		
		OutputUtils.log("[Re] Alternative performance table:\n    	");
		if (this.getAlteValues() != null) {
			Iterator<Criterion> itc = this.getAlteValues().getColumns()
					.iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				OutputUtils.log("	" + crit.getId());
			}
			OutputUtils.logln("");
			Iterator<Alternative> ita = this.getAlteValues().getRows()
					.iterator();
			while (ita.hasNext()) {
				Alternative alte = ita.next();
				OutputUtils.log("	" + alte.getId());
				Iterator<Criterion> itac = this.getAlteValues().getColumns()
						.iterator();
				while (itac.hasNext()) {
					Criterion crit = itac.next();
					OutputUtils.log("	"
							+ this.getAlteValues().getValue(alte, crit));
				}
				OutputUtils.logln("");
			}
		}

	}
	
	public void displayWeights() {
		
		OutputUtils.lscln("[Re] Criteria weights:");
		
		if (this.getWeights() != null) {
			Iterator<Criterion> it = this.getWeights().keySet().iterator();
			while (it.hasNext()) {
				Criterion crit = it.next();
				Float val = Float.parseFloat(OutputUtils.centi.format(this
						.getWeights().get(crit)));
				OutputUtils.lsc("    " + crit.getId() + ":" + val);
			}
			OutputUtils.lscln("");
		}
		
	}
	
	public void outputWeights() {
		
		OutputUtils.logln("[Re] Criteria weights:");
		
		if (this.getWeights() != null) {
			Iterator<Criterion> it = this.getWeights().keySet().iterator();
			while (it.hasNext()) {
				Criterion crit = it.next();
				Float val = Float.parseFloat(OutputUtils.centi.format(this
						.getWeights().get(crit)));
				OutputUtils.log("    " + crit.getId() + ":" + val);
			}
			OutputUtils.logln("");
		}
		
	}
	
	public void displayRefPts() {
		
		OutputUtils.lscln("[Re] Configuration of the reference points:");
		
		if (this.getRefPtsSet() != null) {
			Iterator<Criterion> itc = this.getRefPtsValues().getColumns()
					.iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				OutputUtils.lsc("	" + crit.getId());
			}
			OutputUtils.lscln("");
			Iterator<Alternative> itp = this.getRefPtsValues().getRows()
					.iterator();
			while (itp.hasNext()) {
				Alternative p = itp.next();
				OutputUtils.lsc("    " + p.getId());
				Iterator<Criterion> itac = this.getRefPtsValues().getColumns()
						.iterator();
				while (itac.hasNext()) {
					Criterion crit = itac.next();
					OutputUtils.lsc("	"
							+ OutputUtils.centi.format(this.getRefPtsValues()
									.getValue(p, crit)));
				}
				OutputUtils.lscln("");
			}
		}

	}
	
	public void outputRefPts() {
		
		OutputUtils.logln("[Re] Configuration of the reference points:");
		
		if (this.getRefPtsSet() != null) {
			Iterator<Criterion> itc = this.getRefPtsValues().getColumns()
					.iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				OutputUtils.log("	" + crit.getId());
			}
			OutputUtils.logln("");
			Iterator<Alternative> itp = this.getRefPtsValues().getRows()
					.iterator();
			while (itp.hasNext()) {
				Alternative p = itp.next();
				OutputUtils.log("    " + p.getId());
				Iterator<Criterion> itac = this.getRefPtsValues().getColumns()
						.iterator();
				while (itac.hasNext()) {
					Criterion crit = itac.next();
					OutputUtils.log("	"
							+ OutputUtils.centi.format(this.getRefPtsValues()
									.getValue(p, crit)));
				}
				OutputUtils.logln("");
			}
		}

	}
	
	public void displayLexico() {
		
		if (this.getLexico() != null) {
			OutputUtils.lscln("[Re] Lexicographic order of reference points:"
					+ this.getLexico());
		}

	}
	
	public void outputLexico() {
		
		if (this.getLexico() != null) {
			OutputUtils.logln("[Re] Lexicographic order of reference points:"
					+ this.getLexico());
		}
		
	}
	
	public void displayEvalsOfAltesInPCs() {
		
		OutputUtils.lsc("[Re] Evaluation of the alternatives involved in pairwise comparisons:\n    	");
		if (this.getAlteInPCs() != null) {
			Iterator<Criterion> itc = this.getAlteValues().getColumns().iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				OutputUtils.lsc("	" + crit.getId());
			}
			OutputUtils.lscln("");
			Iterator<Alternative> ita = this.getAlteInPCs().iterator();
			while (ita.hasNext()) {
				Alternative alte = ita.next();
				OutputUtils.lsc("	" + alte.getId());
				Iterator<Criterion> itac = this.getAlteValues().getColumns()
						.iterator();
				while (itac.hasNext()) {
					Criterion crit = itac.next();
					OutputUtils.lsc("	"
							+ this.getEvaluationValue(alte, crit));
				}
				OutputUtils.lscln("");
			}
		}
		
	}
	
	public void outputEvalsOfAltesInPCs() {
		
		OutputUtils.log("[Re] Evaluation of the alternatives involved in pairwise comparisons:\n    	");
		if (this.getAlteInPCs() != null) {
			Iterator<Criterion> itc = this.getAlteValues().getColumns().iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				OutputUtils.log("	" + crit.getId());
			}
			OutputUtils.logln("");
			Iterator<Alternative> ita = this.getAlteInPCs().iterator();
			while (ita.hasNext()) {
				Alternative alte = ita.next();
				OutputUtils.log("	" + alte.getId());
				Iterator<Criterion> itac = this.getAlteValues().getColumns()
						.iterator();
				while (itac.hasNext()) {
					Criterion crit = itac.next();
					OutputUtils.log("	"
							+ this.getEvaluationValue(alte, crit));
				}
				OutputUtils.logln("");
			}
		}
		
	}
	
	public void displayRefComps() {
		
		OutputUtils.lscln("[Re] Reference pairwise comparisons:");
		if (this.getRefComps() != null) {
			int n = 1;
			Iterator<Cell<Alternative, Alternative, Double>> it = this
					.getRefComps().asTable().cellSet().iterator();
			while (it.hasNext()) {
				Cell<Alternative, Alternative, Double> cell = it.next();
				Alternative alteHigh = cell.getRowKey();
				Alternative alteLow = cell.getColumnKey();
				if (cell.getValue() == 1) {
					OutputUtils.lscln("    " + n + "	: " + alteHigh.getId()
							+ " > " + alteLow.getId());
				}
				if (cell.getValue() == -1) {
					OutputUtils.lscln("    " + n + "	: " + alteLow.getId()
							+ " > " + alteHigh.getId());
				}
				if (cell.getValue() == 0) {
					OutputUtils.lscln("    " + n + "	: " + alteHigh.getId()
							+ " ~ " + alteLow.getId());
				}
				n++;
			}
		}
		
	}
	
	public void outputRefComps() {
		
		OutputUtils.logln("[Re] Reference pairwise comparisons:");
		if (this.getRefComps() != null) {
			int n = 1;
			Iterator<Cell<Alternative, Alternative, Double>> it = this
					.getRefComps().asTable().cellSet().iterator();
			while (it.hasNext()) {
				Cell<Alternative, Alternative, Double> cell = it.next();
				Alternative alteHigh = cell.getRowKey();
				Alternative alteLow = cell.getColumnKey();
				if (cell.getValue() == 1) {
					OutputUtils.logln("    " + n + ": " + alteHigh.getId()
							+ " > " + alteLow.getId());
				}
				if (cell.getValue() == -1) {
					OutputUtils.logln("    " + n + ": " + alteLow.getId()
							+ " > " + alteHigh.getId());
				}
				if (cell.getValue() == 0) {
					OutputUtils.logln("    " + n + ": " + alteHigh.getId()
							+ " ~ " + alteLow.getId());
				}
				n++;
			}
		}
		
	}
	
	@Deprecated
	public void displayRefRanking() {
		
		OutputUtils.lscln("[Re] Reference ranking list:");
		if (this.getRefComps() != null) {
			OutputUtils.lscln(RankingUtils.getRanking(this.getRefComps()).toString());
		}

	}
	
	@Deprecated
	public void outputRefRanking() {
		
		OutputUtils.logln("[Re] Reference ranking list:");
		if (this.getRefComps() != null) {
			OutputUtils.logln(RankingUtils.getRanking(this.getRefComps()).toString());
		}
		
	}
	
	public void displayGlobalRanking() {
		
		OutputUtils.lscln("[Re] Global ranking list:");
		if (this.getAllComps() != null) {
			OutputUtils.lscln(RankingUtils.getRanking(this.getAllComps()).toString());
		}

	}
	
	public void outputGlobalRanking() {
		
		OutputUtils.logln("[Re] Global ranking list:");
		if (this.getAllComps() != null) {
			OutputUtils.logln(RankingUtils.getRanking(this.getAllComps()).toString());
		}
		
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return the alteSet
	 */
	public Set<Alternative> getAlteSet() {
		return alteSet;
	}

	/**
	 * @return the alteNames
	 */
	public Map<Alternative, String> getAlteNames() {
		return alteNames;
	}

	/**
	 * @return the alteValues
	 */
	public Evaluations getAlteValues() {
		return alteValues;
	}

	/**
	 * @return the critSet
	 */
	public Set<Criterion> getCritSet() {
		return critSet;
	}

	/**
	 * @return the critNames
	 */
	public Map<Criterion, String> getCritNames() {
		return critNames;
	}

	/**
	 * @return the critScales
	 */
	public Map<Criterion, Interval> getCritScales() {
		return critScales;
	}

	/**
	 * @return the weights
	 */
	public Map<Criterion, Double> getWeights() {
		return weights;
	}

	/**
	 * @return the refPtsSet
	 */
	public Set<Alternative> getRefPtsSet() {
		return refPtsSet;
	}

	/**
	 * @return the refPtsValues
	 */
	public Evaluations getRefPtsValues() {
		return refPtsValues;
	}

	/**
	 * @return the lexico
	 */
	public ArrayList<Integer> getLexico() {
		return lexico;
	}

	/**
	 * @return the alteInPCs
	 */
	public Set<Alternative> getAlteInPCs() {
		return alteInPCs;
	}

	/**
	 * @return the allComps
	 */
	public SparseMatrixD<Alternative, Alternative> getAllComps() {
		return allComps;
	}

	/**
	 * @return the refComps
	 */
	public SparseMatrixD<Alternative, Alternative> getRefComps() {
		return refComps;
	}
	
	/**
	 * @return the lexicography
	 */
	public Preorder<Alternative> getLexicography() {
		return lexicography;
	}

	/**
	 * @param status the status to set
	 */
	protected void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @param alteSet the alteSet to set
	 */
	protected void setAlteSet(Set<Alternative> alteSet) {
		this.alteSet = alteSet;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param alteNames the alteNames to set
	 */
	protected void setAlteNames(Map<Alternative, String> alteNames) {
		this.alteNames = alteNames;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param alteValues the alteValues to set
	 */
	protected void setAlteValues(Evaluations alteValues) {
		this.alteValues = alteValues;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param critSet the critSet to set
	 */
	protected void setCritSet(Set<Criterion> critSet) {
		this.critSet = critSet;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param critNames the critNames to set
	 */
	protected void setCritNames(Map<Criterion, String> critNames) {
		this.critNames = critNames;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param critScales the critScales to set
	 */
	protected void setCritScales(Map<Criterion, Interval> critScales) {
		this.critScales = critScales;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param weights the weights to set
	 */
	public void setWeights(Map<Criterion, Double> weights) {
		this.weights = weights;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param refPtsSet the refPtsSet to set
	 */
	public void setRefPtsSet(Set<Alternative> refPtsSet) {
		this.refPtsSet = refPtsSet;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param refPtsValues the refPtsValues to set
	 */
	public void setRefPtsValues(Evaluations refPtsValues) {
		this.refPtsValues = refPtsValues;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param lexico the lexico to set
	 */
	public void setLexico(ArrayList<Integer> lexico) {
		this.lexico = lexico;
		this.setStatus(XSRMPmodeler.Status.overwritten);
		
		/**
		 * Update the lexicography
		 */
		Preorder<Alternative> re = new Preorder<>();
		ArrayList<Alternative> refPts = this.getListOfRefPts();
		for (int i = 0; i < lexico.size(); i++) {
			re.put(refPts.get(lexico.get(i)-1), i+1);
		}
		this.lexicography = re;

	}

	/**
	 * @param alteInPCs the alteInPCs to set
	 */
	public void setAlteInPCs(Set<Alternative> alteInPCs) {
		this.alteInPCs = alteInPCs;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param allComps the allComps to set
	 */
	public void setAllComps(SparseMatrixD<Alternative, Alternative> allComps) {
		this.allComps = allComps;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}

	/**
	 * @param refComps the refComps to set
	 */
	public void setRefComps(SparseMatrixD<Alternative, Alternative> refComps) {
		this.refComps = refComps;
		this.setStatus(XSRMPmodeler.Status.overwritten);
	}


	/**
	 * @param lexicography the lexicography to set
	 * @throws Exception 
	 */
	public void setLexicography(Preorder<Alternative> lexicography) throws Exception {
		this.lexicography = lexicography;
		this.setStatus(XSRMPmodeler.Status.overwritten);
		
		if (this.getListOfRefPts() != null) {
			ArrayList<Integer> re = new ArrayList<Integer>();
			ArrayList<Alternative> refPts = this.getListOfRefPts();
			for (int i = 0; i < lexicography.size(); i++) {
				re.add(1 + refPts.indexOf(lexicography.get(i + 1).iterator()
						.next()));
			}
			this.lexico = re;
		}
//		else throw new Exception("sdf");
		
	}
	
	

}
