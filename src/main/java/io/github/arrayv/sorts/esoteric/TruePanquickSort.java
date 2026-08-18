package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class TruePanquickSort extends Sort {
	public TruePanquickSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("True Panquick");
		this.setRunAllSortsName("True Panquick Sort");
		this.setRunSortName("True Panquick Sort");
		this.setCategory("Esoteric Sorts");
        this.setAuthors("Distray");
		this.setConstant("n log^3 n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private int start = 0;
	private static boolean inorder = false;
	
	private void flip(int[] array, int len) {
		Writes.reversal(array, start, start+len, 1, true, false);
	}
	
	private void lrpq(int[] array, int len) {
		if(len < 1)
			return;
		int p = array[start+len/2], i = start, j = start + len;
		while(i <= j) {
			while(i <= j && Reads.compareValues(array[i], p) < 0) {
				Highlights.markArray(1, i);
				Delays.sleep(0.75);
				i++;
			}
			while(i <= j && Reads.compareValues(array[j], p) > 0) {
				Highlights.markArray(2, j);
				Delays.sleep(0.75);
				j--;
			}
			if(i <= j) {
				if(i != j) { // equivalent to Panquick's i/j reversal, but with valid Pancake flips
					flip(array, j);
					flip(array, j-i);
					flip(array, j);
				}
				i++; j--;
			}
		}
		
		lrpq(array, j-start); // sort left side
		
		if(inorder) {
			flip(array, j-start); // reverse left side,
			flip(array, len); // so that the right side reverse keeps it in order
		} else {
			flip(array, len); // push left side to end
		}
		
		lrpq(array, len-(i-start)); // sort right side, now at start
		
		if(inorder) {
			flip(array, len-(i-start)); // reverse right side
			flip(array, len); // push left side back to start
			flip(array, j-start); // unreverse left side
		} else {
			flip(array, len-(i-start)); // reverse right side, creating a reverse-sorted list
			flip(array, len); // flip it back in order
		}
	}
	
	public void lrPanquick(int[] array, int start, int len) {
		this.start = start;
		lrpq(array, len);
	}


	public void runSort(int[] array, int currentLength, int bucketCount) {
		lrPanquick(array, 0, currentLength-1);
	}
}