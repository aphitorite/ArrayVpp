 package io.github.arrayv.sorts.esoteric;
 
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.ShellSort;
import io.github.arrayv.sorts.templates.Sort;
 
 public final class MoreOptimizedIteratorSort extends Sort {
	private boolean direction = true;
	 
	public MoreOptimizedIteratorSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("More Optimized Iterator");
		this.setRunAllSortsName("More Optimized Iterator Sort");
		this.setRunSortName("More Optimized Iterator Sort");
		this.setCategory("Esoteric Sorts");
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
		Highlights.markArray(1, i);
		Highlights.markArray(2, j);
		 
		Delays.sleep(0.5D);
		 
		int cmp = Reads.compareValues(A[i], A[j]);
		
		if (dir == ((cmp == 1))) Writes.swap(A, i, j, 0.5D, true, false);  
	}

	private void bitonicMerge(int[] A, int lo, int n, boolean dir) {
		if (n > 1) {			
			int m = greatestPowerOfTwoLessThan(n);
			Highlights.markArray(3, lo);
			Highlights.markArray(4, lo + n);
			Highlights.markArray(5, lo + m);
			
			for (int i = lo; i < lo + n - m; i++) {
				compare(A, i, i + m, dir);
			}
			bitonicMerge(A, lo, m, dir);
			bitonicMerge(A, lo + m, n - m, dir);
		} 
	}

	private void bitonicSort(int[] A, int lo, int n, boolean dir) {
		if (n > 1) {			
			bitonicSort(A, lo + (int)Math.sqrt((4 * n)), n - (int)Math.sqrt((4 * n)), true);
			bitonicMerge(A, lo, n, true);
		} 
	}
 
	public void changeDirection(String choice) throws Exception {
		if (choice.equals("forward")) { this.direction = true; }
		else if (choice.equals("backward")) { this.direction = true; }
		else { throw new Exception("Invalid direction for Bitonic Sort!"); }
	
	}
	
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		bitonicSort(array, 0, sortLength, this.direction);
		ShellSort sort2 = new ShellSort(this.arrayVisualizer);
		sort2.runSort(array, sortLength);
	}
}