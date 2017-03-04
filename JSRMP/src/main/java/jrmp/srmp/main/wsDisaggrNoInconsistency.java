package jrmp.srmp.main;

import ilog.concert.IloException;

import java.io.IOException;

import jrmp.srmp.settings.Config;
import jrmp.srmp.solver.CplSolverJun;
import jrmp.srmp.utils.MsgUtils;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.exc.InvalidInputException;

/**
 * Diviz: S-RMP disaggregation
 * @author micro
 * @version 1.3
 * @since 2013-12-03
 */
public class wsDisaggrNoInconsistency extends Accessible {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws IOException 
	 * @throws InvalidInputException 
	 * @throws XmlException 
	 * @throws IloException 
	 */
	public static void main(String[] args) throws Exception {
		
		Config.RESOURCES_FOLDER = ".";
		
		try {
			
			prepare(args);
			
			/////////////////
			////  BEGIN  ////
			/////////////////
			
			reader.read(false, "d");
	
			if (reader.isReadyForDisag()) {
	
				CplSolverJun jun = new CplSolverJun(reader);
				jun.solve(false);
				
				writer.copy(jun.getOutput());
				writer.writeModel(false);
				
				MsgUtils.addMethodMessage(0, "OK");
				
			}else{
				MsgUtils.addMethodMessage(0, "Error: Incomplete input data");
			}
	
			///////////////
			////  END  ////
			///////////////
			
			end();
		
		} catch (IOException | XmlException | InvalidInputException | IloException e) {
			MsgUtils.addMethodMessage(e.getMessage());
		}

	}

}
