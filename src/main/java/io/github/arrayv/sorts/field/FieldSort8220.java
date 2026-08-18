package io.github.arrayv.sorts.field;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class FieldSort8220 extends Sort {
	public FieldSort8220(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Optimized 8220 Field");
		this.setRunAllSortsName("Optimized 8220 Field Sort");
		this.setRunSortName("Optimized 8220 Fieldsort");
		this.setCategory("Hybrid Sorts");
		this.setAuthors("Potassium");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	public void runSort(int[] array, int currentLength, int bucketCount) {
		OptimizedFieldSort sort = new OptimizedFieldSort(arrayVisualizer);
		sort.initFieldSort(array, 0, currentLength - 1, currentLength, 8, 2, 2, 0, 0);
	}
}