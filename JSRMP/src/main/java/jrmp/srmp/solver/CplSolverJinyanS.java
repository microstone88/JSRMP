/**
 * 
 */
package jrmp.srmp.solver;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloObjective;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.collect.Table.Cell;

import jrmp.srmp.base.XSRMPmodeler;
import jrmp.srmp.settings.Para;
import jrmp.srmp.utils.OutputUtils;

/**
 * @author micro
 *
 */
public class CplSolverJinyanS extends CplSolverJinyan {

	/**
	 * @param input
	 * @throws IOException
	 * @throws IloException
	 */
	public CplSolverJinyanS(XSRMPmodeler input) throws IOException,
			IloException {
		super(input);
	}
	
	@Override
	protected IloObjective addObjective(boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add objective function... ");
		else OutputUtils.log("[i] Add objective function... ");
		
		IloObjective re = cplex.addMaximize();
		
		representable = cplex.numVar(0, numPC, "representable");
		s_min = cplex.numVar(0.00, 1.00, "s_min");
		
		// Definition of "representable"
		cplex.addEq(representable, cplex.sum(gamma), "representable_def");
		
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
		
		IloNumExpr combi = cplex.sum(representable, cplex.prod(Para.AMPLIFI_SLACK, s_min));
		re.setExpr(combi);
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
		return re;
	}

}
