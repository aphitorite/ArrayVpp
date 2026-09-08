package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class AlternateCombSort extends Sort {
    public AlternateCombSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Alternate Comb");
        this.setRunAllSortsName("Alternate Combsort");
        this.setRunSortName("Alternate Combsort");
        this.setCategory("Exchange Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
		this.setAuthors("aphitorite");
		this.setConstant("n log n");
    }
	
	private int nextGap(int x) {
		return (int)(x/1.3117);
	}
	
	private boolean compSwap(int[] array, int a, int b) {
		if(Reads.compareIndices(array, a, b, 0.25, true) > 0) {
			Writes.swap(array, a, b, 0.75, true, false);
			return true;
		}
		return false;
	}

    @Override
    public void runSort(int[] array, int n, int bucketCount) {
		int m = 28914286;
		while(m >= n) m = this.nextGap(m);
		
		for(int i = m; m > 0; i++) {
			if(i == n) { i = n-1-m; m = this.nextGap(m); }
			for(int j = i, k = m; j >= k; j -= k + (k > 1 ? 1 : 0), k = Math.max(1, this.nextGap(k)))
				if(!this.compSwap(array, j-k, j) && k == 1) break;
		}
    }
}