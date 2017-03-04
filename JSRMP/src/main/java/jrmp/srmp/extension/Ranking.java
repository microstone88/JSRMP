/**
 * 
 */
package jrmp.srmp.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.decision_deck.utils.collection.CollectionUtils;
import org.decision_deck.utils.relation.graph.Preorder;

/**
 * @author micro
 *
 */
public class Ranking<E> {

	private LinkedList<Set<E>> rankList;

	/**
	 * 1 for the first element's rank, 2 for the second and so on.
	 * @param size
	 */
	public Ranking(int size) {
		
		rankList = new LinkedList<Set<E>>();
		
		for (int i = 0; i < size; i++) {
			Set<E> newSet = CollectionUtils.newHashSetNoNull();
			rankList.add(newSet);
		}
		
	}
	
	public boolean put(E element, int rank) {
		return rankList.get(rank-1).add(element);
	}
	
	public boolean put(Set<E> set, int rank) {
		return rankList.get(rank-1).addAll(set);
	}
	
	/**
	 * @param element
	 * @return the rank of the element.If the element could not be found, return -1.
	 */
	public int getRank(E element) {
		for (Iterator<Set<E>> it = rankList.iterator(); it.hasNext();) {
			Set<E> set = it.next();
			if (set.contains(element)) {
				return 1+rankList.indexOf(set);
			}
		}
		return -1;
	}
	
	public Set<E> getElementsSet(int rank) {
		return rankList.get(rank-1);
	}
	
	public ArrayList<E> getElementsList(int rank) {
		return new ArrayList<E>(rankList.get(rank-1));
	}
	
	public int removeEmptyRanks() {
		int re = 0;
		Set<Set<E>> needToRemove = CollectionUtils.newHashSetNoNull();
		for (Iterator<Set<E>> it = rankList.iterator(); it.hasNext();) {
			Set<E> set = it.next();
			if (set.isEmpty()) {
				needToRemove.add(set);
				re++;
			}
		}
		rankList.removeAll(needToRemove);
		return re;
	}
	
	public int sizeOf() {
		return this.rankList.size();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Preorder<E> toPreorder() {
		
		Preorder<E> re = new Preorder<>();
		
		this.removeEmptyRanks();
		
		for (int i = 0; i < rankList.size(); i++) {
			re.put(rankList.get(i), i+1);
		}
		
		return re;
		
	}
	
	
	@Override
	public String toString() {
		String str = "";
		for (Iterator<Set<E>> it = rankList.iterator(); it.hasNext();) {
			Set<E> set = it.next();
			str += "    " + (1+rankList.indexOf(set)) + ": ";
			str += set.toString() + "\n";
		}
		return str;
	}
	
}
