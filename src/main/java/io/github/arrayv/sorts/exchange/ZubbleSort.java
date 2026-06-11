package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class ZubbleSort extends Sort {
    public ZubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Zubble");
        this.setRunAllSortsName("Zubble Sort");
        this.setRunSortName("Zubblesort");
        this.setCategory("Exchange Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        for(int i = length - 1; i > 0; i--) {
            boolean sorted = true;
            for(int j = 0; j < i; j++) {
            	int k = j;
            	boolean swap = false;
                while(j < i && Reads.compareIndices(array, k, j+1, 0.01, true) > 0) {
                	j++;
                    sorted = false;
                    swap = true;
                }
                if(swap) Writes.swap(array, k, j, 1, true, false);
            }
            if(sorted) break;
        }
    }
}