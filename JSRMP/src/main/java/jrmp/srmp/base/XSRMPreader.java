/**
 * 
 */
package jrmp.srmp.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.relation.graph.Preorder;
import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDAAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDAAlternativesMatrix;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDACriteria;
import org.decisiondeck.jmcda.persist.xmcda2.XMCDAEvaluations;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativesComparisons;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteria;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteriaValues;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterionValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPerformanceTable;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAReadUtils;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

import jrmp.srmp.extension.PreDef;
import jrmp.srmp.settings.Config;
import jrmp.srmp.utils.OutputUtils;

/**
 * @author micro
 *
 */
public class XSRMPreader extends XSRMPmodeler {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////
	
	private String filePath;

	private XMCDAReadUtils reader = new XMCDAReadUtils();
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public XSRMPreader() {
		this.setStatus(XSRMPmodeler.Status.initialized);
		this.setInputPath(Config.INPUT_FOLDER);
	}

	public XSRMPreader(XSRMPmodeler model) {
		super(model);
		this.setStatus(XSRMPmodeler.Status.overwritten);
		this.setInputPath(Config.INPUT_FOLDER);
	}
	
	///////////////////
	////  METHODS  ////
	///////////////////
	
	public Integer read() throws Exception {
		
		OutputUtils.consoleln("\n[Loading Process]");
		OutputUtils.lsln("\n *** Files Loading *** \n");
		this.setStatus(XSRMPmodeler.Status.loaded);
		
		int re = 0;
		re += this.loadAlternatives(false);
		re += this.loadCriteria(false);
		re += this.loadEvaluations(false);
		re += this.loadRefComps(false);
		re += this.loadWeights(false);
		re += this.loadRefPts(false);
		re += this.loadLexicography(false);
		re += this.loadRanking(false);
		
		OutputUtils.lscln("[i] " + re + "/8 files have been loaded.");
		OutputUtils.lsln("[i] Loading completed!");

		return re;
	}
	
	public Integer read(boolean display) throws Exception{
		
		OutputUtils.consoleln("\n[Loading Process]");
		OutputUtils.lsln("\n *** Files Loading *** \n");
		this.setStatus(XSRMPmodeler.Status.loaded);
		
		int re = 0;
		re += this.loadAlternatives(display);
		re += this.loadCriteria(display);
		re += this.loadEvaluations(display);
		re += this.loadRefComps(display);
		re += this.loadWeights(display);
		re += this.loadRefPts(display);
		re += this.loadLexicography(display);
		re += this.loadRanking(display);

		OutputUtils.lscln("[i] " + re + "/8 files have been loaded.");
		OutputUtils.lsln("[i] Loading completed!");
		
		return re;
	}
	
	
	/**
	 * @param display
	 * @param mode "a" = aggregation,"d" = disaggregation using pairwise comparisons,"r" = disaggregation using ranking list, default = all.
	 * @return the loaded file number
	 * @throws Exception 
	 */
	public Integer read(boolean display, String mode) throws Exception{
		
		OutputUtils.consoleln("\n[Loading Process]");
		OutputUtils.lsln("\n *** Files Loading *** \n");
		this.setStatus(XSRMPmodeler.Status.loaded);
		
		int re = 0;
		re += this.loadAlternatives(display);
		re += this.loadCriteria(display);
		re += this.loadEvaluations(display);
		
		switch(mode) {
		
		case "a": {
			re += this.loadWeights(display);
			re += this.loadRefPts(display);
			re += this.loadLexicography(display);
			OutputUtils.lscln("[i] " + re + "/6 files have been loaded.");
		};break;		//Read input files for aggregation
		
		case "d": {
			re += this.loadRefComps(display);
			OutputUtils.lscln("[i] " + re + "/4 files have been loaded.");
		};break;		//Read the pairwise comparisons for disaggregation
		
		case "r": {
			re += this.loadRanking(display);
			OutputUtils.lscln("[i] " + re + "/4 files have been loaded.");
		};break;		//Read the alternative ranking for disaggregation
		
		default: {
			re += this.loadWeights(display);
			re += this.loadRefPts(display);
			re += this.loadLexicography(display);
			re += this.loadRefComps(display);
			re += this.loadRanking(display);
			OutputUtils.lscln("[i] " + re + "/8 files have been loaded.");
		};break;		//Read all inputs files
		
		}

		OutputUtils.lsln("[i] Loading completed!");
		
		return re;
	}
	
	public Integer read(String path) throws Exception{
		
		this.setInputPath(path);
		
		OutputUtils.consoleln("\n[Loading Process]");
		OutputUtils.lsln("\n *** Files Loading *** \n");
		this.setStatus(XSRMPmodeler.Status.loaded);
		
		int re = 0;
		re += this.loadAlternatives(false);
		re += this.loadCriteria(false);
		re += this.loadEvaluations(false);
		re += this.loadRefComps(false);
		re += this.loadWeights(false);
		re += this.loadRefPts(false);
		re += this.loadLexicography(false);
		re += this.loadRanking(false);

		OutputUtils.lscln("[i] " + re + "/8 files have been loaded.");
		OutputUtils.lsln("[i] Loading completed!");
		
		return re;
	}
	
	/**
	 * Introduce the perturbations into the input alternatives comparisons
	 * @param number the number of perturbations desired. 
	 * Set to negative, if you want to generate a random number of perturbations.
	 * Attention: The number should not exceed the totality of input alternative comparisons.
	 * @return the index array of pairwise comparisons perturbed
	 */
	public ArrayList<Integer> perturb(int number) {
		
		this.setStatus(XSRMPmodeler.Status.overwritten);
		
		// Decide the number of perturbations
		int num = number;
		if (number > this.getNumOfPairComps()) {
			num = this.getNumOfPairComps();
		}
		if (number < 0) {
			Random random = new Random(System.currentTimeMillis());
			num = 1 + random.nextInt(-number);
		}
		
		// Generate the perturbations
		Random random = new Random(System.currentTimeMillis());
		ArrayList<Integer> aimList = new ArrayList<Integer>();
		
		int count = 0;
		while (count < num) {
			
			Integer aim = random.nextInt(this.getListOfPairComps().size());
			if (!aimList.contains(aim)) {
				aimList.add(aim+1);
				Alternative row = this.getListOfPairComps().get(aim).getRowKey();
				Alternative col = this.getListOfPairComps().get(aim).getColumnKey();
				double init = this.getListOfPairComps().get(aim).getValue();
				this.getRefComps().put(row, col, -init);
				count++;
			}
			
		}
		
		// Display the perturbations
		OutputUtils.lsc("[i] The comparison number ");
		for (Integer i : aimList) {
			OutputUtils.lsc((i));
			if (aimList.indexOf(i) != aimList.size() - 1) OutputUtils.lsc(",");
		}
		OutputUtils.lscln("\b has/have been perturbed.");
		
		return aimList;
	}
	
	/**
	 * Introduce the perturbations into the input alternatives comparisons
	 * @param index a array of index of perturbations desired. 
	 * the index takes value from 1 to the number of comparisons.
	 * @return the index array of pairwise comparisons perturbed
	 */
	public ArrayList<Integer> perturb(Integer[] index) {
		
		this.setStatus(XSRMPmodeler.Status.overwritten);
		
		ArrayList<Integer> indList = new ArrayList<Integer>();
		
		int num = this.getNumOfPairComps();
		for (int i = 0; i < index.length; i++) {
			Integer ind = index[i];
			
			if (ind < num && ind > -1) {
				indList.add(ind);
				Alternative row = this.getListOfPairComps().get(ind).getRowKey();
				Alternative col = this.getListOfPairComps().get(ind).getColumnKey();
				double init = this.getListOfPairComps().get(ind).getValue();
				this.getRefComps().put(row, col, -init);
			}
			
		}
		
		// Display the perturbations
		OutputUtils.lsc("[i] The comparison number ");
		for (Integer i : indList) {
			OutputUtils.lsc((i));
			if (indList.indexOf(i) != indList.size() - 1) OutputUtils.lsc(",");
		}
		OutputUtils.lscln("\b has/have been perturbed.");
		
		return indList;
	}
	
	
	/** **********************************************
	 *  **************** ALTERNATIVES ****************
	 *  **********************************************
	 */
	
	private Integer loadAlternatives(boolean display) throws IOException, XmlException, InvalidInputException {
		
		String fileName = "/" + PreDef.FileName.alternatives.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;

		}else{

//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath + fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XAlternatives> listAlternatives = xmcda.getAlternativesList();
			XMCDAAlternatives xmcdaAlternatives = new XMCDAAlternatives();
			this.setAlteSet(xmcdaAlternatives.readAll(listAlternatives));
			this.setAlteNames(xmcdaAlternatives.getNames());
			
			OutputUtils.lcln("OK!");
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] There are " + getAlteSet().size() + " alternatives loaded in total.");
		
		if (display) this.displayAlternatives();
		else this.outputAlternatives();
		
		return 1;
	}
	
	/** **********************************************
	 *  ****************** CRITERIA ******************
	 *  **********************************************
	 */
	
	private Integer loadCriteria(boolean display) throws IOException, XmlException, InvalidInputException {
		
		String fileName = "/" + PreDef.FileName.criteria.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
			
//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath + fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XCriteria> listCriteria = xmcda.getCriteriaList();
			XMCDACriteria xmcdaCriteria = new XMCDACriteria();
			this.setCritSet(xmcdaCriteria.readAll(listCriteria));
			this.setCritNames(xmcdaCriteria.getNames());
			this.setCritScales(xmcdaCriteria.getScales());
			xmcdaCriteria.getIndifferenceThresholds();
			
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] The alternatives are evaluated on " + this.getCritSet().size() + " criteria.");
		
		if (display) this.displayCriteria();
		else this.outputCriteria();
		
		return 1;
	}
	
	/** **********************************************
	 *  ************** CRITERIA WEIGHTS **************
	 *  **********************************************
	 */
	
	private Integer loadWeights(boolean display) throws IOException, XmlException {
		
		String fileName = "/" + PreDef.FileName.weights.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
		
//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath+fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XCriteriaValues> listCritVals = xmcda.getCriteriaValuesList();
			List<XCriterionValue> listWeights = listCritVals.get(0).getCriterionValueList();
			
			
			LinkedHashMap<Criterion, Float> tempMap = new LinkedHashMap<>();
			for (int i = 0; i < listWeights.size(); i++) {
				tempMap.put(new Criterion(listWeights.get(i).getCriterionID()), listWeights.get(i).getValueArray(0).getReal());
			}
			
			this.setWeights(new LinkedHashMap<Criterion, Double>());
			Iterator<Criterion> itc = this.getCritSet().iterator();
			while (itc.hasNext()) {
				Criterion crit = itc.next();
				this.getWeights().put(crit, new Double(tempMap.get(crit)));
			}
			
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] The weights of criteria have been normalized.");
		
		if (display) this.displayWeights();
		else this.outputWeights();
		
		return 1;
	}
	
	/** **********************************************
	 *  **************** EVALUATIONS ****************
	 *  **********************************************
	 */

	private Integer loadEvaluations(boolean display) throws IOException, XmlException, InvalidInputException {
		
		String fileName = "/" + PreDef.FileName.performanceTable.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
		
//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath+fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XPerformanceTable> listPerfTables = xmcda.getPerformanceTableList();
			
			XMCDAEvaluations xmcdaEvas = new XMCDAEvaluations();
			this.setAlteValues(xmcdaEvas.read(listPerfTables));
		
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] There are " + this.getAlteValues().getValueCount() + " values in the performance matrix.");
		
		if (display) this.displayEvaluations();
		else this.outputEvaluations();
		
		return 1;
	}

	/** *************************************************
	 *  ****************** REF. POINTS ******************
	 *  *************************************************
	 */

	private Integer loadRefPts(boolean display) throws IOException, XmlException, InvalidInputException {
		
		String fileName = "/" + PreDef.FileName.profileConfigs.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
		
//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath+fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XPerformanceTable> listProfileConfigs = xmcda.getPerformanceTableList();
			
			//Load profiles evaluations
			XMCDAEvaluations xmcdaEvas = new XMCDAEvaluations();
			this.setRefPtsValues(xmcdaEvas.read(listProfileConfigs));
			this.setRefPtsSet(this.getRefPtsValues().getRows());
			
//			//load lexicographic order
//			List<XAlternativeOnCriteriaPerformances> listProfiles = listProfileConfigs.get(0).getAlternativePerformancesList();
//			this.setLexico(new ArrayList<Integer>());
//			for (int i = 0; i < listProfiles.size(); i++) {
//				this.getLexico().add(new Integer(listProfiles.get(i).getId()));
//			}
		
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] There are " + getRefPtsSet().size() + " reference points involved in this model.");
		
		if (display) {
			this.displayRefPts();
		}else{
			this.outputRefPts();
		}
		
		return 1;
	}
	
	private Integer loadLexicography(boolean display) throws Exception {
		
		String fileName = "/" + PreDef.FileName.lexicography.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
		
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XAlternativeValue> listOrders = xmcda.getAlternativesValuesList().get(0).getAlternativeValueList();
			
			Preorder<Alternative> preorder = new Preorder<Alternative>();
			for (int i = 0; i < listOrders.size(); i++) {
				int order = listOrders.get(i).getValueArray(0).getInteger();
				preorder.put(new Alternative(listOrders.get(i).getAlternativeID()), order);
			}
			this.setLexicography(preorder);
			
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] There are " + getRefPtsSet().size() + " reference points involved in this model.");
		
		if (display) {
			this.displayLexico();
		}else{
			this.outputLexico();
		}
		
		return 1;

	}
	
	/** **********************************************
	 *  ********** ALTERNATIVE COMPARISONS ***********
	 *  **********************************************
	 */
	
	private Integer loadRefComps(boolean display) throws IOException, XmlException, InvalidInputException {
		
		String fileName = "/" + PreDef.FileName.alternativesComparisons.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
		
//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath+fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XAlternativesComparisons> listAlteComp = xmcda.getAlternativesComparisonsList();
			
			XMCDAAlternativesMatrix xmcdaAlteMatrix = new XMCDAAlternativesMatrix();
			this.setRefComps(xmcdaAlteMatrix.readAlternativesFloatMatrix(listAlteComp.get(0)));
			
			Set<Alternative> columnSet = this.getRefComps().getColumns();
			Set<Alternative> rowSet = this.getRefComps().getRows();
			this.setAlteInPCs(new LinkedHashSet<Alternative>());
			this.getAlteInPCs().addAll(columnSet);
			this.getAlteInPCs().addAll(rowSet);
		
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		OutputUtils.lsln("[i] The reference set consist of " + this.getRefComps().getValueCount() + " pairwise comparisons and "
				+ this.getAlteInPCs().size() + " different alternatives.");
		
		if (display) {
			this.displayRefComps();
//			this.displayRefRanking();
		}else{
			this.outputRefComps();
//			this.outputRefRanking();
		}
	
		return 1;
	}
	
	private Integer loadRanking(boolean display) throws IOException, XmlException, InvalidInputException {
		
		String fileName = "/" + PreDef.FileName.ranking.toString() + ".xml";
		File file = new File(Config.RESOURCES_FOLDER + filePath + fileName);
		
		OutputUtils.lc("[i] Loading " + filePath + fileName + "...");
		
		if (!file.exists()) {
			
			OutputUtils.lcln(" File not found (or not given).");
			return 0;
			
		}else{
		
//			InputSupplier<InputStream> input = Resources.newInputStreamSupplier(getClass().getResource(filePath+fileName));
			InputSupplier<FileInputStream> input = Files.newInputStreamSupplier(file);
			
			XMCDA xmcda = reader.getXMCDA(input);
			List<XAlternativesComparisons> listAlteComp = xmcda.getAlternativesComparisonsList();
			
			XMCDAAlternativesMatrix xmcdaAlteMatrix = new XMCDAAlternativesMatrix();
			this.setAllComps(xmcdaAlteMatrix.readAlternativesFloatMatrix(listAlteComp.get(0)));
			
			OutputUtils.lcln("OK!");
			
		}
		
		OutputUtils.logln("[i] " + fileName.substring(1) + " has been successfully loaded!");
		
		if (display) {
			this.displayGlobalRanking();
		}else{
			this.outputGlobalRanking();
		}
	
		return 1;
	}
	
	public String getInputPath() {
		return Config.INPUT_FOLDER;
	}
	
	public void setInputPath(String filePath) {
		Config.INPUT_FOLDER = filePath;
		this.setFilePath(filePath);
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
