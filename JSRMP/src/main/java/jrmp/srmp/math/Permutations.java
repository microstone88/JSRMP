/**
 * 
 */
package jrmp.srmp.math;

import java.util.ArrayList;
import java.util.List;

/**
 * @author micro
 *
 */
public class Permutations {
	
	private static int NUM;
	private static ArrayList<ArrayList<Integer>> all;

	public static ArrayList<ArrayList<Integer>> getAll(int k){
		
		NUM = k;
		all = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> fst = new ArrayList<Integer>();
		for (int i = 1; i < NUM + 1; i++) {
		//for (int i = NUM-1; i >= 0; i--) {
			fst.add(i);
		}
		permutation(fst, new ArrayList<Integer>());
		return all;
		
	}
	
	private static void permutation(List<Integer> data, List<Integer> target){
		
		if (target.size() == NUM){
			all.add(new ArrayList<Integer>(target));
			return;
		}
		
		for (int i = 0; i < data.size(); i++) {
			
			//System.out.println(i + "/" + data.size());
			
			List<Integer> newData = new ArrayList<Integer>(data);
			List<Integer> newTarget = new ArrayList<Integer>(target);
			
			newTarget.add(newData.get(i));
			//System.out.println("New target : "+ newTarget);
			newData.remove(i);
			//System.out.println("New data : "+ newData);
			
			permutation(newData, newTarget);
			
		}
		
	}
	
}
