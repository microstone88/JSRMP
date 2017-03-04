/**
 * 
 */
package jrmp.srmp.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.utils.collection.CollectionUtils;

import com.google.common.collect.Table.Cell;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;
import jrmp.srmp.base.SRMPsolver;
import jrmp.srmp.base.XSRMPmodeler;
import jrmp.srmp.utils.OutputUtils;
import jrmp.srmp.settings.Config;
import jrmp.srmp.settings.Para;

/**
 * @author micro
 *
 */
public abstract class XCplSRMPsolver extends SRMPsolver {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	protected IloCplex cplex;
	
	protected IloObjective obj;
	
	/** *** Preference Parameters to Elicit ***
	 *  
	 *  1. number of profiles 	: Integer k
	 *  2. criteria weights w	: Double[] w[numCrit]
	 *  3. profiles p_1,...,p_k	: Double[][] p[k][numCrit]
	 *  4. lexicographic order	: ArrayList sigma
	 */
	protected int k;
	protected IloNumVar[] w;
	protected IloIntVar[][] p;
	protected ArrayList<Integer> sigma;
	
	/** *** List of binary variables ***
	 * 
	 *  1. Relative position of an alternative to a reference profile on a criterion
	 *     IloIntVar[][][]	 delta[k][m_E][numCrit]	
	 */
	protected IloIntVar[][][] delta;
	
	/** *** List of Numeric variables ***
	 * 
	 *  1. IloNumVar[][][]		 c[k][m_E][numCrit]	: c = delta * w
	 *  2. IloNumVar[][][]		 s[m_E][m_E][k]		: slack variable
	 *  3. IloNumVar[][]		cw[m_E][k]			: coalition of criteria weights for which a is at least as good as p
	 *  4. IloNumVar[]	 	 s_min[k]				: objective (compare to the p_k)
	 */
	protected IloNumVar[][][] c;
	protected IloNumVar[][][] s;
	protected IloNumVar[][] cw;
	protected IloNumVar s_min;
	
	protected ArrayList<ArrayList<Integer>> lexicoList;
	
	//@Jinyan
	protected IloNumVar[] cs;
	protected IloNumVar[] gamma;
	protected IloNumVar representable;

	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public XCplSRMPsolver(XSRMPmodeler input) throws IloException, IOException {
		
		super(input);
		
		cplex = new IloCplex();
		cplex.setOut(OutputUtils.getLogFileStream().getOutput());
		
		OutputUtils.lcln("\n[Solving with CPLEX " + cplex.getVersion() + "]");
		
		k = 1;
		w = new IloNumVar[nCrit];
		p = new IloIntVar[Para.MAX_NUM_REF_PTS][nCrit];
		sigma = new ArrayList<Integer>();
		
		delta = new IloIntVar[Para.MAX_NUM_REF_PTS][mElte][nCrit];
		c = new IloNumVar[Para.MAX_NUM_REF_PTS][mElte][nCrit];
		cw = new IloNumVar[Para.MAX_NUM_REF_PTS][mElte];
		s = new IloNumVar[mElte][mElte][Para.MAX_NUM_REF_PTS];
		
	}

	///////////////////
	////  METHODS  ////
	///////////////////
	
	protected void cplexEnd() {
		this.cplex.end();
	}
	
	protected void cplexClear(IloObjective objective) throws IloException {
		
		OutputUtils.logln("-------------------------------------");
		OutputUtils.log("[Waring] Clear the model... ");
		cplex.remove(objective);
		cplex.clearModel();
		OutputUtils.logln("Done!");
		OutputUtils.logln("-------------------------------------");
		
	}
	
	@SuppressWarnings("deprecation")
	protected boolean cplexSolve() throws IloException {
		
//		OutputUtils.lcln("\n[CPLEX Ver." + cplex.getVersion() + "]");
		
		cplex.setParam(IloCplex.IntParam.MIPEmphasis, Config.CPLEX_MIP_EMPHASIS_LEVEL);
		
		boolean bool = cplex.solve();
		
		OutputUtils.lcln("[CPLEX] Solution status = " + cplex.getStatus());
		OutputUtils.lcln("[CPLEX] Objective func. = " + cplex.getObjective());
		
		if (bool) OutputUtils.lcln("[CPLEX] Objective value = " + cplex.getObjValue());
		
		return bool;
	}
	
	protected void cplexUnsolvedStop() {
		if (!this.isSolved() && k > Para.MAX_NUM_REF_PTS) {
			this.cplexEnd();
			OutputUtils.lcln("[Error] Has reached the maximum of number of the reference profiles! (See Para.MAX_NUM_PROFILES)");
		}
	}
	
	protected void doIfCplexSolved(ArrayList<Integer> sigma) throws IloException {
		
		this.setSolved(true);
		
		OutputUtils.lcln("[CPLEX] There are " + cplex.getSolnPoolNsolns() + " feasible solutions in the solution pool.");
		
		XSRMPmodeler sol = new XSRMPmodeler(this.getInput());
		
		sol.setRefPtsSet(this.getResultOfRefPts().getRows());
		sol.setRefPtsValues(this.getResultOfRefPts());
		sol.setLexico(this.getResultOfLexico());
		sol.setWeights(this.getResultOfWeights());
		
		this.addSolution(sol);
		this.addObjValue(cplex.getObjValue());
		this.exportSolution(sigma);
		this.exportSolutionPool(sigma);
		
		this.setDelta(getResultOfDelta());
		this.setWeightedVar(getResultOfWeightedVar());
		this.setCoalOfWeightedVar(getResultOfCoalOfWeightedVar());
		this.setSlackVar(getResultOfSlackVar());
		
		OutputUtils.lscln("[i] The problem has been solved with " + k + " reference points.");
		
		OutputUtils.summaryln("[Help: See more details in the log.]");
		
	}
	
	protected void exportModel(ArrayList<Integer> sigma) throws IloException {
		
		String file = Config.RESOURCES_FOLDER + Config.MODEL_FOLDER + "/" + "model_"+k+"rp_"+lexicoList.indexOf(sigma)+".lp";
		
//		System.out.println(Config.RESOURCES_FOLDER + Config.MODEL_FOLDER + "/" + "model_"+k+"rp_"+lexicoList.indexOf(sigma)+".lp");
		
		cplex.exportModel(file);
		this.addModelFile(file);
		
		OutputUtils.logln("[i] The problem has been exported in " + file + ".");
	
	}
	
	protected void exportSolution(ArrayList<Integer> sigma) throws IloException {

		String file = Config.RESOURCES_FOLDER + Config.SOL_FOLDER + "/" + "solution_"+k+"rp_"+lexicoList.indexOf(sigma)+".sol";
		
		cplex.writeSolution(file);
		this.addSolutionFile(file);
		
		OutputUtils.logln("[i] The solution has been exported in " + file + ".");
		
	}
	
	protected void exportSolutionPool(ArrayList<Integer> sigma) throws IloException {
		
		String file = Config.RESOURCES_FOLDER + Config.SOL_FOLDER + "/" + "solution_"+k+"rp_"+lexicoList.indexOf(sigma)+"_pool.sol";
	
		cplex.writeSolution(file);
		this.addPoolFile(file);
		
		OutputUtils.logln("[i] All feasible solutions have been exported in " + file + ".");
		
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////

	protected Integer[][][] getResultOfDelta() throws UnknownObjectException, IloException {
		
		Integer[][][] re = new Integer[k][mElte][nCrit];
		for (int n = 0; n < nCrit; n++) {
			for (int m = 0; m < mElte; m++) {
				for (int r = 0; r < k; r++) {
					Double temp = cplex.getValue(delta[r][m][n]);
					re[r][m][n] = temp.intValue();
				}
			}
		}
		return re;
		
	}
	
	protected Integer[][][] getResultOfDelta(int solInPool) throws UnknownObjectException, IloException {
		
		Integer[][][] re = new Integer[k][mElte][nCrit];
		for (int n = 0; n < nCrit; n++) {
			for (int m = 0; m < mElte; m++) {
				for (int r = 0; r < k; r++) {
					Double temp = cplex.getValue(delta[r][m][n],solInPool);
					re[r][m][n] = temp.intValue();
				}
			}
		}
		return re;
		
	}
	
	protected Double[][][] getResultOfWeightedVar() throws UnknownObjectException, IloException {
		
		Double[][][] re = new Double[k][mElte][nCrit];
		for (int n = 0; n < nCrit; n++) {
			for (int m = 0; m < mElte; m++) {
				for (int r = 0; r < k; r++) {
					re[r][m][n] = cplex.getValue(c[r][m][n]);
				}
			}
		}
		return re;
		
	}
	
	protected Double[][][] getResultOfWeightedVar(int solInPool) throws UnknownObjectException, IloException {
		
		Double[][][] re = new Double[k][mElte][nCrit];
		for (int n = 0; n < nCrit; n++) {
			for (int m = 0; m < mElte; m++) {
				for (int r = 0; r < k; r++) {
					re[r][m][n] = cplex.getValue(c[r][m][n],solInPool);
				}
			}
		}
		return re;
		
	}
	
	protected Double[][] getResultOfCoalOfWeightedVar() throws UnknownObjectException, IloException {
		
		Double[][] re = new Double[k][mElte];
		for(int r = 0; r < mElte; r++) {
			for (int h = 0; h < k; h++) {
				re[h][r] = cplex.getValue(cw[h][r]);
			}
		}
		return re;

	}
	
	protected Double[][] getResultOfCoalOfWeightedVar(int solInPool) throws UnknownObjectException, IloException {
		
		Double[][] re = new Double[k][mElte];
		for(int r = 0; r < mElte; r++) {
			for (int h = 0; h < k; h++) {
				re[h][r] = cplex.getValue(cw[h][r],solInPool);
			}
		}
		return re;
		
	}
	
	protected Double[][][] getResultOfSlackVar() throws UnknownObjectException, IloException {
		
		Double[][][] re = new Double[mElte][mElte][k];
		Iterator<Cell<Alternative, Alternative, Double>> it = this.getInput().getRefComps().asTable().cellSet().iterator();
		while (it.hasNext()) {
			Cell<Alternative, Alternative, Double> cell = it.next();
			int row = eltList.indexOf(cell.getRowKey());
			int col = eltList.indexOf(cell.getColumnKey());
			for (int h = 0; h < k; h++) {
				re[row][col][h] = cplex.getValue(s[row][col][h]);
			}
		}
		return re;
		
	}
	
	protected Double[][][] getResultOfSlackVar(int solInPool) throws UnknownObjectException, IloException {
		
		Double[][][] re = new Double[mElte][mElte][k];
		Iterator<Cell<Alternative, Alternative, Double>> it = this.getInput().getRefComps().asTable().cellSet().iterator();
		while (it.hasNext()) {
			Cell<Alternative, Alternative, Double> cell = it.next();
			int row = eltList.indexOf(cell.getRowKey());
			int col = eltList.indexOf(cell.getColumnKey());
			for (int h = 0; h < k; h++) {
				re[row][col][h] = cplex.getValue(s[row][col][h],solInPool);
			}
		}
		return re;
		
	}
	
	public Integer getResultOfNumOfRefPts() {
		return k;
	}
	
	public ArrayList<Integer> getResultOfLexico() {
		return sigma;
	}
	
	public Map<Criterion,Double> getResultOfWeights() throws UnknownObjectException, IloException {
		
		Map<Criterion, Double> re = CollectionUtils.newMapNoNull();
		if (this.isSolved()) {
			double[] weight = cplex.getValues(w);
			for (int n = 0; n < nCrit; n++) {
				re.put(criList.get(n), weight[n]);
			}
		}
		return re;
		
	}
	
	public Map<Criterion,Double> getResultOfWeights(int solInPool) throws UnknownObjectException, IloException {
		
		Map<Criterion, Double> re = CollectionUtils.newMapNoNull();
		if (this.isSolved()) {
			double[] weight = cplex.getValues(w, solInPool);
			for (int n = 0; n < nCrit; n++) {
				re.put(criList.get(n), weight[n]);
			}
		}
		return re;
		
	}
	
	public Evaluations getResultOfRefPts() throws UnknownObjectException, IloException {
		
		Evaluations re = EvaluationsUtils.newEvaluationMatrix();
		if (this.isSolved()) {
			for (int h = 0; h < k; h++){
				for (int n = 0; n < nCrit; n++) {
					double value = cplex.getValue(p[h][n]);
					re.put(new Alternative("p"+(h+1)), criList.get(n), value);
				}
			}
		}
		return re;
		
	}
	
	public Evaluations getResultOfRefPts(int solInPool) throws UnknownObjectException, IloException {
		
		Evaluations re = EvaluationsUtils.newEvaluationMatrix();
		if (this.isSolved()) {
			for (int h = 0; h < k; h++){
				for (int n = 0; n < nCrit; n++) {
					double value = cplex.getValue(p[h][n], solInPool);
					re.put(new Alternative("p"+(h+1)), criList.get(n), value);
				}
			}
		}
		return re;
	}
	
	public Double getResultOfMinOfSlackVars() throws UnknownObjectException, IloException {
		
		if (this.isSolved()) {
			return cplex.getValue(s_min);
		}else
			return null;
		
	}
	
	public Double getResultOfMinOfSlackVars(int solInPool) throws UnknownObjectException, IloException {
		
		if (this.isSolved()) {
			return cplex.getValue(s_min, solInPool);
		}else
			return null;
		
	}
	
	public Double[] getResultOfSumOfSlacks() throws UnknownObjectException, IloException {
		
		Double[] re = new Double[numPC];
		if (this.isSolved()) {
			
			for (int i = 0; i < numPC; i++) {
				re[i] = cplex.getValue(cs[i]);
			}
			return re;
			
		}else
			return null;
		
	}
	
	public Double[] getResultOfSumOfSlacks(int solInPool) throws UnknownObjectException, IloException {
		
		Double[] re = new Double[numPC];
		if (this.isSolved()) {
			
			for (int i = 0; i < numPC; i++) {
				re[i] = cplex.getValue(cs[i], solInPool);
			}
			return re;
		}else
			return null;
		
	}
	
	public Double[] getResultOfGamma() throws UnknownObjectException, IloException {
		
		Double[] re = new Double[numPC];
		if (this.isSolved()) {
			
			for (int i = 0; i < numPC; i++) {
				re[i] = cplex.getValue(gamma[i]);
			}
			return re;
		}else
			return null;
		
	}
	
	public Double[] getResultOfGamma(int solInPool) throws UnknownObjectException, IloException {
		
		Double[] re = new Double[numPC];
		if (this.isSolved()) {
			
			for (int i = 0; i < numPC; i++) {
				re[i] = cplex.getValue(gamma[i], solInPool);
			}
			return re;
		}else
			return null;
		
	}
	
}
