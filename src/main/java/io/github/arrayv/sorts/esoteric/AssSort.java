package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Distributions;

// @Meme Man#3121
public final class AssSort extends Sort {
	public AssSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Ass");
		this.setRunAllSortsName("Ass Sort");
		this.setRunSortName("Ass Sort");
		this.setCategory("Esoteric Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray, gooflang");
	}
	public void runSort(int[] array, int currentLength, int bucketCount) {
		Distributions.BLANCMANGE.initializeArray(array, arrayVisualizer);
	}
}