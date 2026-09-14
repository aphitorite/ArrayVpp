package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BlockInsertionSort;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class DunningKrugerSort extends BogoSorting {
	public DunningKrugerSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Dunning-Kruger");
		this.setRunAllSortsName("Dunning-Kruger Effect Sort");
		this.setRunSortName("Dunning-Kruger Sort");
		this.setCategory("Impractical Sorts");
        this.setAuthors("Potassium");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	public void pull(int[] array, int start, int end) {
		if (start > end) {
			for (int i = start; i > end + 1; i--) {
				this.Writes.swap(array, i, i - 1, 0.25D, true, false);
			}
		}
		if (end > start) {
			for (int i = start; i < end - 1; i++) {
				this.Writes.swap(array, i, i + 1, 0.25D, true, false);
			}
		}
	}


	public void runSort(int[] array, int length, int bucketCount) {
		int j = 0;
		int i = 0;
		boolean insert = true;
		for (int k = 0; k < length; k++) {
			j = 0;
			i = 0;
			while (j < length) {
				i = array[j];
				this.Writes.swap(array, j, Math.min(arrayVisualizer.getStabilityValue(array[j]), length - 1), 1.5D, true, false);
				if (this.Reads.compareValues(array[j], i) == 0) j++; 
			} 
			if (isArraySorted(array, length)) {
				insert = false;
				break;
			} 
			pull(array, 0, length);
		} 
		if (insert) {
			BlockInsertionSort sort = new BlockInsertionSort(this.arrayVisualizer);
			sort.insertionSort(array, 0, length);
		} 
	}
}