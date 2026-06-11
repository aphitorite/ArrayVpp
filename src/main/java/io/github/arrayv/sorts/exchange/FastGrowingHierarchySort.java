package io.github.arrayv.sorts.exchange;

/* 
 * This algorithm implements the Fast-growing hierarchy, a family of extremely fast growing functions.
 * https://en.wikipedia.org/wiki/Fast-growing_hierarchy
 * The functions can be labeled with natural numbers and even countable infinite ordinal numbers which produce even faster growing functions.
 * 
 * At every step of the recursion, every time f is called, a comparison between two adjacent elements is made.
 * If the compared elements are out of order, they are swapped.
 * Which elements are compared is determined by the finite rest of the function label modulo the array size.
 * Because of the absurd recursion this guarantees that the array is sorted at the end.
 * 
 * In order to produce very large countable ordinals with which to label functions, an Ordinal collapsing function described here is implemented: 
 * https://en.wikipedia.org/wiki/Ordinal_collapsing_function
 * The user has the choice between three starting ordinals.
 * 
 * Most inputs and array sizes lead to a stack overflow error, since the stack required for the recursions wouln't fit into the known universe.
 */

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ordinals.*;

public class FastGrowingHierarchySort extends Sort {

	public FastGrowingHierarchySort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Fast-growing Hierarchy");
        this.setRunAllSortsName("Fast-growing Hierarchy Sort");
        this.setRunSortName("Fast-growing Hierarchy Sort");
        this.setCategory("Esoteric Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(1);
        this.setBogoSort(false);
        this.setAuthors("Tycho/Äonothem");
        this.setQuestion("Choose ordinal:\n1 is \u03C9\n2 is \u03B5_0\n3 is \u03C8(\u03B5_(\u03A9 + 1))\n Default is \u03C9", 1);
	}
	
	private Finite f(Ordinal a, Finite n, int[] array, int length) {
		int r = a.getFiniteRest().getValMod(length);
		if (Reads.compareIndices(array, r, r+1, 0, true) == 1) {
			Writes.swap(array, r, r+1, 0, true, false);
		}
		
		if (a.isZero()) {
			return (Finite)n.successor();
		} else if (a.isLimit()){
			//System.out.println(a.toString()); //Enable to see the limit ordinal function labels in the console (
			return f(a.funSeq(n), n, array, length);
		} else {
			Finite b = n;
			a = a.predecessor();
			for (Finite i = Ordinal.ZERO; i.compareTo(n) == -1; i = (Finite)i.successor()) {
				//System.out.println(b.toString());
				b = (Finite)f(a, b, array, length);
			}
			return b;
		}
	}

	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		switch(bucketCount) {
			default:
			case 1:
				f(Ordinal.OMEGA, new Finite(sortLength), array, sortLength);
				break;
			case 2:
				f(Ordinal.EPSILON0, new Finite(sortLength), array, sortLength);
				break;
			case 3:
				f(Ordinal.BHO, new Finite(sortLength), array, sortLength);
				break;
				
		}
	}
}