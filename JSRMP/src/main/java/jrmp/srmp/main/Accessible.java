/**
 * 
 */
package jrmp.srmp.main;

import java.io.IOException;
import java.util.Date;

import jrmp.srmp.base.XSRMPreader;
import jrmp.srmp.base.XSRMPwriter;
import jrmp.srmp.settings.Config;
import jrmp.srmp.utils.MsgUtils;
import jrmp.srmp.utils.OutputUtils;

/**
 * @author micro
 *
 */
public class Accessible {
	
	protected static XSRMPreader reader;
	
	protected static XSRMPwriter writer;
	
	protected static OutputUtils out;
	
	protected static MsgUtils msg;

	protected static void prepare(String[] args) throws IOException {
		
		msg = new MsgUtils();
		out = new OutputUtils();
		
		Date now = new Date(System.currentTimeMillis());
		OutputUtils.lscln("[START @ " + now + "]\n");
		
		reader = new XSRMPreader();
		writer = new XSRMPwriter();
		
		for (int i = 0; i < args.length; i++) {
			String str = args[i];
			
			if (str.equalsIgnoreCase("-e")) {
				Config.DEFAULT_EXP_NAME = args[i+1];
				Config.DEFAULT_EXP_ID = args[i+1] + "_" + System.currentTimeMillis();
				reader.setInputPath("" + args[i+1] + "/inputs");
				writer.setOutputPath("" + args[i+1] + "/outputs");
			}
			
			if (str.equalsIgnoreCase("-i")) {
				reader.setInputPath(args[i+1]);
			}
			
			if (str.equalsIgnoreCase("-o")) {
				writer.setOutputPath(args[i+1]);
			}
			
			if (str.equalsIgnoreCase("-test")) {
				// TODO test
				
			}
			
			if (str.equalsIgnoreCase("-f")) {
				Config.OVERWRITE_INTERACT_OPTION = false;
			}
			
		}
		
		OutputUtils.lcln("[i] Current resource path is " + Config.RESOURCES_FOLDER);
		OutputUtils.lcln("[i] Current input path is " + reader.getInputPath());
		OutputUtils.lcln("[i] Current output path is " + writer.getOutputPath());
		
	}
	
	protected static void end() throws IOException {

		Date now = new Date(System.currentTimeMillis());
		OutputUtils.lscln("\n[END @ " + now + "]");
		
		out.end(); 
		msg.end();
	}
	
	
}
