/**
 * 
 */
package jrmp.srmp.solver;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;


import com.google.common.collect.Table.Cell;

import ilog.concert.IloAnd;
import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloObjective;
import ilog.concert.IloOr;
import jrmp.srmp.base.XSRMPmodeler;
import jrmp.srmp.settings.Para;
import jrmp.srmp.utils.OutputUtils;

/**
 * @author micro
 *
 */
public abstract class CplSRMPsolver extends XCplSRMPsolver  {

	/**
	 * @param input
	 * @throws IloException
	 * @throws IOException 
	 */
	public CplSRMPsolver(XSRMPmodeler input) throws IOException, IloException {
		super(input);
	}
	
	protected void addWeights(boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add " + nCrit + " weight variables... ");
		else OutputUtils.log("[i] Add " + nCrit + " weight variables... ");
		
		for (int n = 0; n < nCrit; n++) {
			w[n] = cplex.numVar(Para.WEIGHT_LOWER_BOUND, Para.WEIGHT_UPPER_BOUND, "w"+n);
		}
		cplex.addEq(cplex.sum(w), 1.00, "w.norm");
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
	}
	
	protected void addNewReferenceProfile(int h, boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add a new reference profile \"p" + (h+1) + "\"... ");
		else OutputUtils.log("[i] Add a new reference profile \"p" + (h+1) + "\"... ");
		
		for (int n = 0; n < nCrit; n++) {
			p[h][n] = cplex.intVar(Para.RATING_SCALE_MIN, Para.RATING_SCALE_MAX, "p"+(h+1)+"_"+(n+1));
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
	}
	
	protected void addNewRelatedVariables(int h, boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add \"p" + (h+1) + "\"-related variables \"delta\", \"c\" and \"cw\" for each alternative in the reference set... ");
		else OutputUtils.log("[i] Add \"p" + (h+1) + "\"-related variables \"delta\", \"c\" and \"cw\" for each alternative in the reference set... ");
		
		for(int r = 0; r < mElte; r++) {
			
			for (int n = 0; n < nCrit; n++) {

				/**
				 * Add delta[k-1][r][n]
				 */
				delta[h][r][n] = cplex.boolVar("delta"+(h+1)+"_"+(r+1)+"_"+(n+1));
				
				int dir = this.getInput().getPrefDirValue(criList.get(n));
				double g = this.getInput().getEvaluationValue(eltList.get(r), criList.get(n));
				cplex.addLe(cplex.prod(Para.L, cplex.diff(delta[h][r][n], 1)), 
							cplex.prod(dir, cplex.diff(g, p[h][n])), 
							"delta_L"+(h+1)+"_"+(r+1)+"_"+(n+1));
				cplex.addLe(cplex.prod(dir, cplex.diff(g, p[h][n])), 
							cplex.diff(cplex.prod(Para.L, delta[h][r][n]), Para.EPSILON), 
							"delta_e"+(h+1)+"_"+(r+1)+"_"+(n+1));
				
				/**
				 * Add c[k-1][r][n]
				 */
				c[h][r][n] = cplex.numVar(0.00, 1.00, "c"+(h+1)+"_"+(r+1)+"_"+(n+1));
				
				cplex.addLe(c[h][r][n], w[n], "c_w"+(h+1)+"_"+(r+1)+"_"+(n+1));
				cplex.addGe(c[h][r][n], 0.00, "c_zero"+(h+1)+"_"+(r+1)+"_"+(n+1));
				cplex.addLe(c[h][r][n], delta[h][r][n], "c_d"+(h+1)+"_"+(r+1)+"_"+(n+1));
				cplex.addGe(cplex.sum(c[h][r][n], 1.00), cplex.sum(delta[h][r][n], w[n]), "c_dwo"+(h+1)+"_"+(r+1)+"_"+(n+1));
			}
			
			/**
			 * Add cw[k-1][r]
			 */
			cw[h][r] = cplex.numVar(0.00, 1.00, "cw"+(h+1)+"_"+(r+1));
			cplex.addEq(cw[h][r], cplex.sum(c[h][r]), "cw_sum"+(h+1)+"_"+(r+1));
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
	}

	protected void addNewSlackVariable(int h, boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add \"p" + (h+1) + "\"-related slack variables for each pairwise comparison in the reference set... ");
		else OutputUtils.log("[i] Add \"p" + (h+1) + "\"-related slack variables for each pairwise comparison in the reference set... ");
		
		Set<Cell<Alternative, Alternative, Double>> compSet = this.getInput().getRefComps().asTable().cellSet();
		for (Iterator<Cell<Alternative, Alternative, Double>> it = compSet.iterator(); it.hasNext();) {
			
			Cell<Alternative, Alternative, Double> comp = it.next();
			int row = eltList.indexOf(comp.getRowKey());
			int col = eltList.indexOf(comp.getColumnKey());
			
			s[row][col][h] = cplex.numVar(-1.00, 1.00, "s"+(h+1)+"("+row+","+col+")");

			double dir = comp.getValue();
			IloConstraint def = cplex.eq(cplex.prod(dir, cplex.diff(cw[h][row], cw[h][col])), s[row][col][h],
					"s_def"+(h+1)+"("+row+","+col+")");
			cplex.add(def);
			
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
	}
	
	protected void setConstToSlackVars(boolean display) throws IloException {

		if (display) OutputUtils.lc("[i] Transform the pairwise comparisons to linear constraints... ");
		else OutputUtils.log("[i] Transform the pairwise comparisons to linear constraints... ");
		
		Set<Cell<Alternative, Alternative, Double>> compSet = this.getInput().getRefComps().asTable().cellSet();
		for (Iterator<Cell<Alternative, Alternative, Double>> it = compSet.iterator(); it.hasNext();) {
			
			Cell<Alternative, Alternative, Double> comp = it.next();
			int row = eltList.indexOf(comp.getRowKey());
			int col = eltList.indexOf(comp.getColumnKey());
			double dir = comp.getValue();
			
			/**
			 * If two alternatives are not indifferent
			 */
			if (dir != 0) {
				
				IloAnd zero = cplex.and();
				IloOr positive = cplex.or();
				
				for (int h = 0; h < k; h++) {

					positive.add(cplex.ge(s[row][col][sigma.get(h)-1], Para.EPSILON));
					
					/**
					 * the slack variable with respect to the first reference point should be non-negative (6.15)
					 */
					if (h == 0) {
						IloConstraint frist = cplex.ge(s[row][col][sigma.get(h)-1], 0.00, "s_(6.15)" + sigma.get(h) + "(" + row + "," + col + ")");
						cplex.add(frist);
					}
					

					if (h != k - 1) {
						zero.add(cplex.eq(s[row][col][sigma.get(h)-1], 0.00));
					}
					
					if (k >= 2 && h != k - 1) {
						IloConstraint ifThen = cplex.ifThen(zero, cplex.ge(s[row][col][sigma.get(h+1)-1], 0.00));
						cplex.add(ifThen);
					}
				}
				cplex.add(positive); // Constraint (6.19)
			}
			
			/**
			 * TODO (to be verified) If two alternatives are indifferent 
			 */
			else {
				
				IloAnd zero = cplex.and();
				for (int h = 0; h < k; h++) {
					zero.add(cplex.eq(s[row][col][sigma.get(h)-1], 0.00));
				}
				cplex.add(zero);
				
			}
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
	}
	
	protected void setDominanceToRefPts(boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Setting dominance constraints to the reference points... ");
		else OutputUtils.log("[i] Setting dominance constraints to the reference points... ");
		
		for (int h = 1; h < k; h++) {
			for (int n = 0; n < nCrit; n++) {
				int dir = this.getInput().getPrefDirValue(criList.get(n));
				cplex.addGe(cplex.prod(dir,	cplex.diff(p[h][n], p[h-1][n])), 0.00, 
						"dom(" + (h + 1) + "," + h + ")_" + n);
			}
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
			
	}
	
	public abstract void solve(boolean display);
	
	protected abstract IloObjective addObjective(boolean display) throws IloException;

}
