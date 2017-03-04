package jrmp.srmp.main;

import jrmp.srmp.base.SRMPaggregator;
import jrmp.srmp.base.XSRMPreader;
import jrmp.srmp.solver.CplSolverJun;

public class TestDisa extends Accessible {

	public static void main(String[] args) throws Exception {
		
		prepare(args);
		
		/////////////////
		////  BEGIN  ////
		/////////////////
		
		XSRMPreader reader = new XSRMPreader();
		reader.read(false,"d");
		
//		XSRMPgenerator gene = new XSRMPgenerator();
//		gene.generate();
//		
//		writer.copy(gene);
//		writer.writeAsInputs(true);
		
		CplSolverJun jun = new CplSolverJun(reader);

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
		
//		jun.getInput().displayWeights();
////		aggr.getInput().displayWeights();
//		aggr.getOutput().displayWeights();
//		
//		jun.getInput().displayRefPts();
////		aggr.getInput().displayRefPts();
//		aggr.getOutput().displayRefPts();
//		
//		jun.getInput().displayLexico();
////		aggr.getInput().displayLexico();
//		aggr.getOutput().displayLexico();
//		
////		aggr.getInput().displayPairComps();
////		aggr.getOutput().displayPairComps();
//		
//		aggr.getInput().displayGlobalRanking();
//		aggr.getOutput().displayGlobalRanking();

		System.out.println("[i] The ratio of representables is " + aggr.getRatioOfRepresentables()*100 + "%.");
		
		///////////////
		////  END  ////
		///////////////
		
		end();
		
	}

}
