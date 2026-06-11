package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

final public class SantaSort extends BogoSorting {
    public SantaSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Santa");
        this.setRunAllSortsName("Santa Sort");
        this.setRunSortName("Santasort");
        this.setCategory("Impractical Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    private void comp(int[] array, int start, int end) {
    	if(Reads.compareIndices(array, start, end, 0.5, true) == 1) {
    		Writes.swap(array, start, end, 0.5, false, false);
    	}
    	// optimized santa
    	// if(start > end) {
    	// 	Writes.swap(array, start, end, 0.5, false, false);
    	// }
    }
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	int walk = 0;
    	do {
    		while(randInt(0, currentLength) != 0) {
    			boolean goRight = randBoolean();
    			this.comp(array, walk % currentLength, (walk + 1) % currentLength);
    			if(goRight)
    				walk++;
    			else {
    				walk--;
    				if(walk < 0)
    					walk += currentLength;
    			}
    		}
    	} while(!isArraySorted(array, currentLength));
    }
}