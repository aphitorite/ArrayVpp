package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class FullDinnerSort extends Sort {
	public FullDinnerSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Full Dinner");
		this.setRunAllSortsName("Full Dinner sort");
		this.setRunSortName("Full Dinnersort");
		this.setCategory("Exchange Sorts");
        this.setAuthors("Potassium");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}

	public void snailSort(int[] array, int currentLength, int bucketCount, double gap, double gapshrink) {
		int i = 0;
		for (double l = gap; Math.floor(l * 100.0D) > 100.0D; l /= gapshrink) {
			i = 0;
			while (i < currentLength) {
				int m = i;
				if (Reads.compareValues(array[(int)(i / l)], array[i]) == 1) {
					Writes.reversal(array, (int)(i / l), i, 0.5D, true, false);
					i = 0;
				} 
				Highlights.markArray(1, m / 2);
				Highlights.markArray(2, m);
				if (i == 0) m = 0; 
				if (Reads.compareValues(array[(int)(i / l)], array[i]) == 1) {
					Writes.reversal(array, (int)(i / l), i, 0.5D, true, false);
					i = 0;
				} 
				Highlights.markArray(1, m);
				Highlights.markArray(2, m + 1);
				i++;
			}
			l /= gapshrink;
			i = currentLength;
			while (i > 0) {
				int m = i;
				if (Reads.compareValues(array[Math.min(currentLength, (int)((i + currentLength) / l))], array[i]) == -1) {
					Writes.reversal(array, Math.min(currentLength, (int)((i + currentLength) / l)), i, 0.5D, true, false);
					i = currentLength;
				} 
				Highlights.markArray(1, Math.min(currentLength, (int)((i + currentLength) / l)));
				Highlights.markArray(2, i);
				if (i == currentLength) m = currentLength; 
				if (Reads.compareValues(array[Math.min(currentLength, (int)((i + currentLength) / l))], array[i]) == -1) {
					Writes.reversal(array, Math.min(currentLength, (int)((i + currentLength) / l)), i, 0.5D, true, false);
					i = currentLength;
				} 
				Highlights.markArray(1, m);
				
				i--;
			} 
		} 
		InsertionSort sort = new InsertionSort(this.arrayVisualizer);
		sort.customInsertSort(array, 0, currentLength, 0.25D, false);
	}

	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		snailSort(array, currentLength, bucketCount, 2.0D, 1.025D);
	}
}