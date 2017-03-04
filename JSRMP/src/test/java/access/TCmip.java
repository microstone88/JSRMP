/**
 * 
 */
package access;

import ilog.concert.IloException;

import java.io.IOException;

import jrmp.srmp.base.SRMPaggregator;
import jrmp.srmp.base.XSRMPgenerator;
import jrmp.srmp.main.Accessible;
import jrmp.srmp.solver.CplSolverJun;

/**
 * @author micro
 *
 */
public class TCmip extends Accessible {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IloException 
	 */
	public static void main(String[] args) throws IOException, IloException {
		
		prepare(args);
		
		/////////////////
		////  BEGIN  ////
		/////////////////
		
		XSRMPgenerator gene = new XSRMPgenerator();
		gene.generate();
		
//		writer.copy(gene);
//		writer.writeAsInputs(false);
		
		CplSolverJun jun = new CplSolverJun(gene);
		
		if (jun.getInput().isReadyForDisag()) {
			//CplSolverJun jun = new CplSolverJun(gene);
			jun.solve(true);
		}else{
			System.out.println("Disaggrgation Wrong!");
		}
		
		SRMPaggregator aggr = new SRMPaggregator(jun.getOutput());
		
		if (aggr.getInput().isReadyForAggr()) {
			aggr.execute(false);
		}else{
			System.out.println("Aggrgation Wrong!");
		}
		
		System.out.println("[i] The ratio of representables is " + aggr.getRatioOfRepresentables()*100 + "%.");
		
		///////////////
		////  END  ////
		///////////////
		
		end();
		
	}

}
