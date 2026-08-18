package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.MergeSorting2;

public final class PanicSort extends MergeSorting2 {
	public PanicSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Panic");
		this.setRunAllSortsName("Panic Sort");
		this.setRunSortName("Adaptive Dependency Mergesort");
		this.setCategory("Merge Sorts");
	    this.setAuthors("Potassium");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}

	
	public void runSort(int[] array, int length, int bucketCount) {
		mergeSort2(array, 0, length, false, length, length);
	}
}