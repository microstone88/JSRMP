/**
 * 
 */
package jrmp.srmp.base;

import java.util.ArrayList;
import java.util.Iterator;

import jrmp.srmp.settings.Para;
import jrmp.srmp.utils.OutputUtils;
import jrmp.srmp.utils.RankingUtils;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;

import com.google.common.collect.Table.Cell;

/**
 * @author micro
 *
 */
public class SRMPaggregator extends SRMPmodeler {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private boolean needToRebuid = false;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public SRMPaggregator(XSRMPmodeler input) {
		
		super(input);
		
	}
	
	///////////////////
	////  METHODS  ////
	///////////////////
	
	public boolean execute() {
		
		if (this.getInput().isReadyForAggr()) {
			
			this.calculateDelta(false);
			this.calculateWeightedVar(false);
			this.calculateCoalOfWeightedVar(false);
			this.calculateSlackVar(false);
			this.calculateRelativePrefs(false);
			this.calculateGlobalPref(false);
			
			if (this.getGlobalPrefMatrix() != null) {
				
				this.setRanking(RankingUtils.getRanking(this.getGlobalPrefMatrix()));
				this.logRanking();
				this.setCompleted(true);
				this.prepareOutput();

				return true;
				
			} else {
				OutputUtils.lcln("[Error] Aggregation failed!");
				return false;
			}
			
		}else
			return false;
		
	}
	
	public boolean execute(boolean display) {
		
		if (this.getInput().isReadyForAggr()) {
			
			OutputUtils.lscln("\n[Aggregation Process]");
			
			this.calculateDelta(display);
			this.calculateWeightedVar(display);
			this.calculateCoalOfWeightedVar(display);
			this.calculateSlackVar(display);
			this.calculateRelativePrefs(display);
			this.calculateGlobalPref(display);
			
			if (this.getGlobalPrefMatrix() != null) {
				
				this.setRanking(RankingUtils.getRanking(this.getGlobalPrefMatrix()));
				if (display) this.displayRanking();
				else this.logRanking();
				this.setCompleted(true);
				this.prepareOutput();

				OutputUtils.lscln("[i] Aggregation completed!");
				return true;
				
			} else {
				OutputUtils.lscln("[i] Aggregation failed!");
				return false;
			}
			
		}else
			return false;
		
	}
	
	public SparseMatrixD<Alternative,Alternative> rebuildPairComps() {
		
		SparseMatrixD<Alternative,Alternative> re = Matrixes.newSparseD();
		for (Iterator<Cell<Alternative, Alternative, Double>> it = this.getInput().getRefComps().asTable().cellSet().iterator(); it.hasNext();) {
			Cell<Alternative,Alternative,Double> cell = (Cell<Alternative,Alternative,Double>) it.next();
			re.put(cell.getRowKey(), cell.getColumnKey(), this.getGlobalPrefMatrix().getValue(cell.getRowKey(), cell.getColumnKey()));
		}
		return re;
	}

	private void prepareOutput() {
		
		XSRMPmodeler out = new XSRMPmodeler(this.getInput());
		out.setAllComps(this.getGlobalPrefMatrix());
		if (this.isNeedToRebuid()) {
			out.setRefComps(this.rebuildPairComps());
		}
		this.setOutput(out);
		
	}
	
	private void calculateDelta(boolean display) {
		
		if (display) {
			OutputUtils.lc("[i] Calculating the binary variables \"delta\"...");
		}else OutputUtils.log("[i] Calculating the binary variables \"delta\"...");
		
		Integer[][][] re = new Integer[hRefP][mAlte][nCrit];
		
		int h = 0;
		for (Iterator<Alternative> itp = rptList.iterator();itp.hasNext();) {
			Alternative rpt = itp.next();
			
			int m = 0;
			for (Iterator<Alternative> ita = altList.iterator();ita.hasNext();) {
				Alternative alt = ita.next();
				
				int n = 0;
				for (Iterator<Criterion> itc = criList.iterator();itc.hasNext();) {
					Criterion cri = itc.next();
					
					double val = this.getInput().getEvaluationValue(alt, cri);
					double ref = this.getInput().getRefPtsValues().getValue(rpt, cri);
					int prefDir = this.getInput().getPrefDirValue(cri);
					switch (prefDir) {
						case 1:		if (val >= ref) re[h][m][n] = 1;
									else re[h][m][n] = 0;
									break;
						case -1:	if (val <= ref) re[h][m][n] = 1;
									else re[h][m][n] = 0;
									break;
						default	: 	OutputUtils.lcln("[Error] The criterion " + cri.getId()+ " preference direction is missing.");
									break;
					}
					n++;
				}
				m++;
			}
			h++;
		}
		
		this.setDelta(re);
		
		OutputUtils.logln("OK!");
		
		if (display) {
			OutputUtils.lcln("OK!");
			this.displayDelta();
		}else
			this.logDelta();
		
	}
	
	private void calculateWeightedVar(boolean display) {
		
		if (display) {
			OutputUtils.lc("[i] Calculating the weighted variables \"c\"...");
		}else OutputUtils.log("[i] Calculating the weighted variables \"c\"...");
		
		Double[][][] re = new Double[hRefP][mAlte][nCrit];
		
		Integer[][][] d = this.getDelta();
		for (int h = 0; h < hRefP; h++) {
			for (int m = 0; m < mAlte; m++) {
				for (int n = 0; n < nCrit; n++) {
					
					if (d[h][m][n] != null) {
						re[h][m][n] = d[h][m][n] * wgtList.get(n);
					}else{
						OutputUtils.lcln("[Error] \"Delta\" is not available.");
					}
					
				}
			}
		}
		
		this.setWeightedVar(re);

		OutputUtils.logln("OK!");
		
		if (display) {
			OutputUtils.lcln("OK!");
			this.displayWeightedVar();
		}else
			this.logWeightedVar();
		
	}
	
	private void calculateCoalOfWeightedVar(boolean display) {
		
		if (display) {
			OutputUtils.lc("[i] Calculating the coalitions of weighted variables \"cw\"...");
		}else OutputUtils.log("[i] Calculating the coalitions of weighted variables \"cw\"...");
		
		Double[][] re = new Double[mAlte][hRefP];
		
		Double[][][] c = this.getWeightedVar();
		for (int h = 0; h < hRefP; h++) {
			for (int m = 0; m < mAlte; m++) {
				for (int n = 0; n < nCrit; n++) {
					
					if (c[h][m][n] != null) {
						
						if (re[m][h] == null) { re[m][h] = 0.00; }
						re[m][h] += c[h][m][n];
						
					}else{
						OutputUtils.lcln("[Error] \"c\" is not available.");
					}
					
				}
			}
		}
		
		this.setCoalOfWeightedVar(re);

		OutputUtils.logln("OK!");
		
		if (display) {
			OutputUtils.lcln("OK!");
			this.displayCoalOfWeightedVar();
		}else
			this.logCoalOfWeightedVar();
		
	}
	
	private void calculateSlackVar(boolean display) {
		
		if (display) {
			OutputUtils.lc("[i] Calculating the slack variables \"s\"...");
		}else OutputUtils.log("[i] Calculating the slack variables \"s\"...");
		
		Double[][][] re = new Double[mAlte][mAlte][hRefP];
		
		Double[][] cw = this.getCoalOfWeightedVar();
		for (int h = 0; h < hRefP; h++) {
			for (int mR = 0; mR < mAlte; mR++) {
				for (int mC = mR; mC < mAlte; mC++) {
					
					if (cw[mR][h] != null && cw[mC][h] != null) {
						re[mR][mC][h] = cw[mR][h] - cw[mC][h];
					}else{
						OutputUtils.lcln("[Error] \"cw\" is not available.");
					}
					
				}
			}
		}
		
		this.setSlackVar(re);

		OutputUtils.logln("OK!");
		
		if (display) {
			OutputUtils.lcln("OK!");
			this.displaySlackVar();
		}else
			this.logSlackVar();
		
	}
	
	private void calculateRelativePrefs(boolean display) {
		
		if (display) {
			OutputUtils.lc("[i] Determinating the relative preference relations...");
		}else OutputUtils.log("[i] Determinating the relative preference relations...");
		
		ArrayList<SparseMatrixD<Alternative, Alternative>> re = new ArrayList<SparseMatrixD<Alternative,Alternative>>();
		
		for (int h = 0; h < hRefP; h++) {
			re.add(this.calculateRelativePref(this.rptList.get(h)));
		}
		
		this.setRelativePrefMatrixList(re);

		OutputUtils.logln("OK!");
		
		if (display) {
			OutputUtils.lcln("OK!");
			this.displayRelativePrefMatrixList();
		}else
			this.logRelativePrefMatrixList();
		
	}
	
	private SparseMatrixD<Alternative, Alternative> calculateRelativePref(Alternative refPt) {
		
		SparseMatrixD<Alternative, Alternative> re = Matrixes.newSparseD();
		
		Double[][][] s = this.getSlackVar();
		for (int mR = 0; mR < mAlte; mR++) {
			for (int mC = mR; mC < mAlte; mC++) {
				
				Double val = s[mR][mC][rptList.indexOf(refPt)];
				
//				System.out.println(re.getValueCount() +","+ val);
				
				// TODO Test how to judge indifference relation
				if (val.compareTo(Para.EPSILON) >= 0) {
					re.put(this.altList.get(mR), this.altList.get(mC), 1.00);
					re.put(this.altList.get(mC), this.altList.get(mR), -1.00);
				} else
				if (val.compareTo(-Para.EPSILON) <= 0) {
					re.put(this.altList.get(mR), this.altList.get(mC), -1.00);
					re.put(this.altList.get(mC), this.altList.get(mR), 1.00);
				} else {
					re.put(this.altList.get(mR), this.altList.get(mC), 0.00);
					re.put(this.altList.get(mC), this.altList.get(mR), 0.00);
				}
				
			}
		}
		
		return re;
		
	}
	
	private void calculateGlobalPref(boolean display) {
		
		if (display) {
			OutputUtils.lc("[i] Determinating the global preference relations...");
		}else OutputUtils.log("[i] Determinating the global preference relations...");
		
		SparseMatrixD<Alternative, Alternative> re = Matrixes.newSparseD();
		
		for (int r = 0; r < this.altList.size(); r++) {
			for (int c = 0; c < this.altList.size(); c++) {
				re.put(this.altList.get(r), this.altList.get(c), 0);
			}
		}
		
		ArrayList<Integer> lexico =  this.getInput().getLexico();
		for (Iterator<Integer> itp = lexico.iterator(); itp.hasNext();) {
			
			int indexOfProfile = itp.next() - 1;
			SparseMatrixD<Alternative, Alternative> matrix = this.getRelativePrefMatrixList().get(indexOfProfile);
			
			for (Iterator<Alternative> itRow = re.getRows().iterator(); itRow.hasNext();) {
				Alternative alteRow = itRow.next();
				
				for (Iterator<Alternative> itCol = re.getColumns().iterator(); itCol.hasNext();) {
					Alternative alteCol = itCol.next();
					if (re.getValue(alteRow, alteCol) == 0) {
						re.put(alteRow, alteCol, matrix.getValue(alteRow, alteCol));
					}
				}
			
			}
			
		}
		
		this.setGlobalPrefMatrix(re);

		OutputUtils.logln("OK!");
		
		if (display) {
			OutputUtils.lcln("OK!");
			this.displayGlobalPrefMatrix();
		}else
			this.logGlobalPrefMatrix();
		
	}
	
	public Integer getRank(Alternative alternative) {
		return this.getRanking().getRank(alternative);
	}
	
	public Double getRatioOfRepresentables() {
		
		Double re = -1.00;
		if (this.getOutput() != null) {
			int num = this.getInput().getNumOfPairComps();
			int den = 0;
			if (num > 0) {
				
				for (Iterator<Cell<Alternative, Alternative, Double>> it = this.getInput().getRefComps().asTable().cellSet().iterator(); it.hasNext();) {
					Cell<Alternative, Alternative, Double> pair = it.next();
						
					double ref = this.getInput().getRefComps().getValue(pair.getRowKey(), pair.getColumnKey());
					double val = this.getOutput().getAllComps().getValue(pair.getRowKey(), pair.getColumnKey());

//					System.out.println("[" + pair.getRowKey() + "," + pair.getColumnKey() + "] = " + val + " ? " + ref);
					
					if (Math.rint(val) == Math.rint(ref) || Math.rint(val) == 0) den++;
					
				}
				re = new Double((double)den/(double)num);
			}
		}
		return re;

	}
	
	public Integer getNumOfIndifferences() {
		
		Integer re = -1;
		if (this.getOutput() != null) {
			int num = this.getInput().getNumOfPairComps();
			int den = 0;
			if (num > 0) {
				
				for (Iterator<Cell<Alternative, Alternative, Double>> it = this.getInput().getRefComps().asTable().cellSet().iterator(); it.hasNext();) {
					Cell<Alternative, Alternative, Double> pair = it.next();
						
//					double ref = this.getInput().getPairComps().getValue(pair.getRowKey(), pair.getColumnKey());
					double val = this.getOutput().getAllComps().getValue(pair.getRowKey(), pair.getColumnKey());

//					System.out.println("[" + pair.getRowKey() + "," + pair.getColumnKey() + "] = " + val + " ? " + ref);
					
					if (Math.rint(val) == 0) den++;
					
				}
				re = den;
			}
		}
		return re;

	}

	/**
	 * @return the needToRebuid
	 */
	public boolean isNeedToRebuid() {
		return needToRebuid;
	}

	/**
	 * @param needToRebuid the needToRebuid to set
	 */
	public void setNeedToRebuid(boolean needToRebuid) {
		this.needToRebuid = needToRebuid;
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////

	
}
