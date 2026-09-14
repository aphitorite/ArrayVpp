package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.distributions.templates.Distribution;

// @Meme Man#3121
public final class AssSort extends Sort {
	public AssSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Ass");
		this.setRunAllSortsName("Ass Sort");
		this.setRunSortName("Ass Sort");
		this.setCategory("Esoteric Sorts");
        this.setAuthors("Gooflang");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray, gooflang");
	}
	public void runSort(int[] array, int currentLength, int bucketCount) {
		Distribution distribution = arrayVisualizer.getSortAnalyzer().getDistributionById("Blancmange");
		if (distribution != null) distribution.initializeArray(array, arrayVisualizer);
	}
}