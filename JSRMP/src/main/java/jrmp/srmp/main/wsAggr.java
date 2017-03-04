package jrmp.srmp.main;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.exc.InvalidInputException;

import jrmp.srmp.base.SRMPaggregator;
import jrmp.srmp.settings.Config;
import jrmp.srmp.utils.MsgUtils;

/**
 * Diviz: S-RMP Aggregation
 * @author micro
 * @version 1.3
 * @since 2013-12-03
 */
public class wsAggr extends Accessible {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws IOException 
	 * @throws InvalidInputException 
	 * @throws XmlException 
	 */
	public static void main(String[] args) throws Exception {
		
		Config.RESOURCES_FOLDER = ".";
		
		try {
		
			prepare(args);
			
			/////////////////
			////  BEGIN  ////
			/////////////////
			
			reader.read(false, "a");
	
			if (reader.isReadyForAggr()) {
				
				SRMPaggregator aggr = new SRMPaggregator(reader);
				aggr.execute(false);
				
				writer.copy(aggr.getOutput());
				writer.writePreorder(true);
				
				MsgUtils.addMethodMessage(0, "OK");
				
			}else{
				MsgUtils.addMethodMessage(0, "Error: Incomplete input data");
			}
			
			///////////////
			////  END  ////
			///////////////
	
			end();
			
		} catch (IOException | XmlException | InvalidInputException e) {
			MsgUtils.addMethodMessage(e.getMessage());
		}

	}

}
