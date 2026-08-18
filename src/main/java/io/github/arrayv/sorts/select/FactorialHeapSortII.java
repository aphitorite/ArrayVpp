package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class FactorialHeapSortII extends Sort {
    public FactorialHeapSortII(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Factorial Heap IIe");
        this.setRunAllSortsName("Factorial Heap Sort II");
        this.setRunSortName("Factorial Heapsort II");
        this.setCategory("Selection Sorts");
  	    this.setAuthors("Distray");
        this.setConstant("n log^2 n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private int hcount(int n) {
    	if(n == 0) return 1;
    	return (n + 1) * hcount(n - 1) + 1;
    }
    
    private int ctz(int v, int s) {
    	int n = 2 + s;
    	while(v % n == 0) {
    		v /= n; n++;
    	}
    	return n - 2 - s;
    }
    
    private void siftDown(int[] array, int a, int b, int hl, int tmp, boolean step) {
    	while(hl > 0) {
	    	int l = a + 1, ll = l, hc = hcount(hl - 1);
	    	for(int j = 0, k = hc; j < hl && l + k < b; j++, k += hc) {
	    		if(Reads.compareIndices(array, ll, l + k, 0.25, true) < 0) ll = l + k;
	    	}
	    	if(ll < b && Reads.compareValueIndex(array, tmp, ll, 0.5, true) < 0) {
	    		Writes.write(array, a, array[ll], 2, true, false);
	    		a = ll; step = true; hl--;
	    	} else {
	    		break;
	    	}
    	}
		if(step) Writes.write(array, a, tmp, 1, true, false);
    }
    

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	int mh = 0;
    	while(hcount(++mh) <= length);
    	for(int hv = 1; hv <= mh; hv++) {
    		for(int i = mh - hv, j = 1; i < length; i += hcount(hv) + ctz(j++, hv)) {
        		siftDown(array, i, length, hv, array[i], false);
    		}
    	}
    	for(int i = length - 1; i > mh; i--) {
    		int t = array[i];
    		Writes.write(array, i, array[0], 2.5, true, false);
    		siftDown(array, 0, i, mh, t, true);
    	}
    	Writes.reversal(array, 0, mh, 0.5, true, false);
    }
}