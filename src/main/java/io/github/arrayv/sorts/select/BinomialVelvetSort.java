package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class BinomialVelvetSort extends Sort {  
    public BinomialVelvetSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Binomial Velvet");
        this.setRunAllSortsName("Binomial Velvet Sort");
        this.setRunSortName("Binomial Velvet Sort");
        this.setCategory("Selection Sorts");
		this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    
    // Based off of Thehf Fiseg Wnida Dwoiqel (draft algorithm for The Epsilon Committee)
    
    // Invariant: Base children of any given root must be in order, and all leaves must be
    // already in ascending index order
    
    private void heap(int[] array, int a, int b) {
    	int m=a+(b-a)/2;
    	if(a < m) {
    		heap(array, a, m);
    		heap(array, m, b);
    		// invariant remains maintained on left child if swapped, invariant may be
    		// pre-broken on root
    		if(Reads.compareIndices(array, a, m, 1, true) > 0) {
    			Writes.swap(array, a, m, 1, true, false);
    			// maintain invariant with right child of root
    			sift(array, m, m, b, array[m]);
    		}
    	}
    }
    
    private void sift(int[] array, int a, int a1, int b, int tmp) {
    	int p = b - a, lp = 0, min = b, minp = p;
    	do {
    		lp = p;
    		minp = p = (p + 1) / 2;
    		min = b - p;
    	} while(p != lp && b - p <= a1);
    	// find minimum "right child" coming after a1
    	while(p != lp) {
    		while(b - p > a1 && p != lp) {
        		b -= p;
        		lp = p;
        		p = (p + 1) / 2;
        		if(min != b && Reads.compareIndices(array, min, b, 1, true) >= 0) {
        			min = b;
        			minp = lp;
        		}
    		}
    		lp = p;
    		p = (p + 1) / 2;
    	}
    	// push a1 to leaf that maintains invariant
    	if(min > a1 && Reads.compareValueIndex(array, tmp, min, 1, true) > 0) {
    		Writes.write(array, a1, array[min], 1, true, false);
			sift(array, min, min, min+minp, tmp);
    	} else Writes.write(array, a1, tmp, 1, true, false);
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	heap(array, 0, currentLength);
        for(int i=1; i<currentLength-1; i++)
        	sift(array, 0, i, currentLength, array[i]);
    }
}