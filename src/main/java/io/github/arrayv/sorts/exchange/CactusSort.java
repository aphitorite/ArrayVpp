package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

final public class CactusSort extends BogoSorting {
    public CactusSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Cactus");
        this.setRunAllSortsName("Cactus Sort");
        this.setRunSortName("Cactus Sort");
        this.setCategory("Exchange Sorts");
		this.setAuthors("Distray");
        this.setConstant("n^3");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private int find(int[] array, int a, int b, int k) {
    	int min = -1;
    	for(int i = a; i < b; i++) {
    		if(Reads.compareIndices(array, i, k, 0.1, true) > 0 && (min == -1 || Reads.compareIndices(array, min, i, 0.1, true) > 0)) {
    			min = i;
    		}
    	}
    	return min;
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	int R = currentLength - 1;
    	while(R > 0) {
	    	boolean ws;
	    	do {
	    		ws = false;
    			int v = find(array, 0, R, R);
    			if(v != -1) {
    				ws = true;
    				if(v > 0 && Reads.compareIndices(array, v-1, R, 0.01, true) != 0)
    					Writes.swap(array, R, v-1, 0.5, true, false);
    				else {
    					int i = v;
    					do {
        					Writes.swap(array, R, i++, 0.5, true, false);
    					} while(i < R && Reads.compareIndices(array, i, R, 0.01, true) >= 0);
    					/*if(Reads.compareIndices(array, i-1, R, 0.01, true) == 0) {
        					Writes.swap(array, R, i, 0.5, true, false);
    					}*/
    				}
    			}
	    	} while(ws);
	    	R--;
    	}
    }
}