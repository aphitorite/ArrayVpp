package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class AdaptiveVelvetSort extends Sort {  
    public AdaptiveVelvetSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Adaptive Velvet");
        this.setRunAllSortsName("Adaptive Velvet Sort");
        this.setRunSortName("Adaptive Velvet Sort");
        this.setCategory("Selection Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    // Based off of Thehf Fiseg Wnida Dwoiqel (draft algorithm for The Epsilon Committee)
    private void pair(int[] array, int a, int m) {
    	if(a+1<m && Reads.compareIndices(array, a, a+1, 1, true) > 0)
    		Writes.swap(array, a, a+1, 1, true, false);
    }
    
    private void heap(int[] array, int a, int b) {
    	int m=a+(b-a)/2;
    	if(a < m) {
    		heap(array, a, m);
    		heap(array, m, b);
    		if(Reads.compareIndices(array, a, m, 1, true) > 0) {
    			Writes.swap(array, a, m, 1, true, false);
    			heap(array, m, b);
    		}
    	}
    }
    
    private void sift(int[] array, int a, int a1, int b) {
    	int p = (b - a + 1) / 2, B = b, lp = 0;
    	while(p != lp) {
    		while(b - p > a1 && p != lp) {
        		b -= p;
        		lp = p;
        		p = (p + 1) / 2;
        		if(Reads.compareIndices(array, a1, b, 1, true) > 0) {
        			Writes.swap(array, a1, b, 1, true, false);
        			pair(array, b, B);
        			sift(array, b, b, b+lp);
        		}
    		}
    		lp = p;
    		p = (p + 1) / 2;
    	}
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	heap(array, 0, currentLength);
        for(int i=1; i<currentLength-1; i++)
        	sift(array, 0, i, currentLength);
    }
}