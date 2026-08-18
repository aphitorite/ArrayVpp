package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class OddEvenDandelionSort extends Sort {

    public OddEvenDandelionSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        setSortListName("Odd-Even Dandelion");
        setRunAllSortsName("Odd-Even Dandelion Sort");
        setRunSortName("Odd-Even Dandelion Sort");
        setCategory("Exchange Sorts");
		setAuthors("Distray");
		setConstant("n^2");
        setBucketSort(false);
        setRadixSort(false);
        setUnreasonablySlow(false);
        setUnreasonableLimit(0);
        setBogoSort(false);

    
        this.setAuthors("Distray");
}

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
    	boolean movesmade = true, last = false;
    	int start = 0;
    	while(movesmade || last) {
    		last = movesmade;
    		movesmade = false;
    		for(int i=start; i<sortLength-1; i+=2) {
    			if(Reads.compareValues(array[i], array[i+1]) == 1) {
    				Writes.swap(array, i, i+1, 1, true, false);
					movesmade = true;
    			} else if(movesmade)
    				break;
    		}
    		start^=1;
    	}
    }
}
