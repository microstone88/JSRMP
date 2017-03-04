/**
 * 
 */
package jrmp.srmp.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import jrmp.srmp.extension.Ranking;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.relation.graph.Preorder;

/**
 * @author micro
 *
 */
public class RankingUtils {

	public static Ranking<Alternative> getRanking(SparseMatrixD<Alternative, Alternative> matrix) {
		
		if (matrix != null) {
			int mAlte = countAlternatives(matrix);
			Ranking<Alternative> re = new Ranking<Alternative>(mAlte);
			for (Iterator<Alternative> itR = matrix.getRows().iterator(); itR
					.hasNext();) {
				Alternative alteR = itR.next();
				int neg1 = mAlte;
				int zero = -1;
				for (Iterator<Alternative> itC = matrix.getColumns().iterator(); itC
						.hasNext();) {
					Alternative alteC = itC.next();
					if (matrix.getValue(alteR, alteC) == 1) {
						neg1--;
					}
					if (matrix.getValue(alteR, alteC) == 0) {
						zero++;
					}
				}
				int rank = neg1 - zero;
				re.put(alteR, rank);
			}
			re.removeEmptyRanks();
			return re;
		}else
			OutputUtils.lcln("[i] Building the ranking list... Failed!");
			return null;
		
	}
	
	public static Preorder<Alternative> getPreorder(SparseMatrixD<Alternative, Alternative> matrix) {
		
		Preorder<Alternative> re = new Preorder<>();
		
		Ranking<Alternative> ranking = RankingUtils.getRanking(matrix);
		for (int i = 0; i < ranking.sizeOf(); i++) {
			re.put(ranking.getElementsSet(i+1), i+1);
		}
		
		return re;
		
	}
	
	public static SparseMatrixD<Alternative, Alternative> toSparseMatrixD(Preorder<Alternative> preorder) {
		
		SparseMatrixD<Alternative,Alternative> re = Matrixes.newSparseD();

		for (int i = 1; i < preorder.getRanksCount(); i++) {
			
			//add "0" (Indifference)
			ArrayList<Alternative> list1 = new ArrayList<>(preorder.get(i));
			ArrayList<Alternative> list2 = new ArrayList<>(preorder.get(i+1));
			for (int j = 0; j < list1.size()-1; j++) {
				re.put(list1.get(j), list1.get(j+1), 1);
				re.put(list1.get(j+1), list1.get(j), 1);
				
				//add "1" (Preference)
				for (int k = 0; k < list2.size(); k++) {
					re.put(list1.get(j), list2.get(k), 1);
				}
				
			}
			
			for (int k = 0; k < list2.size(); k++) {
				re.put(list1.get(list1.size()-1), list2.get(k), 1);
			}
			
		}
		
		return re;
		
	}
	
	
	public static Integer getIndifferenceCount(SparseMatrixD<Alternative, Alternative> matrix) {
		
		if (matrix != null) {
			int re = -countAlternatives(matrix);
			for (Iterator<Alternative> itR = matrix.getRows().iterator(); itR
					.hasNext();) {
				Alternative alteR = itR.next();
				for (Iterator<Alternative> itC = matrix.getColumns().iterator(); itC
						.hasNext();) {
					Alternative alteC = itC.next();
					if (matrix.getValue(alteR, alteC) == 0) {
						re++;
					}
				}
			}
			return re / 2;
		}else
			return 0;
		
	}
	
	public static Integer countAlternatives(SparseMatrixD<Alternative, Alternative> matrix) {
		
		Set<Alternative> columnSet = matrix.getColumns();
		Set<Alternative> rowSet = matrix.getRows();
		LinkedHashSet<Alternative> tempo = new LinkedHashSet<Alternative>();
		tempo.addAll(columnSet);
		tempo.addAll(rowSet);
		return tempo.size();
			
	}
	
	
	
}
