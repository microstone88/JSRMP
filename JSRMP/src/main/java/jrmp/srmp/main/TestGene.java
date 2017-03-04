/**
 * 
 */
package jrmp.srmp.main;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.exc.InvalidInputException;

import jrmp.srmp.base.XSRMPgenerator;

/**
 * @author micro
 *
 */
public class TestGene extends Accessible {

	public static void main(String[] args) throws IOException, XmlException, InvalidInputException {
		
		prepare(args);
		
		/////////////////
		////  BEGIN  ////
		/////////////////
		
		XSRMPgenerator gene = new XSRMPgenerator();
		gene.generate();

		writer.copy(gene);
		writer.writeAsInputs(true);
		
//		SRMPaggregator aggr = new SRMPaggregator(gene);
//		aggr.execute(false);
		
//		writer.copy(aggr.getOutput());
//		writer.writeRanking(false);
		
		///////////////
		////  END  ////
		///////////////
		
		end();
		
	}

}
