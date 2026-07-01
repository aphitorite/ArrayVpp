package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class PickySort extends Sort {
	public PickySort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Picky");
		this.setRunAllSortsName("Picky Sort");
		this.setRunSortName("Picky Sort");
		this.setCategory("Esoteric Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Potassium");
	}

	
	public void runSort(int[] array, int length, int bucketCount) {
		for (int i = 0; i < length - 1; i++) {
			int lowestindex = i;
			
			for (int j = i + 1; j < length; j++) {
				this.Highlights.markArray(2, j);
				this.Delays.sleep(0.01D);
				
				if (this.Reads.compareValues(array[j], array[lowestindex]) == -1) {
					lowestindex = j;
					this.Highlights.markArray(1, lowestindex);
					this.Delays.sleep(0.01D);
				} 
			} 
			this.Writes.reversal(array, i, lowestindex, 0.25D, true, false);
		} 
	}
}