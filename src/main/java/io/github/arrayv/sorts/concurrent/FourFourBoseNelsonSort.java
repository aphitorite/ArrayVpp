package io.github.arrayv.sorts.concurrent;

import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.main.ArrayVisualizer;

final public class FourFourBoseNelsonSort extends Sort {
	
    public FourFourBoseNelsonSort(ArrayVisualizer arrayVisualizer) {
    	super(arrayVisualizer);
        
    	this.setSortListName("[4,4] Bose-Nelson");
    	this.setRunAllSortsName("[4,4] Bose-Nelson Sorting Network");
    	this.setRunSortName("[4,4] Bose-Nelson Sort");
        this.setCategory("Concurrent Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private int c;
    
    
    private void compareSwap(int[] array, int a, int b) {
    	if (b < c && Reads.compareIndices(array, a, b, 0.5, true) > 0) {
    	    Writes.swap(array, a, b, 0.5, true, false);
        }
    }
    
    private void nels(int[] array, int a, int b, int c, int d, int v) {
    	if(v == 0)
    		return;
    	if(v == 1) {
    		compareSwap(array, a, b);
        	compareSwap(array, c, d);
    		compareSwap(array, a, c);
    		compareSwap(array, b, d);
    		compareSwap(array, b, c);
    	} else {
    		nels(array, a, b, c, d, v/=4);
    		nels(array, a+v, b+v, c+v, d+v, v);
    		nels(array, a+2*v, b+2*v, c+2*v, d+2*v, v);
    		nels(array, a+3*v, b+3*v, c+3*v, d+3*v, v);
    		
    		// use bose-nelson as cleanup
    		/* [1, 5, 9, 13], [2, 6, 10, 14], [3, 7, 11, 15], [4, 8, 12, 16]
    		 *     ^  ^        ^               ^
    		 *     a  b        c               d
    		 * [1, 2, 3, 13], [5, 6, 10, 14], [9, 7, 11, 15], [4, 8, 12, 16]
    		 *           ^           ^            ^            ^
    		 *           a           b            c            d
    		 * [1, 2, 3, 4], [5, 6, 7, 14], [9, 10, 11, 15], [13, 8, 12, 16]
    		 *                         ^                ^         ^  ^
    		 *                         a                b         c  d
    		 *                         
    		 *
    		 */
    		nels(array, a+v, a+2*v, b, c, v);
    		nels(array, a+3*v, b+2*v, c+v, d, v);
    		nels(array, b+3*v, c+3*v, d+v, d+2*v, v);
    		
    		// try and resolve halves
    		nels(array, a+2*v, a+3*v, b, b+v, v);
    		nels(array, c+2*v, c+3*v, d, d+v, v);
    		nels(array, b+2*v, b+3*v, c, c+v, v);

    		// bose-nelson on fours
    		nels(array, a, a+2*v, c, c+2*v, v);
    		nels(array, a+v, a+3*v, c+v, c+3*v, v);
    		nels(array, a+v, a+2*v, c+v, c+2*v, v);
    		nels(array, b, b+2*v, d, d+2*v, v);
    		nels(array, b+v, b+3*v, d+v, d+3*v, v);
    		nels(array, b+v, b+2*v, d+v, d+2*v, v);
    	}
    }
    
    private void s(int[] array, int a, int b) {
		int m = (b - a) / 4;
    	if(m > 1) {
    		s(array, a, a+m);
    		s(array, a+m, a+2*m);
    		s(array, a+2*m, b-m);
    		s(array, b-m, b);
    	}
    	if(b - a > 1)
    		nels(array, a, a+m, a+2*m, b-m, m);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	int v = 1;
    	while(v < length) v *= 4;
    	c = length;
    	s(array, 0, v);
    }
}