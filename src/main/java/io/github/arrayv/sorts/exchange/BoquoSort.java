package io.github.arrayv.sorts.exchange;

import io.github.arrayv.sorts.templates.BogoSorting;
import io.github.arrayv.main.ArrayVisualizer;

final public class BoquoSort extends BogoSorting {
    public BoquoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Boquo");
        this.setRunAllSortsName("Boquo Sort");
        this.setRunSortName("Boquosort");
        this.setCategory("Bogo Sorts");
		this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(128);
        this.setBogoSort(true);
    }
    
    private boolean isRangeLPed(int[] array, int start, int split, int pivot, int end) {
    	for (int i = start; i < split; i++) {
            if (Reads.compareIndices(array, i, pivot, this.delay, true) > 0)
                return false;
        }
        for (int i = split + 1; i < end; i++) {
            if (Reads.compareIndices(array, pivot, i, this.delay, true) > 0)
                return false;
        }
        return true;
    }
    
    private void uskc(int[] array, int start, int end) {
    	int p = randInt(start+1, end);
    	int c = Reads.compareIndices(array, start, p, 0, true);
    	if(c == 0) {
    		Writes.multiSwap(array, p, start+1, 1, true, false);
    	} else {
    		int d;
    		do {
    			p--;
    		} while((d = Reads.compareIndices(array, start, p, 0, true)) == c && p > start);
    		if(c > 0 && d != 0)
    			Writes.multiSwap(array, start, p, 1, true, false);
    		else if(p > start)
    			Writes.multiSwap(array, p, start+1, 1, true, false);
    	}
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	int i;
    	while(true) {
    		uskc(array, 0, length);
			for(i=1; i<length && !isRangeLPed(array, 1, i, 0, length); i++);
			if(i < length) {
				if(isArraySorted(array, length))
					return;
				if(isRangeReversed(array, 0, length, true, false)) {
					Writes.reversal(array, 0, length-1, 1, true, false);
					return;
				}
				Writes.swap(array, 0, randInt(1, length), 1, true, false);
			}
    	}
    }
}