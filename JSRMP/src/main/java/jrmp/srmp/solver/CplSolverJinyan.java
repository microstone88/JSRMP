package jrmp.srmp.solver;

import ilog.concert.IloAnd;
import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.collect.Table.Cell;

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
/**
 * @author micro
 *
 */
public class CplSolverJinyan extends CplSRMPsolver {
	
	private Integer representableInt = -1;
		
	// Compatibility
	private Double phi = new Double(-1.0);

	public CplSolverJinyan(XSRMPmodeler input) throws IOException, IloException {
		
		super(input);
		
		//@ Jinyan
		cs = new IloNumVar[numPC];
		gamma = new IloNumVar[numPC];
		
		OutputUtils.lscln("\n[S-RMP Solver Ver.JINYAN,2013]");
	
	}

	@Override
	public void solve(boolean display) {
		
		try {
			
			while (!this.isSolved() && k <= Para.MAX_NUM_REF_PTS) {
				
				int num_lexico = Factorial.fac(k);
				lexicoList = Permutations.getAll(k);
				
				OutputUtils.lcln("[i] Try with " + k + " reference point(s). " + num_lexico + " possible lexicographic order(s) exist(s).");
				
				for (int t = 0; t < lexicoList.size(); t++) {
					sigma = lexicoList.get(t);

					this.addWeights(display);
					for (int h = 0; h < k; h++) {
						this.addNewReferenceProfile(h, display);
						this.addNewRelatedVariables(h, display);
						this.addNewSlackVariable(h, display);
					}
					this.setConstToSlackVars(display);
					
					//@Jinyan
					this.addCsAndGamma(display);
					
					/**
					 * Add dominance relations p_h < p_h+1 to the profiles on each criterion.
					 */
					if (k >= 2) {
						this.setDominanceToRefPts(display);
					}
					
					obj = this.addObjective(display);
					
					OutputUtils.lcln("[i] " + (t+1) + "/" + num_lexico + " Try with the order " + sigma + " ...");
					
					this.exportModel(sigma);
					
					boolean bool = this.cplexSolve();
					
					if (Config.CHECK_ALL_LEXICOS) {
						if (bool) {
							
							if (this.isAcceptable()) {
								this.doIfCplexSolved(sigma);
								if ((t + 1) != num_lexico) {
									this.cplexClear(obj);
									OutputUtils.lcln("[i] Check other lexico-orders...");
								} else {
									OutputUtils.lcln("[i] All lexico-orders have been checked.");
									this.setOutput(this.getSolution(this.findTheBestSolution("max")));
								}
							}
							else this.ifNotCompatible();
							
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
							
							if (this.isAcceptable()) {
								this.doIfCplexSolved(sigma);
								this.setOutput(this.getSolution(this.findTheBestSolution("max")));
								break;
							}
							else this.ifNotCompatible();
							
						}else{
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
	
	protected void addCsAndGamma(boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add binary variables gamma... ");
		else OutputUtils.log("[i] Add binary variables gamma... ");
		
		for (int i = 0; i < numPC; i++) {
			
			cs[i] = cplex.numVar(-100.00, 100.00, "cs_" + (i+1));
			gamma[i] = cplex.boolVar("gamma_" + (i+1));
			
			int row = eltList.indexOf(pCoList.get(i).getRowKey());
			int col = eltList.indexOf(pCoList.get(i).getColumnKey());
			
			IloNumExpr sum = s[row][col][0];
			for (int h = 1; k > 1 && h < k; h++) {
				sum = cplex.sum(sum, s[row][col][h]);
			}
			cplex.addEq(cs[i], sum, "cs_" + (i+1) + "def" + "("	+ row + "," + col + ")");
			
			cplex.addGe(cs[i], cplex.prod(Para.EPSILON, gamma[i]), "gamma_e_" + i);
			cplex.addLe(cs[i], cplex.prod(Para.L, gamma[i]), "gamma_L_" + i);
			
		}
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");

	}
	
	@Override
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
//				IloOr positive = cplex.or();
				
				for (int h = 0; h < k; h++) {

					/**
					 * the slack variable with respect to the first reference point should be non-negative (6.15)
					 */
					if (h == 0) {
						IloConstraint frist = cplex.ge(s[row][col][sigma.get(h)-1], 0.00, "s_(6.15)" + sigma.get(h) + "(" + row + "," + col + ")");
						cplex.add(frist);
					}
					
//					positive.add(cplex.ge(s[row][col][sigma.get(h)-1], Para.EPSILON));

					if (h != k - 1) {
						zero.add(cplex.eq(s[row][col][sigma.get(h)-1], 0.00));
					}
					
					if (k >= 2) {
						IloConstraint ifThen = cplex.ifThen(zero, cplex.ge(s[row][col][sigma.get(h)-1], 0.00));
						cplex.add(ifThen);
					}
				}
//				cplex.add(positive); // Constraint (6.19)
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
	
	@Override
	protected IloObjective addObjective(boolean display) throws IloException {
		
		if (display) OutputUtils.lc("[i] Add objective function... ");
		else OutputUtils.log("[i] Add objective function... ");
		
		IloObjective re = cplex.addMaximize();
		
		representable = cplex.numVar(0, numPC, "representable");
		
		cplex.addEq(representable, cplex.sum(gamma), "representable_def");
		re.setExpr(representable);
		
		if (display) OutputUtils.lcln("Done!");
		else OutputUtils.logln("Done!");
		
		return re;
	}
	
	@Override
	protected void doIfCplexSolved(ArrayList<Integer> sigma) throws IloException {
		
		super.doIfCplexSolved(sigma);
		
		this.setSumOfSlacks(getResultOfSumOfSlacks());
		this.setGamma(getResultOfGamma());
	
	}
	
	protected boolean isAcceptable() throws IloException {
		
		this.setRepresentableInt(new Double(Math.rint(cplex.getValue(representable))).intValue());
		
		if (this.getCompatibility() < 0) {
			this.setCompatibility(this.calculateCompatibility());
		}
		
		OutputUtils.lscln("[i] The inferred model is " 
				+ OutputUtils.centi.format(this.getCompatibility()*100) 
				+ "% compatible with the input."
				+ "(Estimated >=" + (1-Para.ESTIMATED_INCONS_LEVEL)*100 + "%)");
		
		if (this.getCompatibility() >= 1 - Para.ESTIMATED_INCONS_LEVEL) return true;
		else {
			this.setRepresentableInt(-1);
			this.setCompatibility(-1.0);
			return false;
		}
		
	}
	
	protected Double calculateCompatibility() throws IloException {
		
		if (cplex != null && numPC != 0) {
			Double re = (double) this.getRepresentableInt() / (double) numPC;
			return re;
		}
		else {
			return -1.0;
		}
		
	}
	
	public Double getCompatibility() {
		return phi;
	}
	
	protected void setCompatibility(double value) throws IloException {
		this.phi = value;
	}
	
	protected void ifNotCompatible() throws IloException {
		OutputUtils.lcln("[i] Solution not acceptable!");
		this.cplexClear(obj);
		OutputUtils.logln("[i] Restart solving the problem...");
	}
	
	public String getGammaStatus() {
		
		String re = "[i] Gamma status: ";
		Double[] gamma = this.getGamma();
		for (int i = 0; i < gamma.length; i++) {
			if (i%5 == 0) re += "\n";
			re += "	(" + (i+1) + "," + gamma[i].intValue() + ")";
		}
		return re;
		
	}
	
	public String getSlackStatus() {
		
		String re = "[i] Slack status: ";
		Double[] slack = this.getSumOfSlacks();
		for (int i = 0; i < slack.length; i++) {
			if (i%5 == 0) re += "\n";
			re += "	(" + (i+1) + "," + OutputUtils.tenmi.format(slack[i].doubleValue()) + ")";
		}
		return re;
		
	}

	/**
	 * @return the representableInt
	 */
	public Integer getRepresentableInt() {
		return representableInt;
	}

	/**
	 * @param representableInt the representableInt to set
	 */
	protected void setRepresentableInt(Integer representableInt) {
		this.representableInt = representableInt;
	}
	
}
