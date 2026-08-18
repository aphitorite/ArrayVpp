package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class MonoboundBubbleSort extends Sort {
    public MonoboundBubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Monobound Bubble");
        this.setRunAllSortsName("Monobound Bubble Sort");
        this.setRunSortName("Monobound Bubblesort");
        this.setCategory("Exchange Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        for(int i = length - 1; i > 0; --i) {
            for(int j = 1; j < i; ++j) {
                while(j < i && Reads.compareIndices(array, j, 0, 0.025, true) < 0){
                	++j;
                }
                if(j == i) {
                	break;
                }
                Writes.swap(array, 0, j, 0.01, true, false);
            }
            if(Reads.compareIndices(array, i, 0, 0.025, true) < 0) {
            	Writes.swap(array, 0, i, 0.01, true, false);
            } else if(i > 1) {
            	Writes.swap(array, 0, --i, 0.01, true, false);
            }
        }
    }
}