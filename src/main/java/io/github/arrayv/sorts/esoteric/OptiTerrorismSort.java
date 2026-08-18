package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.m;

public final class OptiTerrorismSort extends Sort {
	public OptiTerrorismSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Optiterrorism");
		this.setRunAllSortsName("Optimized Terrorism Sort");
		this.setRunSortName("Optimized Terrorismsort");
		this.setCategory("Esoteric Sorts");
        this.setAuthors("Distray");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(32);
		this.setBogoSort(false);
	}

	private void horror(int[] a, int i, int j, int k) {
		if(i >= k || j < 0 || i < 0)
			return;
		
		if (i != j && Reads.compareValues(a[i], a[j]) == 1) {
			Writes.swap(a, i, j, 0.025, true, false);
		}
		this.Delays.sleep(0.005D);

		this.Highlights.markArray(1, i);
		this.Highlights.markArray(2, j);
		int m = j-(j-i)/2;
		if(m==j)
			return;
		this.horror(a, i+1, j, k);
		this.horror(a, i, m,  k);
	}

	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) {
		for(int i=0; i<m.flog2(sortLength); i++)
			horror(array, i, sortLength-1, sortLength);
	}

}
