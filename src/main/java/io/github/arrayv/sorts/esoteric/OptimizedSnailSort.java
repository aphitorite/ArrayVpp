package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class OptimizedSnailSort extends Sort {
	public OptimizedSnailSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Optimized Snail");
		this.setRunAllSortsName("Optimized Snail sort");
		this.setRunSortName("Optimized Snailsort");
		this.setCategory("Esoteric Sorts");
        this.setAuthors("Potassium");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}

	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		int i = 0;
		while (i < currentLength - 2) {
			int m = i;
			if (Reads.compareValues(array[i], array[i + 2]) == 1) {
				Writes.swap(array, i, i + 2, 1.0D, true, false);
				i = 0;
			} 
			Highlights.markArray(1, m);
			Highlights.markArray(2, m + 2);
			
			if (Reads.compareValues(array[i], array[i + 1]) == 1) {
				Writes.swap(array, i, i + 1, 1.0D, true, false);
			}
			Highlights.markArray(1, m);
			Highlights.markArray(2, m + 1);
			i++;
		} 
		if (Reads.compareValues(array[currentLength - 2], array[currentLength] - 1) == 1) {
			Writes.swap(array, currentLength - 2, currentLength - 1, 1.0D, true, false);
		}
		InsertionSort sort = new InsertionSort(this.arrayVisualizer);
		sort.customInsertSort(array, 0, currentLength, 0.5D, false);
	}
}