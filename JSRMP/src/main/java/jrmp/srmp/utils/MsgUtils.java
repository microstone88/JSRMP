/**
 * 
 */
package jrmp.srmp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jrmp.srmp.extension.PreDef;
import jrmp.srmp.settings.Config;

import org.decisiondeck.jmcda.persist.xmcda2.XMCDAVarious;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMethodMessages;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAErrorsManager;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;

import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

/**
 * @author micro
 *
 */
public class MsgUtils {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private String filePath;
	private String fileName;
	
	private File file;
	
	private static ArrayList<String> msgList;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public MsgUtils() {
		this.setFilePath(Config.XML_FOLDER);
		this.setFileName("/" + PreDef.FileName.messages.toString() + ".xml");
		this.file = new File(Config.RESOURCES_FOLDER + this.getFilePath() + this.getFileName());
		msgList = new ArrayList<>();
	}
	
	//////////////////////////
	////  STATIC METHODS  ////
	//////////////////////////
	
	public static void addMethodMessage(String msg) {
		msgList.add(msg);
	}
	
	public static void addMethodMessage(int index, String msg) {
		msgList.add(index, msg);
	}
	

	///////////////////
	////  METHODS  ////
	///////////////////

	private void exportMessages(Collection<String> msg) throws  IOException {
		OutputSupplier<FileOutputStream> outputStream = Files.newOutputStreamSupplier(file);
		XMCDAVarious writeVarious = new XMCDAVarious(new XMCDAErrorsManager());
		XMethodMessages xMsg = writeVarious.writeMessages(msg);
		XMCDAWriteUtils.write(xMsg, outputStream, true);
	}
	
	public void clearMethodMessage() {
		msgList.clear();
	}
	
	public void end() throws IOException {
		this.exportMessages(msgList);
	}
	
	///////////////////////////
	//// SETTERS & GETTERS ////
	///////////////////////////

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the msgSet
	 */
	public ArrayList<String> getMsgSet() {
		return msgList;
	}

}
