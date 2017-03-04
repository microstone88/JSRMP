package access;

import ilog.concert.IloException;

import java.io.IOException;

import jrmp.srmp.base.SRMPaggregator;
import jrmp.srmp.base.XSRMPgenerator;
import jrmp.srmp.main.Accessible;
import jrmp.srmp.solver.CplSolverJun;
import jrmp.srmp.utils.OutputUtils;

public class exp extends Accessible {

	public static void main(String[] args) {
		
		try {
			
			prepare(args);
			
			/////////////////
			////  BEGIN  ////
			/////////////////
			
			XSRMPgenerator gene = new XSRMPgenerator();
			gene.generate(true);
			
			CplSolverJun jun = new CplSolverJun(gene);
			
			if (jun.getInput().isReadyForDisag()) {
				jun.solve(false);
			}else{
				OutputUtils.lcln("[Error]");
			}
			
			SRMPaggregator aggr = new SRMPaggregator(jun.getOutput());
			aggr.execute(false);
			
			OutputUtils.lscln("[i] The ratio of representables is " + aggr.getRatioOfRepresentables()*100 + "%.");
			
			///////////////
			////  END  ////
			///////////////
			
			end();
			
		} catch (IOException e) {
			e.printStackTrace();OutputUtils.logln(e);
		} catch (IloException e) {
			e.printStackTrace();OutputUtils.logln(e);
		}
		
	}

}
