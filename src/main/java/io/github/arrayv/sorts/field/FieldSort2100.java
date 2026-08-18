package io.github.arrayv.sorts.field;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class FieldSort2100 extends Sort {
	public FieldSort2100(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Optimized 2100 Field");
		this.setRunAllSortsName("Optimized 2100 Field Sort");
		this.setRunSortName("Optimized 2100 Fieldsort");
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
		sort.initFieldSort(array, 0, currentLength - 1, currentLength, 2, 1, 0, 0, 0);
	}
}