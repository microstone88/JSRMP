/**
 * 
 */
package access;

import jrmp.srmp.base.SRMPaggregator;
import jrmp.srmp.base.XSRMPreader;
import jrmp.srmp.base.XSRMPwriter;
import jrmp.srmp.main.Accessible;
import jrmp.srmp.solver.CplSolverJinyanS;
import jrmp.srmp.utils.OutputUtils;

/**
 * @author micro
 *
 */
public class Malaga extends Accessible {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		prepare(args);
		
		/////////////////
		////  BEGIN  ////
		/////////////////
		
		//** GENERATION **//
		
//		XSRMPgenerator gene = new XSRMPgenerator();
//		gene.generate();
//		
//		writer.copy(gene);
//		writer.writeAsInputs(true);
		
		//** LOADING **//
		
		XSRMPreader reader = new XSRMPreader();
		reader.read(false);
		
		reader.displayRefComps();
		reader.perturb(0);
//		Integer index[] = {16};
//		reader.perturb(index);
		reader.displayRefComps();
		
		//** SOLVING **//
		
//		CplSolverJinyan jinyan = new CplSolverJinyan(reader);
		CplSolverJinyanS jinyan = new CplSolverJinyanS(reader);

		
		if (jinyan.getInput().isReadyForDisag()) {
			jinyan.solve(false);
		}else{
			System.out.println("[ERROR] Disaggrgation Error!");
		}
		
		if (jinyan.isSolved()) {
		
			OutputUtils.lcln(jinyan.getGammaStatus());
			OutputUtils.lcln(jinyan.getSlackStatus());
		
			//** AGGREGATION **//
		
			SRMPaggregator aggr = new SRMPaggregator(jinyan.getOutput());
			aggr.setNeedToRebuid(true);
			
			if (aggr.getInput().isReadyForAggr()) {
				aggr.execute(false);
			}else{
				System.out.println("[ERROR] Aggregation Error!");
			}
			
			aggr.getOutput().displayRefComps();
		
			OutputUtils.lscln("[i] The ratio of representables is " 
					+ OutputUtils.centi.format(aggr.getRatioOfRepresentables()*100)
					+ "% (" + aggr.getNumOfIndifferences() + " indiff.).");
			
			//** EXPORTATION **//
		
			XSRMPwriter writer = new XSRMPwriter(jinyan.getOutput());
			writer.writeModel(true);
		
		}
		///////////////
		////  END  ////
		///////////////
		
		end();
		
	}

}
