/**
 * 
 */
package jrmp.srmp.base;

import java.util.ArrayList;
import java.util.Iterator;

import jrmp.srmp.extension.Ranking;
import jrmp.srmp.utils.OutputUtils;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.matrix.SparseMatrixD;

import com.google.common.collect.Table.Cell;

/**
 * @author micro
 *
 */
public class SRMPmodeler {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private boolean completed = false;
	
	private XSRMPmodeler input;
	
	private XSRMPmodeler output;

	private Integer[][][] delta;
	
	private Double[][][] weightedVar;
	
	private Double[][] coalOfWeightedVar;
	
	private Double[][][] slackVar;
	
	private ArrayList<SparseMatrixD<Alternative,Alternative>> relativePrefMatrixList;
	
	private SparseMatrixD<Alternative, Alternative> globalPrefMatrix;
	
	private Ranking<Alternative> result;
	
	//@Jinyan
	private Double[] sumOfSlacks;
	
	private Double[] gamma;
	
	protected int nCrit;
	
	protected int mAlte;
	
	protected int hRefP;
	
	protected ArrayList<Alternative> rptList;
	
	protected ArrayList<Alternative> altList;
	
	protected ArrayList<Criterion> criList;
	
	protected ArrayList<Double> wgtList;
	
	//@Jinyan
	protected int numPC;
	
	protected ArrayList<Cell<Alternative,Alternative,Double>> pCoList;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public SRMPmodeler(XSRMPmodeler input) {
		
		this.setInput(input);

	}

	///////////////////
	////  METHODS  ////
	///////////////////
	
	protected SparseMatrixD<Alternative,Alternative> getRelativePrefMatrix(Alternative refPt) {
		return this.getRelativePrefMatrixList().get(rptList.indexOf(refPt));
	}
	
	/////////////////////////////
	////  DISPLAY & OUTPUT  /////
	/////////////////////////////
	
	protected void displayDelta() {
		
		OutputUtils.lscln("[Re] Binary variable matrix \"delta\":");
		for (int dh = 0; dh < hRefP; dh++) {

			OutputUtils.lsc("	* delta[" + (dh + 1) + "][m][n] *\n	");
			Iterator<Criterion> itc = this.criList.iterator();
			while (itc.hasNext()) {
				OutputUtils.lsc("	" + itc.next().getId());
			}
			OutputUtils.lscln("");

			for (int dm = 0; dm < mAlte; dm++) {
				OutputUtils.lsc("	" + this.altList.get(dm).getId());
				for (int dn = 0; dn < nCrit; dn++) {
					OutputUtils.lsc("	" + delta[dh][dm][dn]);
				}
				OutputUtils.lscln("");
			}
		}
		
	}
	
	protected void logDelta() {
		
		OutputUtils.logln("[Re] Binary variable matrix \"delta\":");
		for (int dh = 0; dh < hRefP; dh++) {

			OutputUtils.log("	* delta[" + (dh + 1) + "][m][n] *\n	");
			Iterator<Criterion> itc = this.criList.iterator();
			while (itc.hasNext()) {
				OutputUtils.log("	" + itc.next().getId());
			}
			OutputUtils.logln("");

			for (int dm = 0; dm < mAlte; dm++) {
				OutputUtils.log("	" + this.altList.get(dm).getId());
				for (int dn = 0; dn < nCrit; dn++) {
					OutputUtils.log("	" + delta[dh][dm][dn]);
				}
				OutputUtils.logln("");
			}
		}
		
	}
	
	protected void displayWeightedVar() {
		
		OutputUtils.lscln("[Re] Weighted variable matrix \"c\":");
		for (int dh = 0; dh < hRefP; dh++) {
			OutputUtils.lsc("	* c[" + (dh + 1) + "][m][n] *\n	");
			Iterator<Criterion> itc = this.criList.iterator();
			while (itc.hasNext()) {
				OutputUtils.lsc("	" + itc.next().getId());
			}
			OutputUtils.lscln("");
			for (int dm = 0; dm < mAlte; dm++) {
				OutputUtils.lsc("	" + this.altList.get(dm).getId());
				for (int dn = 0; dn < nCrit; dn++) {
					OutputUtils.lsc("	" + 
							OutputUtils.centi.format(weightedVar[dh][dm][dn]));
				}
				OutputUtils.lscln("");
			}
		}
		
	}
	
	protected void logWeightedVar() {
		
		OutputUtils.logln("[Re] Weighted variable matrix \"c\":");
		for (int dh = 0; dh < hRefP; dh++) {
			OutputUtils.log("	* c[" + (dh + 1) + "][m][n] *\n	");
			Iterator<Criterion> itc = this.criList.iterator();
			while (itc.hasNext()) {
				OutputUtils.log("	" + itc.next().getId());
			}
			OutputUtils.logln("");
			for (int dm = 0; dm < mAlte; dm++) {
				OutputUtils.log("	" + this.altList.get(dm).getId());
				for (int dn = 0; dn < nCrit; dn++) {
					OutputUtils.log("	" + 
							OutputUtils.centi.format(weightedVar[dh][dm][dn]));
				}
				OutputUtils.logln("");
			}
		}
		
	}
	
	protected void displayCoalOfWeightedVar() {
		
		OutputUtils.lsc("[Re] Coalition of weights matrix \"cw\":\n	");
		Iterator<Alternative> itp = this.rptList.iterator();
		while (itp.hasNext()) {
			OutputUtils.lsc("	" + itp.next().getId());
		}
		OutputUtils.lscln("");
		for (int dm = 0; dm < mAlte; dm++) {
			OutputUtils.lsc("	" + this.altList.get(dm).getId());
			for (int dh = 0; dh < hRefP; dh++) {
				OutputUtils.lsc("	" + 
						OutputUtils.centi.format(coalOfWeightedVar[dm][dh]));
			}
			OutputUtils.lscln("");
		}
		
	}
	
	protected void logCoalOfWeightedVar() {
		
		OutputUtils.log("[Re] Coalition of weights matrix \"cw\":\n	");
		Iterator<Alternative> itp = this.rptList.iterator();
		while (itp.hasNext()) {
			OutputUtils.log("	" + itp.next().getId());
		}
		OutputUtils.logln("");
		for (int dm = 0; dm < mAlte; dm++) {
			OutputUtils.log("	" + this.altList.get(dm).getId());
			for (int dh = 0; dh < hRefP; dh++) {
				OutputUtils.log("	" + 
						OutputUtils.centi.format(coalOfWeightedVar[dm][dh]));
			}
			OutputUtils.logln("");
		}
		
	}
	
	protected void displaySlackVar() {
		
		OutputUtils.lscln("[Re] Slack variable matrix \"c\":");
		for (int dh = 0; dh < hRefP; dh++) {
			OutputUtils.lsc("	* s[mR][mC][" + (dh + 1) + "] *\n	");
			Iterator<Alternative> ita = this.altList.iterator();
			while (ita.hasNext()) {
				OutputUtils.lsc("	" + ita.next().getId());
			}
			OutputUtils.lscln("");
			for (int dmR = 0; dmR < mAlte; dmR++) {
				OutputUtils.lsc("	" + this.altList.get(dmR).getId());
				for (int dmC = 0; dmC < mAlte; dmC++) {
					if (slackVar[dmR][dmC][dh] == null || dmR == dmC) {
						OutputUtils.lsc("	-");
					}else OutputUtils.lsc("	" + 
						OutputUtils.centi.format(slackVar[dmR][dmC][dh]));
				}
				OutputUtils.lscln("");
			}
		}
		
	}
	
	protected void logSlackVar() {
		
		OutputUtils.logln("[Re] Slack variable matrix \"c\":");
		for (int dh = 0; dh < hRefP; dh++) {
			OutputUtils.log("	* s[mR][mC][" + (dh + 1) + "] *\n	");
			Iterator<Alternative> ita = this.altList.iterator();
			while (ita.hasNext()) {
				OutputUtils.log("	" + ita.next().getId());
			}
			OutputUtils.logln("");
			for (int dmR = 0; dmR < mAlte; dmR++) {
				OutputUtils.log("	" + this.altList.get(dmR).getId());
				for (int dmC = 0; dmC < mAlte; dmC++) {
					if (slackVar[dmR][dmC][dh] == null || dmR == dmC) {
						OutputUtils.log("	-");
					}else OutputUtils.log("	" + 
						OutputUtils.centi.format(slackVar[dmR][dmC][dh]));
				}
				OutputUtils.logln("");
			}
		}
		
	}
	
	protected void displayRelativePrefMatrixList() {
		for (int h = 0; h < hRefP; h++) {
			this.displayRelativePrefMatrix(rptList.get(h));
		}
	}
	
	protected void logRelativePrefMatrixList() {
		for (int h = 0; h < hRefP; h++) {
			this.logRelativePrefMatrix(rptList.get(h));
		}
	}
	
	protected void displayRelativePrefMatrix(Alternative refPt) {

		OutputUtils.lscln("[Re] Preference with respect to the reference point \""
				+ refPt.getId() + "\":");
		OutputUtils.lsc("    (1 = Preference, 0 = Indifference, -1 = Opposite preference)\n	");
		Iterator<Alternative> ita = this.altList.iterator();
		while (ita.hasNext()) {
			OutputUtils.lsc("	" + ita.next().getId());
		}
		OutputUtils.lscln("");

		Iterator<Alternative> itR = this.getRelativePrefMatrix(refPt).getRows().iterator();
		while (itR.hasNext()) {
			Alternative alteRow = itR.next();
			OutputUtils.lsc("	" + alteRow.getId());
			Iterator<Alternative> itC = this.getRelativePrefMatrix(refPt).getColumns().iterator();
			while (itC.hasNext()) {
				Alternative alteCol = itC.next();
				Double temp = this.getRelativePrefMatrix(refPt).getValue(alteRow, alteCol);
				OutputUtils.lsc("	" + OutputUtils.integ.format(temp));
			}
			OutputUtils.lscln("");
		}
		
	}
	
	protected void logRelativePrefMatrix(Alternative refPt) {
		
		OutputUtils.logln("[Re] Preference with respect to the reference point \""
				+ refPt.getId() + "\":");
		OutputUtils.log("    (1 = Preference, 0 = Indifference, -1 = Opposite preference)\n	");
		Iterator<Alternative> ita = this.altList.iterator();
		while (ita.hasNext()) {
			OutputUtils.log("	" + ita.next().getId());
		}
		OutputUtils.logln("");

		Iterator<Alternative> itR = this.getRelativePrefMatrix(refPt).getRows().iterator();
		while (itR.hasNext()) {
			Alternative alteRow = itR.next();
			OutputUtils.log("	" + alteRow.getId());
			Iterator<Alternative> itC = this.getRelativePrefMatrix(refPt).getColumns().iterator();
			while (itC.hasNext()) {
				Alternative alteCol = itC.next();
				Double temp = this.getRelativePrefMatrix(refPt).getValue(alteRow, alteCol);
				OutputUtils.log("	" + OutputUtils.integ.format(temp));
			}
			OutputUtils.logln("");
		}
		
	}
	
	protected void displayGlobalPrefMatrix() {
		
		OutputUtils.lscln("[Re] Global preference:");
		OutputUtils.lsc("    (1 = Preference, 0 = Indifference, -1 = Opposite preference)\n	");
		Iterator<Alternative> ita = this.altList.iterator();
		while (ita.hasNext()) {
			OutputUtils.lsc("	" + ita.next().getId());
		}
		OutputUtils.lscln("");

		Iterator<Alternative> itR = this.getGlobalPrefMatrix().getRows().iterator();
		while (itR.hasNext()) {
			Alternative alteR = itR.next();
			OutputUtils.lsc("	" + alteR.getId());
			Iterator<Alternative> itC = this.getGlobalPrefMatrix().getColumns().iterator();
			while (itC.hasNext()) {
				Alternative alteC = itC.next();
				Double temp = this.getGlobalPrefMatrix().getValue(alteR, alteC);
				OutputUtils.lsc("	" + OutputUtils.integ.format(temp));
			}
			OutputUtils.lscln("");
		}
		
	}
	
	protected void logGlobalPrefMatrix() {
		
		OutputUtils.logln("[Re] Global preference:");
		OutputUtils.log("    (1 = Preference, 0 = Indifference, -1 = Opposite preference)\n	");
		Iterator<Alternative> ita = this.altList.iterator();
		while (ita.hasNext()) {
			OutputUtils.log("	" + ita.next().getId());
		}
		OutputUtils.logln("");

		Iterator<Alternative> itR = this.getGlobalPrefMatrix().getRows().iterator();
		while (itR.hasNext()) {
			Alternative alteR = itR.next();
			OutputUtils.log("	" + alteR.getId());
			Iterator<Alternative> itC = this.getGlobalPrefMatrix().getColumns().iterator();
			while (itC.hasNext()) {
				Alternative alteC = itC.next();
				Double temp = this.getGlobalPrefMatrix().getValue(alteR, alteC);
				OutputUtils.log("	" + OutputUtils.integ.format(temp));
			}
			OutputUtils.logln("");
		}
		
	}
	
	protected void displayRanking() {
		OutputUtils.lscln("[Re] Ranking of the alternatives:");
		OutputUtils.lscln(this.getRanking().toString());
	}
	
	protected void logRanking() {
		OutputUtils.logln("[Re] Ranking of the alternatives:");
		OutputUtils.logln(this.getRanking().toString());
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////
	
	/**
	 * @return the input
	 */
	public XSRMPmodeler getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(XSRMPmodeler input) {
		
		this.input = input;
		
		mAlte = this.getInput().getNumOfAlternatives();
		nCrit = this.getInput().getNumOfCriteria();
		
		altList = this.getInput().getListOfAlternatives();
		criList = this.getInput().getListOfCriteria();

		if (this.getInput().isReadyForAggr()) {
			hRefP = this.getInput().getNumOfRefPts();
			rptList = this.getInput().getListOfRefPts();
			wgtList = this.getInput().getListOfWeights();
		}
		
		//@Jinyan
		if (this.getInput().isReadyForDisag()) {
			numPC = this.getInput().getNumOfPairComps();
			pCoList = this.getInput().getListOfPairComps();
		}
		
	}
	
	/**
	 * @return the output
	 */
	public XSRMPmodeler getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(XSRMPmodeler output) {
		this.output = output;
	}

	/**
	 * @return the completed
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	protected void setCompleted(boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return the delta
	 */
	protected Integer[][][] getDelta() {
		return delta;
	}

	/**
	 * @param delta the delta to set
	 */
	protected void setDelta(Integer[][][] delta) {
		this.delta = delta;
	}

	/**
	 * @return the weightedVar
	 */
	protected Double[][][] getWeightedVar() {
		return weightedVar;
	}

	/**
	 * @param weightedVar the weightedVar to set
	 */
	protected void setWeightedVar(Double[][][] weightedVar) {
		this.weightedVar = weightedVar;
	}

	/**
	 * @return the coalOfWeightedVar
	 */
	protected Double[][] getCoalOfWeightedVar() {
		return coalOfWeightedVar;
	}

	/**
	 * @param coalOfWeightedVar the coalOfWeightedVar to set
	 */
	protected void setCoalOfWeightedVar(Double[][] coalOfWeightedVar) {
		this.coalOfWeightedVar = coalOfWeightedVar;
	}

	/**
	 * @return the slackVar
	 */
	protected Double[][][] getSlackVar() {
		return slackVar;
	}

	/**
	 * @param slackVar the slackVar to set
	 */
	protected void setSlackVar(Double[][][] slackVar) {
		this.slackVar = slackVar;
	}

	/**
	 * @return the relativePrefMatrixList
	 */
	protected ArrayList<SparseMatrixD<Alternative, Alternative>> getRelativePrefMatrixList() {
		return relativePrefMatrixList;
	}

	/**
	 * @param relativePrefMatrixList the relativePrefMatrixList to set
	 */
	protected void setRelativePrefMatrixList(
			ArrayList<SparseMatrixD<Alternative, Alternative>> relativePrefMatrixList) {
		this.relativePrefMatrixList = relativePrefMatrixList;
	}

	/**
	 * @return the globalPrefMatrix
	 */
	protected SparseMatrixD<Alternative, Alternative> getGlobalPrefMatrix() {
		return globalPrefMatrix;
	}

	/**
	 * @param globalPrefMatrix the globalPrefMatrix to set
	 */
	protected void setGlobalPrefMatrix(
			SparseMatrixD<Alternative, Alternative> globalPrefMatrix) {
		this.globalPrefMatrix = globalPrefMatrix;
	}
	
	/**
	 * @return the result
	 */
	protected Ranking<Alternative> getRanking() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	protected void setRanking(Ranking<Alternative> result) {
		this.result = result;
	}

	/**
	 * @return the sumOfSlacks
	 */
	protected Double[] getSumOfSlacks() {
		return sumOfSlacks;
	}

	/**
	 * @param sumOfSlacks the sumOfSlacks to set
	 */
	protected void setSumOfSlacks(Double[] sumOfSlacks) {
		this.sumOfSlacks = sumOfSlacks;
	}

	/**
	 * @return the gamma
	 */
	protected Double[] getGamma() {
		return gamma;
	}

	/**
	 * @param gamma the gamma to set
	 */
	protected void setGamma(Double[] gamma) {
		this.gamma = gamma;
	}
	
}
