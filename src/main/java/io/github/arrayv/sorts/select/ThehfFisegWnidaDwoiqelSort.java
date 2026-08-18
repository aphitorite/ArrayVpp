package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

/*._________________________.
 | As Seen On PCBoy's Oh God |
  '-------------------------' */

final public class ThehfFisegWnidaDwoiqelSort extends BogoSorting {
    public ThehfFisegWnidaDwoiqelSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Thehf Fiseg Wnida Dwoiqel");
        this.setRunAllSortsName("Thehf Fiseg Wnida Dwoiqel Sort");
        this.setRunSortName("Thehf Fiseg Wnida Dwoiqelsort");
        this.setCategory("Impractical Sorts");
  	    this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
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
    
    private boolean sift(int[] array, int a, int a1, int b) {
    	int p = (b - a) / 2, lp = 0;
    	boolean did = false;
    	while(p != lp) {
    		while(b - p > a1 && p != lp) {
        		b -= p;
        		lp = p;
        		p = (p + 1) / 2;
        		if(Reads.compareIndices(array, a1, b, 1, true) > 0) {
        			Writes.swap(array, a1, b, 1, true, false);
        			did = true;
        			a1 = b;
        			b += lp;
        		}
    		}
    		lp = p;
    		p = (p + 1) / 2;
    	}
    	return did;
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
        for(int j=0; j<currentLength/2&&!isRangeSorted(array,j,currentLength-j); j++) {
        	heap(array, j, currentLength-j);
	        for(int i=j+1; i<currentLength-j-1; i++)
	        	while(sift(array, j, i, currentLength-j));
        }
    }
}