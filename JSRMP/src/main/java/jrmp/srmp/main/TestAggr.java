package jrmp.srmp.main;

import jrmp.srmp.base.SRMPaggregator;
import jrmp.srmp.settings.Config;

public class TestAggr extends Accessible {

	public static void main(String[] args) throws Exception {
		
		Config.RESOURCES_FOLDER = "data/";
		
		prepare(args);
		
		/////////////////
		////  BEGIN  ////
		/////////////////
		
		reader.read(false,"a");
		
		SRMPaggregator aggr = new SRMPaggregator(reader);
		aggr.execute(true);
		
		writer.copy(aggr.getOutput());
		writer.writeRanking(true);
		
		///////////////
		////  END  ////
		///////////////
		
		end();

	}

}
