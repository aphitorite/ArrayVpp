package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.PiaeSorting;

//i'm_old!
public final class PiaeSort extends PiaeSorting {
	public PiaeSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
     
		this.setSortListName("Piae");
		this.setRunAllSortsName("Piae Sort");
		this.setRunSortName("Piae Sort");
		this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}

	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) {
		this.piae(array, 0, sortLength);
	}
}