package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class SoupSort extends BogoSorting {
	public SoupSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Soup");
		this.setRunAllSortsName("Soup Sort");
		this.setRunSortName("Soupsort");
		this.setCategory("Exchange Sorts");
		this.setAuthors("Potassium, soup can");
		this.setConstant("n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}

	
	public void runSort(int[] array, int length, int bucketCount) {
		for (int i = 0; i < length - 1; i++) {
			if (randBoolean()) {
				Writes.swap(array, i, i + 1, 0.5D, true, false);
			}
			if (randInt(1, 100) == 1)
				return; 
		} 
	}
}