package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class VoidSort extends Sort {
	public VoidSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Void");
		this.setRunAllSortsName("Void Sort");
		this.setRunSortName("Voidsort");
		this.setCategory("Impractical Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(1024);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private void voidSort(int[] A, int i, int j, int d, int o) {
		Writes.recordDepth(d++);
		
		if (Reads.compareValues(A[i], A[j]) == 1) {
			Writes.swap(A, i, j, 0.005, true, false);
		}
		
		Delays.sleep(0.0025);
		
		Highlights.markArray(1, i);
		Highlights.markArray(2, j);
		
		for(int p=1; p<j-i; p++) {
			Writes.recursion(2);
			voidSort(A, i+p, j, d, o);
			voidSort(A, i, j-p, d, o);
			if(o > 0) {
				Writes.reversal(A, i, j, 0.01, true, false);
				Writes.recursion();
				voidSort(A, i, j, d, o - 1);
			}
		}
	}
	private void voidVoidSort(int[] a, int i, int j, int d, int o) {
		Writes.recordDepth(d++);
		for(int p=1; p<j-i; p++) {
			Writes.recursion(2);
			voidVoidSort(a, i, j-p, d, o*o);
			voidVoidSort(a, i+p, j, d, o*o);
			if(o > 0) {
				Writes.reversal(a, i, j, 0.01, true, false);
				Writes.recursion();
				voidVoidSort(a, i, j, d, o - 1);
			} else {
				voidSort(a, i, j, d, j - i);
			}
		}
	}

	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		this.voidVoidSort(array, 0, currentLength - 1, 0, currentLength);
	}
}