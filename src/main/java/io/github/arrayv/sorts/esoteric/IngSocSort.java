package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class IngSocSort extends Sort {

    public IngSocSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("1984");
        this.setRunAllSortsName("English Socialism Sort");
        this.setRunSortName("Ingsort");
        this.setCategory("Esoteric Sorts");
        this.setAuthors("Potassium");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    public void pull(int[] array, int start, int end) {
    	if(start > end) {
    		for(int i = start; i > end + 1; i--) {
    			Writes.swap(array, i, i - 1, 0.25, true, false);
    		}
    	}
    	if(end > start) {
    		for(int i = start; i < end + 1; i++) {
    			Writes.swap(array, i, i + 1, 0.25, true, false);
    		}
    	}
    }
    
    private boolean isArraySorted(int[] array, int currentLength) {
        for (int i = 0; i < currentLength; ++i) {
            if (Reads.compareIndices(array, i, i + 1, 0.1, true) > 0) {
            	Highlights.incrementFancyFinishPosition();
                return false;
            }

        }
        return true;
    }
    
    // love me some newspeak <Distay>
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	// Removes wrongthink.
    	while(!isArraySorted(array, currentLength)) {
            for(int i = currentLength - 1; i > 0; i--) {
            	if(Reads.compareValues(array[i], array[i - 1]) < 0 ) {
            		pull(array, i - 1, currentLength);
            		currentLength--;
                    arrayVisualizer.setCurrentLength(currentLength);
            	}
            	for(int j = 0; j < i; j++) {
            		Writes.write(array, j, (int)(array[j] / 1.0125), 0.25, true, false);
            	}
            	for(int j = i; j < currentLength; j++) {
            		Writes.write(array, j, (int)(array[j] / 1.00625), 0.25, true, false);
            	}
            	if (isArraySorted(array, currentLength)) break;
            }
    	}
    }
}