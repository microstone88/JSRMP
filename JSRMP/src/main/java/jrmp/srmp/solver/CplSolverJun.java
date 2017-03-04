/**
 * 
 */
package jrmp.srmp.solver;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.collect.Table.Cell;

import ilog.concert.IloException;
import ilog.concert.IloObjective;
import jrmp.srmp.base.XSRMPmodeler;
import jrmp.srmp.math.Factorial;
import jrmp.srmp.math.Permutations;
import jrmp.srmp.settings.Config;
import jrmp.srmp.settings.Para;
import jrmp.srmp.utils.OutputUtils;

/**
 * @author micro
 *
 */
public class CplSolverJun extends CplSRMPsolver {

	public CplSolverJun(XSRMPmodeler input) throws IloException, IOException {
		
		super(input);
		
		OutputUtils.lscln("\n*** S-RMP Solver Ver.JUN,2012 ***");
		
	}

	@Override
	public void solve(boolean display) {
		
		try {
			
			while (!this.isSolved() && k <= Para.MAX_NUM_REF_PTS) {
				
				int num_lexico = Factorial.fac(k);
				lexicoList = Permutations.getAll(k);
				
				OutputUtils.lcln("\n[i] Try with " + k + " reference point(s). " + num_lexico + " possible lexicographic order(s) exist(s).");
				
				for (int t = 0; t < lexicoList.size(); t++) {
					sigma = lexicoList.get(t);

					this.addWeights(display);
					for (int h = 0; h < k; h++) {
						this.addNewReferenceProfile(h, display);
						this.addNewRelatedVariables(h, display);
						this.addNewSlackVariable(h, display);
					}
					this.setConstToSlackVars(display);
					
					/**
					 * Add dominance relations p_h < p_h+1 to the profiles on each criterion.
					 */
					if (k >= 2) {
						this.setDominanceToRefPts(display);
					}
					
					IloObjective obj = this.addObjective(display);
					
					OutputUtils.lcln("[i] " + (t+1) + "/" + num_lexico + " Try with the order " + sigma + " ...");
					
					this.exportModel(sigma);
					
					boolean bool = this.cplexSolve();
					
					if (Config.CHECK_ALL_LEXICOS) {
						if (bool) {
							this.doIfCplexSolved(sigma);
							if ((t + 1) != num_lexico) {
								this.cplexClear(obj);
								OutputUtils.lcln("[i] Check other lexico-orders...");
							}else{
								OutputUtils.lcln("[i] All lexico-orders have been checked.");
								this.setOutput(this.getSolution(this.findTheBestSolution("max")));
							}
						}else{
							OutputUtils.lcln("[i] Solution not found!");
							if ((t + 1) != num_lexico) {
								this.cplexClear(obj);
								OutputUtils.lcln("[i] Check other lexico-orders...");
							}
							else {
								OutputUtils.lcln("[i] All lexico-orders have been checked.");
								if (this.isSolved()) {
									this.setOutput(this.getSolution(this.findTheBestSolution("max")));
								}else{
									this.cplexClear(obj);
									OutputUtils.lcln("[i] Restart solving the problem...");
								}
							}
						}
					}else{
						if (bool) {
							this.doIfCplexSolved(sigma);
							this.setOutput(this.getSolution(this.findTheBestSolution("max")));
							break;
						}else {
							OutputUtils.lcln("[i] Solution not found!");
							this.cplexClear(obj);
							OutputUtils.logln("[i] Restart solving the problem...");
						}
					}
				}
				
				if (this.isSolved()) {
					this.cplexEnd();
					break;
				}else{
					k++;
				}
				
			}
					
			this.cplexUnsolvedStop();
			
		} catch (IloException e) {
		      System.err.println("Concert exception '" + e + "' caught");
		}
		
	}
	
	@Override
	protected IloObjective addObjective(boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add objective function... ");
		else OutputUtils.log("[i] Add objective function... ");
		
		IloObjective re = cplex.addMaximize();
		
		s_min = cplex.numVar(0.00, 1.00, "s_min");
		
		// Definition of "s_min"
		Set<Cell<Alternative, Alternative, Double>> compSet = this.getInput().getRefComps().asTable().cellSet();
		for (Iterator<Cell<Alternative, Alternative, Double>> it = compSet.iterator(); it.hasNext();) {
			Cell<Alternative, Alternative, Double> comp = it.next();
			int row = eltList.indexOf(comp.getRowKey());
			int col = eltList.indexOf(comp.getColumnKey());
			for (int h = 0; h < k; h++) {
				if (h == 0) {
					cplex.addLe(cplex.diff(s_min, cplex.abs(s[row][col][h])), 0);
				}
			}
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
		re.setExpr(s_min);
		return re;

	}


}
