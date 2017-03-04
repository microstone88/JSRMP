/**
 * 
 */
package jrmp.srmp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

import jrmp.srmp.settings.Config;

/**
 * @author micro
 *
 */
public class OutputUtils {
	
	public static DecimalFormat tenmi = new DecimalFormat("0.0000");
	public static DecimalFormat milli = new DecimalFormat("0.000");
	public static DecimalFormat centi = new DecimalFormat("0.00");
	public static DecimalFormat deci = new DecimalFormat("0.0");
	public static DecimalFormat integ = new DecimalFormat("0");
	
	public static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	
	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private String summaryPath;
	private String logFilePath;
	
	private String summaryName;
	private String logFileName;
	
	private File summary;
	private File logFile;
	
	private static OutputSupplier<FileOutputStream> summaryStream;
	private static OutputSupplier<FileOutputStream> logFileStream;
	
	private static PrintWriter summaryPrinter;
	private static PrintWriter logFilePrinter;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public OutputUtils() throws IOException {
		
		this.setLogFilePath(Config.OUTPUT_FOLDER);
		this.setSummaryPath(Config.OUTPUT_FOLDER);
		
		this.setLogFileName("/log_" + Config.DEFAULT_EXP_NAME + ".log");
		this.setSummaryName("/summary_" + Config.DEFAULT_EXP_NAME + ".txt");
		
		
		this.prepare();
		
		summary = new File(Config.RESOURCES_FOLDER + summaryPath + summaryName);
		logFile = new File(Config.RESOURCES_FOLDER + logFilePath + logFileName);
		
		summaryStream = Files.newOutputStreamSupplier(summary, true);
		logFileStream = Files.newOutputStreamSupplier(logFile, true);
		
		summaryPrinter = new PrintWriter(summaryStream.getOutput());
		logFilePrinter = new PrintWriter(logFileStream.getOutput());
		
		this.writeSummaryHeader();
		this.writeLogFileHeader();
		
	}
	
	///////////////////
	////  METHODS  ////
	///////////////////
	
	private void prepare() {
		
//		console("[i] Preparing folders...");
		
		File resourceFolder = new File(Config.RESOURCES_FOLDER);
		if (!resourceFolder.exists()) {resourceFolder.mkdir();console(".");}
		
		File expFolder = new File(Config.RESOURCES_FOLDER + "/" + Config.DEFAULT_EXP_NAME);
		if (!expFolder.exists()) {expFolder.mkdir();console(".");}
		
		File inputFolder = new File(Config.RESOURCES_FOLDER + "/" + Config.INPUT_FOLDER);
		if (!inputFolder.exists()) {inputFolder.mkdir();console(".");}
		
		File outputFolder = new File(Config.RESOURCES_FOLDER + "/" + Config.OUTPUT_FOLDER);
		if (!outputFolder.exists()) {outputFolder.mkdir();console(".");}
		
		File modelsFolder = new File(Config.RESOURCES_FOLDER + "/" + Config.MODEL_FOLDER);
		if (!modelsFolder.exists()) {modelsFolder.mkdir();console(".");}
		
		File solsFolder = new File(Config.RESOURCES_FOLDER + "/" + Config.SOL_FOLDER);
		if (!solsFolder.exists()) {solsFolder.mkdir();console(".");}
		
		File xmlsFolder = new File(Config.RESOURCES_FOLDER + "/" + Config.XML_FOLDER);
		if (!xmlsFolder.exists()) {xmlsFolder.mkdir();console(".");}
		
		consoleln("");
		
//		consoleln(" Completed!");
		
	}
	
	private void writeSummaryHeader() throws IOException {
		
		OutputSupplier<FileOutputStream> tempSupplier = Files.newOutputStreamSupplier(summary, false);
		PrintWriter headWriter = new PrintWriter(tempSupplier.getOutput());
		
		headWriter.println("\n---------- General informations ----------");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		headWriter.println("Date	:" + df.format(new Date()));
		Properties prop = new Properties(System.getProperties());
		headWriter.println("Owner	:" + prop.getProperty("user.name").toUpperCase());
		headWriter.print("O.S.	:" +prop.getProperty("os.arch") + " " + prop.getProperty("os.name").toUpperCase() + " platform");
		headWriter.println(" (version " + prop.getProperty("os.version") + ")");
		headWriter.println("Ref	:" + Config.DEFAULT_EXP_NAME);
		headWriter.println("Exp.ID	:" + Config.DEFAULT_EXP_ID);
		headWriter.println("\n---------- SUMMARY REPORT ----------\n");
		headWriter.close();
		
	}
	
	private void writeLogFileHeader() throws IOException {
		
		OutputSupplier<FileOutputStream> tempSupplier = Files.newOutputStreamSupplier(logFile, false);
		PrintWriter headWriter = new PrintWriter(tempSupplier.getOutput());
		
		headWriter.println("\n---------- General informations ----------");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		headWriter.println("Date	:" + df.format(new Date()));
		Properties prop = new Properties(System.getProperties());
		headWriter.println("Owner	:" + prop.getProperty("user.name").toUpperCase());
		headWriter.print("O.S.	:" +prop.getProperty("os.arch") + " " + prop.getProperty("os.name").toUpperCase() + " platform");
		headWriter.println(" (version " + prop.getProperty("os.version") + ")");
		headWriter.println("Ref	:" + Config.DEFAULT_EXP_NAME);
		headWriter.println("Exp.ID	:" + Config.DEFAULT_EXP_ID);
		headWriter.println("\n---------- LOG INFORMATION ----------\n");
		headWriter.close();

	}
	
	public void end() throws IOException {
		lsln("\n---------- END OF OUTPUTS ----------");
		summaryPrinter.close();
		logFilePrinter.close();
		System.in.close();
	}
	
	public static void summary(Object content) {
		if (content != null) {
			summaryPrinter.print(content);
			summaryPrinter.flush();
		}else{
			summaryPrinter.print("-");
			summaryPrinter.flush();
		}
	}
	
	public static void summaryln(Object content) {
		if (content != null) {
			summaryPrinter.println(content);
			summaryPrinter.flush();
		}else{
			summaryPrinter.println("-");
			summaryPrinter.flush();
		}
	}
	
	public static void log(Object content) {
		if (content != null) {
			logFilePrinter.print(content);
			logFilePrinter.flush();
		}else{
			logFilePrinter.print("-");
			logFilePrinter.flush();
		}
	}
	
	public static void logln(Object content) {
		if (content != null) {
			logFilePrinter.println(content);
			logFilePrinter.flush();
		}else{
			logFilePrinter.println("-");
			logFilePrinter.flush();
		}
	}
	
	public static void console(Object content) {
		if (content != null) {
			System.out.print(content);
		}else{
			System.out.print("null");
		}
	}
	
	public static void consoleln(Object content) {
		if (content != null) {
			System.out.println(content);
		}else{
			System.out.println("null");
		}
	}
	
	public static void ls(Object content) {
		log(content);
		summary(content);
	}
	
	public static void lsln(Object content) {
		logln(content);
		summaryln(content);
	}
	
	public static void lc(Object content) {
		log(content);
		console(content);
		
		//TODO to decide to be removed or not
		MsgUtils.addMethodMessage(content.toString());
	}
	
	public static void lcln(Object content) {
		logln(content);
		consoleln(content);
		
		//TODO to decide to be removed or not
		MsgUtils.addMethodMessage(content.toString());
	}
	
	public static void lsc(Object content) {
		log(content);
		summary(content);
		console(content);
	}
	
	public static void lscln(Object content) {
		logln(content);
		summaryln(content);
		consoleln(content);
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////

	/**
	 * @return the summaryPath
	 */
	public String getSummaryPath() {
		return summaryPath;
	}

	/**
	 * @return the logFilePath
	 */
	public String getLogFilePath() {
		return logFilePath;
	}

	/**
	 * @return the summaryName
	 */
	public String getSummaryName() {
		return summaryName;
	}

	/**
	 * @return the logFileName
	 */
	public String getLogFileName() {
		return logFileName;
	}
	
	/**
	 * @return the summaryStream
	 */
	public static OutputSupplier<FileOutputStream> getSummaryStream() {
		return summaryStream;
	}

	/**
	 * @return the logFileStream
	 */
	public static OutputSupplier<FileOutputStream> getLogFileStream() {
		return logFileStream;
	}

	/**
	 * @return the summarypath
	 */
	public String getSummarypath() {
		return summaryPath;
	}

	/**
	 * @return the logfilepath
	 */
	public String getLogfilepath() {
		return logFilePath;
	}

	/**
	 * @return the summaryname
	 */
	public String getSummaryname() {
		return summaryName;
	}

	/**
	 * @return the logfilename
	 */
	public String getLogfilename() {
		return logFileName;
	}

	/**
	 * @param summaryPath the summaryPath to set
	 */
	public void setSummaryPath(String summaryPath) {
		this.summaryPath = summaryPath;
	}

	/**
	 * @param logFilePath the logFilePath to set
	 */
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	/**
	 * @param summaryName the summaryName to set
	 */
	public void setSummaryName(String summaryName) {
		this.summaryName = summaryName;
	}

	/**
	 * @param logFileName the logFileName to set
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
}
