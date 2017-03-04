package jrmp.srmp.base;

import java.util.ArrayList;

import org.decision_deck.jmcda.structure.Alternative;

public abstract class SRMPsolver extends SRMPmodeler {

	//////////////////////
	////  ATTRIBUTES  ////
	//////////////////////

	private ArrayList<XSRMPmodeler> solutions = new ArrayList<XSRMPmodeler>();
	
	private ArrayList<Double> objValues = new ArrayList<Double>();
	
	private ArrayList<String> solutionFiles = new ArrayList<String>();
	
	private ArrayList<String> modelFiles = new ArrayList<String>();
	
	private ArrayList<String> poolFiles = new ArrayList<String>();
	
	protected int mElte;
	
	protected ArrayList<Alternative> eltList;
	
	///////////////////////
	////  CONSTRUCTOR  ////
	///////////////////////
	
	public SRMPsolver(XSRMPmodeler input) {
		
		super(input);
		
		mElte   = this.getInput().getNumOfAltesInPCs();
		eltList = this.getInput().getListOfAltesInPCs();
	}
	
	///////////////////
	////  METHODS  ////
	///////////////////
	
	public abstract void solve(boolean display);
	
	/**
	 * @param direction maximize(default) or minimize
	 * @return the index of the best solution
	 */
	protected Integer findTheBestSolution(String direction) {
		
		int re = -1;
		if (this.isLegal()) {
			
			Double best = this.getObjValue(0);
			for (int i = 0; i < this.solutions.size(); i++) {
				
				Double val = this.getObjValue(i);
				if (direction.toLowerCase().contains("min")) {
					if (val <= best) re = i;
				}else{
					// Default is "max"imize the objective value
					if (val >= best) re = i;
				}
			
			}
			
		}
		return re;
		
	}
	
	private boolean isLegal() {
		
		int ref = this.solutions.size();
		int a 	= this.objValues.size();
		int b 	= this.solutionFiles.size();
		int c 	= this.poolFiles.size();
		if (a == ref && b == ref && c ==ref) return true;
		else return false;
		
	}
	
	
	public boolean isSolved() {
		return this.isCompleted();
	}

	protected void setSolved(boolean solved) {
		this.setCompleted(solved);
	}
	
	protected void addSolution(XSRMPmodeler solution) {
		this.solutions.add(solution);
	}
	
	protected void addObjValue(Double objValue) {
		this.objValues.add(objValue);
	}
	
	protected void addSolutionFile(String filepath) {
		this.solutionFiles.add(filepath);
	}
	
	protected void addModelFile(String filepath) {
		this.modelFiles.add(filepath);
	}
	
	protected void addPoolFile(String filepath) {
		this.poolFiles.add(filepath);
	}
	
	public XSRMPmodeler getSolution(Integer index) {
		return this.solutions.get(index);
	}
	
	public Double getObjValue(Integer index) {
		return this.objValues.get(index);
	}
	
	public String getSolutionFile(Integer index) {
		return this.solutionFiles.get(index);
	}
	
	public String getModelFile(Integer index) {
		return this.modelFiles.get(index);
	}
	
	public String getPoolFile(Integer index) {
		return this.poolFiles.get(index);
	}
	
	/////////////////////////////
	////  SETTERS & GETTERS  ////
	/////////////////////////////

	/**
	 * @return the solutions
	 */
	public ArrayList<XSRMPmodeler> getSolutions() {
		return solutions;
	}

	/**
	 * @return the objValues
	 */
	public ArrayList<Double> getObjValues() {
		return objValues;
	}

	/**
	 * @return the solutionFiles
	 */
	public ArrayList<String> getSolutionFiles() {
		return solutionFiles;
	}

	/**
	 * @return the modelFiles
	 */
	public ArrayList<String> getModelFiles() {
		return modelFiles;
	}
	
	/**
	 * @return the poolFiles
	 */
	public ArrayList<String> getPoolFiles() {
		return poolFiles;
	}
	
}
