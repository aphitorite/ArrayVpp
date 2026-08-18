package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class TerrorismSort extends Sort {
	public TerrorismSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Terrorism");
		this.setRunAllSortsName("Terrorism Sort");
		this.setRunSortName("Terrorismsort");
		this.setCategory("Esoteric Sorts");
        this.setAuthors("Distray");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(32);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}

	private void horror(int[] a, int i, int j, int k, int d) {
		if(i >= k || j < 0 || i < 0)
			return;
		
		if (i != j && Reads.compareValues(a[i], a[j]) == -1) {
			Writes.swap(a, i, j, 0.025, true, false);
		}
		this.Delays.sleep(0.005D);
		Writes.recordDepth(d++);
		this.Highlights.markArray(1, i);
		this.Highlights.markArray(2, j);
		Writes.recursion(2);
		this.horror(a, i+1, j, k, d);
		this.horror(a, i, j-1, k, d);
	}

	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) {
		horror(array, 0, sortLength-1, sortLength, 0);
	}

}
