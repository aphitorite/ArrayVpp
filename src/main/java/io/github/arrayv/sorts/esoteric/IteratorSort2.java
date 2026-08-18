package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class IteratorSort2 extends Sort {
	private boolean direction = true;
	
	public IteratorSort2(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Iterator 2");
		this.setRunAllSortsName("Iterator Sort 2");
		this.setRunSortName("Iterator Sort 2");
		this.setCategory("Esoteric Sorts");
        this.setAuthors("Potassium");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(512);
		this.setBogoSort(false);
	}
	
	private static int greatestPowerOfTwoLessThan(int n) {
		int k = 1;
		while (k < n) {
			k <<= 1;
		}
		return k >> 1;
	}
	
	private void compare(int[] A, int i, int j, boolean dir) {
		this.Highlights.markArray(1, i);
		this.Highlights.markArray(2, j);
		
		this.Delays.sleep(0.5D);
		
		int cmp = this.Reads.compareValues(A[i], A[j]);
		
		if (dir == ((cmp == 1))) this.Writes.swap(A, i, j, 0.5D, true, false);
	}
	
	private void bitonicMerge(int[] A, int lo, int n, boolean dir) {
		if (n > 1) {
			int m = greatestPowerOfTwoLessThan(n);
			this.Highlights.markArray(3, lo);
			this.Highlights.markArray(4, n);
			this.Highlights.markArray(5, m);
			
			for (int i = lo; i < lo + n - m; i++) {
				compare(A, i, i + m, dir);
			}
			bitonicMerge(A, lo + m, n - m, dir);
			bitonicMerge(A, lo, m, dir);
			bitonicMerge(A, lo + 1, n - 1, dir);
		} 
	}

	
	private void bitonicSort(int[] A, int lo, int n, boolean dir) {
		if (n > 1) {
			int m = n / 2 + 1;
			
			bitonicMerge(A, lo + m, n - m, false);
			bitonicMerge(A, lo, m, true);
			bitonicMerge(A, lo, n, true);
			bitonicMerge(A, lo + m, n - m, true);
			bitonicMerge(A, lo, m, false);
			bitonicMerge(A, lo, n, true);
			bitonicMerge(A, lo + m, n - m, false);
			bitonicMerge(A, lo, m, true);
			bitonicMerge(A, lo, n, false);
			bitonicMerge(A, lo + m, n - m, true);
			bitonicMerge(A, lo, m, false);
			bitonicMerge(A, lo, n, false);
			bitonicMerge(A, lo + m, n - m, true);
			bitonicMerge(A, lo, m, true);
			bitonicMerge(A, lo, n, true);
			bitonicMerge(A, lo + m, n - m, false);
			bitonicMerge(A, lo, m, false);
			bitonicMerge(A, lo, n, true);
			bitonicMerge(A, lo + m, n - m, true);
			bitonicMerge(A, lo, m, true);
			bitonicMerge(A, lo, n, false);
			bitonicMerge(A, lo + m, n - m, false);
			bitonicMerge(A, lo, m, false);
			bitonicMerge(A, lo, n, false);
		} 
	}
	
	public void changeDirection(String choice) throws Exception {
		if (choice.equals("forward")) { this.direction = true; }
		else if (choice.equals("backward")) { this.direction = true; }
		else { throw new Exception("Invalid direction for Bitonic Sort!"); }
	
	}
	
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		for (int k = sortLength; k > 0; k--) {
			bitonicSort(array, 0, sortLength, this.direction);
		}
		this.Writes.reversal(array, 0, sortLength - 1, 1.0D, true, false);
	}
}