/**
 * 
 */
package jrmp.srmp.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.relation.graph.Preorder;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDAAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDAAlternativesMatrix;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDACriteria;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDAEvaluations;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeOnCriteriaPerformances;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativesComparisons;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativesValues;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteria;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteriaValues;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterionValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPerformanceTable;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;

import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

import jrmp.srmp.extension.PreDef;
import jrmp.srmp.settings.Config;
import jrmp.srmp.utils.OutputUtils;
import jrmp.srmp.utils.RankingUtils;

/**
 * @author micro
 * 
 */
public class XSRMPwriter extends XSRMPmodeler {

	// ////////////////////
	// // ATTRIBUTES ////
	// ////////////////////

	private String filePath;

	// /////////////////////
	// // CONSTRUCTOR ////
	// /////////////////////

	public XSRMPwriter() {
		this.setStatus(XSRMPmodeler.Status.initialized);
		this.setOutputPath(Config.OUTPUT_FOLDER);
	}

	public XSRMPwriter(XSRMPmodeler model) {
		super(model);
		this.setStatus(XSRMPmodeler.Status.overwritten);
		this.setOutputPath(Config.OUTPUT_FOLDER);
	}

	// /////////////////
	// // METHODS ////
	// /////////////////

	public Integer write() throws IOException {

		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		int re = 0;

		re += this.exportAlternatives(false);
		re += this.exportCriteria(false);
		re += this.exportEvaluations(false);
		re += this.exportRefComps(false);
		re += this.exportWeights(false);
		re += this.exportRefPts(false);
		re += this.exportLexicography(false);
		re += this.exportRanking(false);


		OutputUtils.lscln("[i] " + re + "/8 files have been exported.");
		OutputUtils.lsln("[i] Exporting completed!");

		return re;
	}

	public Integer write(boolean display) throws IOException {

		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		int re = 0;

		re += this.exportAlternatives(display);
		re += this.exportCriteria(display);
		re += this.exportEvaluations(display);
		re += this.exportRefComps(display);
		re += this.exportWeights(display);
		re += this.exportRefPts(display);
		re += this.exportLexicography(display);
		re += this.exportRanking(display);

		OutputUtils.lscln("[i] " + re + "/8 files have been exported.");
		OutputUtils.lsln("[i] Exporting completed!");

		return re;
	}

	public Integer write(String path) throws IOException {

		this.setOutputPath(path);

		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		int re = 0;

		re += this.exportAlternatives(false);
		re += this.exportCriteria(false);
		re += this.exportEvaluations(false);
		re += this.exportRefComps(false);
		re += this.exportWeights(false);
		re += this.exportRefPts(false);
		re += this.exportLexicography(false);
		re += this.exportRanking(false);

		OutputUtils.lscln("[i] " + re + "/8 files have been exported.");
		OutputUtils.lsln("[i] Exporting completed!");

		return re;
	}

	public void writeModel(boolean display) throws IOException {

		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		int re = 0;
		
		re += this.exportRefPts(display);
		re += this.exportLexicography(display);
		re += this.exportWeights(display);


		OutputUtils.lscln("[i] " + re + "/3 files have been exported.");
		OutputUtils.lscln("[i] Exporting completed!");

	}

	public void writeRanking(boolean display) throws IOException {

		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		this.exportRanking(display);

		OutputUtils.lscln("[i] Only ranking.xml has been exported.");
		OutputUtils.lscln("[i] Exporting completed!");

	}
	
	public void writePreorder(boolean display) throws IOException {
		
		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		this.exportPreorder(display);

		OutputUtils.lscln("[i] Only preorder.xml has been exported.");
		OutputUtils.lscln("[i] Exporting completed!");

	}
	
	public Integer writeAsInputs(boolean displayRanking) throws IOException {

		String ini = this.getFilePath();

		this.setFilePath("/" + Config.DEFAULT_EXP_NAME + "/inputs");

		OutputUtils.consoleln("\n[Exporting Process]");
		OutputUtils.lsln("\n *** Files Exporting *** \n");

		int re = 0;
		
		long start = System.currentTimeMillis();

		re += this.exportAlternatives(false);
		re += this.exportCriteria(false);
		re += this.exportEvaluations(false);
		re += this.exportRefComps(false);
		re += this.exportWeights(false);
		re += this.exportRefPts(false);
		re += this.exportLexicography(false);
		re += this.exportRanking(displayRanking);
		
		long end = System.currentTimeMillis();
		long duration = end - start;

		OutputUtils.lscln("[i] " + re + "/8 files have been exported."
				+ " (Elapsed time: " + duration + " ms)");

		this.setFilePath(ini);

		return re;
	}

	/**
	 * ********************************************** ****************
	 * ALTERNATIVES ****************
	 * **********************************************
	 */

	protected Integer exportAlternatives(boolean display) throws IOException {

		String fileName = "/" + PreDef.FileName.alternatives.toString()
				+ ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportAlternatives(display);
					return 0;
				}

			}
		}

		if (this.getAlteSet() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XMCDAAlternatives writerAlternative = new XMCDAAlternatives();
			writerAlternative.setNames(this.getAlteNames());
			XAlternatives xAlternatives = writerAlternative.writeAlternatives(
					this.getAlteSet(), null);
			XMCDAWriteUtils.write(xAlternatives, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display)
				this.displayAlternatives();
			else
				this.outputAlternatives();
			return 1;
		} else {
			OutputUtils.lcln("Nothing to export.");
			return 0;
		}
	}

	/**
	 * ********************************************** ******************
	 * CRITERIA ******************
	 * **********************************************
	 */

	protected Integer exportCriteria(boolean display) throws IOException {

		String fileName = "/" + PreDef.FileName.criteria.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportCriteria(display);
					return 0;
				}

			}
		}

		if (this.getCritSet() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XMCDACriteria writerCriteria = new XMCDACriteria();
			writerCriteria.setScales(this.getCritScales());
			writerCriteria.setNames(this.getCritNames());
			XCriteria xCriteria = writerCriteria.write(this.getCritSet());
			XMCDAWriteUtils.write(xCriteria, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display)
				this.displayCriteria();
			else
				this.outputCriteria();
			return 1;
		} else {
			OutputUtils.lcln("Nothing to export.");
			return 0;
		}
	}

	/**
	 * ********************************************** ************** CRITERIA
	 * WEIGHTS ************** **********************************************
	 */

	protected Integer exportWeights(boolean display) throws IOException {

		String fileName = "/" + PreDef.FileName.weights.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportWeights(display);
					return 0;
				}

			}
		}

		if (this.getWeights() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XCriteriaValues xCriteriaVals = XMCDA.Factory.newInstance()
					.addNewCriteriaValues();
			Iterator<Criterion> it = this.getWeights().keySet().iterator();
			while (it.hasNext()) {
				Criterion crit = it.next();
				XCriterionValue xCritVal = xCriteriaVals.addNewCriterionValue();
				xCritVal.setCriterionID(crit.getId());
				XValue xVal = xCritVal.addNewValue();
				xVal.setReal(new Float(this.getWeights().get(crit)));
			}
			XMCDAWriteUtils.write(xCriteriaVals, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display)
				this.displayWeights();
			else
				this.outputWeights();
			return 1;
		} else {
			OutputUtils.lcln("Nothing to export.");
			return 0;
		}
	}

	/**
	 * ********************************************** ****************
	 * EVALUATIONS ****************
	 * **********************************************
	 */

	protected Integer exportEvaluations(boolean display) throws IOException {

		String fileName = "/" + PreDef.FileName.performanceTable.toString()
				+ ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportEvaluations(display);
					return 0;
				}

			}
		}

		if (this.getAlteValues() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XMCDAEvaluations writerEva = new XMCDAEvaluations();
			XPerformanceTable xEva = writerEva.write(this.getAlteValues());
			XMCDAWriteUtils.write(xEva, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display)
				this.displayEvaluations();
			else
				this.outputEvaluations();
			return 1;
		} else {
			OutputUtils.lcln("Nothing to export.");
			return 0;
		}
	}

	/**
	 * ************************************************* ****************** REF.
	 * POINTS ******************
	 * *************************************************
	 */

	protected Integer exportRefPts(boolean display) throws IOException {

		String fileName = "/" + PreDef.FileName.profileConfigs.toString()
				+ ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportRefPts(display);
					return 0;
				}

			}
		}
		if (this.getRefPtsValues() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XMCDAEvaluations writerPConfigs = new XMCDAEvaluations();
			XPerformanceTable xPConfigs = writerPConfigs.write(this
					.getRefPtsValues());
			List<XAlternativeOnCriteriaPerformances> xPList = xPConfigs
					.getAlternativePerformancesList();
			for (int i = 0; i < xPList.size(); i++) {
				XAlternativeOnCriteriaPerformances xProfile = xPList.get(i);
				xProfile.setMcdaConcept("Ref. profile");
//				xProfile.setId(this.getLexico().get(i).toString());
			}
			XMCDAWriteUtils.write(xPConfigs, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display) {
				this.displayRefPts();
				this.displayLexico();
			} else {
				this.outputRefPts();
				this.outputLexico();
			}
			return 1;
		} else {
			OutputUtils.lcln("Nothing to export.");
			return 0;
		}
	}

	/**
	 * ********************************************** ********** ALTERNATIVE
	 * COMPARISONS *********** **********************************************
	 */

	protected Integer exportRefComps(boolean display) throws IOException {

		String fileName = "/"
				+ PreDef.FileName.alternativesComparisons.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportRefComps(display);
					return 0;
				}

			}
		}

		if (this.getRefComps() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XMCDAAlternativesMatrix writerAltesMatrix = new XMCDAAlternativesMatrix();
			XAlternativesComparisons xAltesComps = writerAltesMatrix.write(this
					.getRefComps());
			xAltesComps.setComparisonType("Preference");
			XMCDAWriteUtils.write(xAltesComps, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display) {
				this.displayRefComps();
				// this.displayRefRanking();
			} else {
				this.outputRefComps();
				// this.outputRefRanking();
			}
			return 1;
		} else {
			OutputUtils.lcln("Nothing to export.");
			return 0;
		}
	}

	protected Integer exportRanking(boolean display) throws IOException {

		String fileName = "/" + PreDef.FileName.ranking.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportRanking(display);
					return 0;
				}

			}
		}

		if (this.getAllComps() != null) {
			OutputSupplier<FileOutputStream> outputStream = Files
					.newOutputStreamSupplier(file);
			XMCDAAlternativesMatrix writerAltesMatrix = new XMCDAAlternativesMatrix();
			XAlternativesComparisons xAltesComps = writerAltesMatrix.write(this
					.getAllComps());
			xAltesComps.setComparisonType("Preference");
			XMCDAWriteUtils.write(xAltesComps, outputStream, true);
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display) {
				this.displayGlobalRanking();
			} else {
				this.outputGlobalRanking();
			}
			return 1;
		} else
			OutputUtils.lcln("Nothing to export.");
		return 0;

	}
	
	protected Integer exportPreorder(boolean display) throws IOException {
		
		String fileName = "/" + PreDef.FileName.preorder.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportPreorder(display);
					return 0;
				}

			}
		}

		if (this.getAllComps() != null) {

			Preorder<Alternative> preorder = RankingUtils.getPreorder(this.getAllComps());
			
			OutputSupplier<FileOutputStream> outputStream = Files.newOutputStreamSupplier(file);
			XAlternativesValues xAlternativesVals = XMCDA.Factory.newInstance().addNewAlternativesValues();
			Iterator<Alternative> it = preorder.iterator();
			while (it.hasNext()) {
				Alternative alte = it.next();
				XAlternativeValue xAlteVal = xAlternativesVals.addNewAlternativeValue();
				xAlteVal.setAlternativeID(alte.getId());
				XValue xVal = xAlteVal.addNewValue();
				xVal.setInteger(preorder.getRank(alte));
			}
			XMCDAWriteUtils.write(xAlternativesVals, outputStream, true);
			
			
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display) {
				this.displayGlobalRanking();
			} else {
				this.outputGlobalRanking();
			}
			return 1;
		} else
			OutputUtils.lcln("Nothing to export.");
		return 0;
		
	}
	
	protected Integer exportPreorder(Preorder<Alternative> preorder, boolean display) throws IOException {
		
		String fileName = "/" + PreDef.FileName.preorder.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportPreorder(display);
					return 0;
				}

			}
		}

		if (this.getAllComps() != null) {

			OutputSupplier<FileOutputStream> outputStream = Files.newOutputStreamSupplier(file);
			XAlternativesValues xAlternativesVals = XMCDA.Factory.newInstance().addNewAlternativesValues();
			Iterator<Alternative> it = preorder.iterator();
			while (it.hasNext()) {
				Alternative alte = it.next();
				XAlternativeValue xAlteVal = xAlternativesVals.addNewAlternativeValue();
				xAlteVal.setAlternativeID(alte.getId());
				XValue xVal = xAlteVal.addNewValue();
				xVal.setInteger(preorder.getRank(alte));
			}
			XMCDAWriteUtils.write(xAlternativesVals, outputStream, true);
			
			
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display) {
				this.displayGlobalRanking();
			} else {
				this.outputGlobalRanking();
			}
			return 1;
		} else
			OutputUtils.lcln("Nothing to export.");
		return 0;
		
	}
	
	protected Integer exportLexicography(boolean display) throws IOException {
		
		String fileName = "/" + PreDef.FileName.lexicography.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);

		OutputUtils.lc("[i] Exporting " + filePath + fileName + "...");

		if (Config.OVERWRITE_INTERACT_OPTION) {
			if (file.exists()) {

				System.in.skip(System.in.available());
				OutputUtils.lc("File exists. Overwrite? (y/n):");
				char re = (char) System.in.read();
				OutputUtils.logln(re);

				if (re == 'y') {
					OutputUtils.lc("[i] Overwriting " + filePath + fileName
							+ "...");
				} else if (re == 'n') {
					OutputUtils.lcln("[i] Exporting " + filePath + fileName
							+ "... Aborted.");
					return 0;
				} else {
					exportLexicography(display);
					return 0;
				}

			}
		}
		
		if (this.getLexicography() != null) {
			
			Preorder<Alternative> preorder = this.getLexicography();
			
			OutputSupplier<FileOutputStream> outputStream = Files.newOutputStreamSupplier(file);
			XAlternativesValues xAlternativesVals = XMCDA.Factory.newInstance().addNewAlternativesValues();
			Iterator<Alternative> it = preorder.iterator();
			while (it.hasNext()) {
				Alternative alte = it.next();
				XAlternativeValue xAlteVal = xAlternativesVals.addNewAlternativeValue();
				xAlteVal.setAlternativeID(alte.getId());
				XValue xVal = xAlteVal.addNewValue();
				xVal.setInteger(preorder.getRank(alte));
			}
			XMCDAWriteUtils.write(xAlternativesVals, outputStream, true);
			
			OutputUtils.lcln("OK!");
			OutputUtils.logln("[i] " + fileName.substring(1)
					+ " has been successfully exported!");
			if (display) {
				// Nothing to display
			} else {
				// Nothing to output
			}
			return 1;
		} else
			OutputUtils.lcln("Nothing to export.");
		return 0;
		
	}
	
	public String getOutputPath() {
		return Config.OUTPUT_FOLDER;
	}

	public void setOutputPath(String filePath) {

		Config.OUTPUT_FOLDER = filePath;

		Config.MODEL_FOLDER = Config.OUTPUT_FOLDER + "/models";
		Config.SOL_FOLDER = Config.OUTPUT_FOLDER + "/sols";
		Config.XML_FOLDER = Config.OUTPUT_FOLDER + ""; //just put the output files in the output folder

		this.setFilePath(Config.XML_FOLDER);

	}

	// ///////////////////////////
	// // SETTERS & GETTERS ////
	// ///////////////////////////

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
