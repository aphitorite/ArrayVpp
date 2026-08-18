package io.github.arrayv.sorts.concurrent;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class DupeSort extends Sort {

    public DupeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Dupe");
        this.setRunAllSortsName("Dupe Sorting Network");
        this.setRunSortName("Dupe Sorting Network");
        this.setCategory("Concurrent Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log^2 n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private void compSwap(int[] array, int a, int b) {
    	if(a < b && Reads.compareIndices(array, a, b, 1, true) > 0) {
    		Writes.swap(array, a, b, 1, true, false);
    	}
    }
    
    private int bitreverse(int val, int log) {
    	int v = 0;
    	while(log > 0) {
    		v |= ((val & 1) << --log);
    		val >>= 1;
    	}
    	return v;
    }
	
    @Override
    public void runSort(int[] array, int length, int bucketCount) throws Exception {
		for(int i = 1, p = 0, q = 0;; i++) {
			if(2<<p==i) {q=++p;if(1<<p>=2*length)break;}
			if(i==((1<<p)|(1<<(p-q)))){if(q--!=p)continue;}
			int k = bitreverse(i, p);
			for(int j = 0; j < length; j++) {
				if((j^k)<length && (j^k)-j == 1<<q) compSwap(array, j, j^k);
			}
		}
    }
}