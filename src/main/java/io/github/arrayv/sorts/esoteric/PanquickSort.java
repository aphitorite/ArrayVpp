package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class PanquickSort extends Sort {
	public PanquickSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Panquick");
		this.setRunAllSortsName("Panquick Sort");
		this.setRunSortName("Panquick Sort");
		this.setCategory("Esoteric Sorts");
		this.setConstant("n log^2 n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Potassium");
	}


	private void quickSort(int[] a, int p, int r) {
		int pivot = p + (r - p + 1) / 2;
		int x = a[pivot];

		int i = p;
		int j = r;

		Highlights.markArray(3, pivot);

		while (i <= j) {
			while (Reads.compareValues(a[i], x) == -1) {
				i++;
				Highlights.markArray(1, i);
				Delays.sleep(0.5D);
			}
			while (Reads.compareValues(a[j], x) == 1) {
				j--;
				Highlights.markArray(2, j);
				Delays.sleep(0.5D);
			}

			if (i <= j) {

				if (i == pivot) {
					Highlights.markArray(3, j);
				}
				if (j == pivot) {
					Highlights.markArray(3, i);
				}
				Writes.reversal(a, i, j, 1.0D, true, false);

				i++;
				j--;
			}
		}

		if (p < j) {
			quickSort(a, p, j);
		}
		if (i < r) {
			quickSort(a, i, r);
		}
	}


	public void runSort(int[] array, int currentLength, int bucketCount) {
		quickSort(array, 0, currentLength - 1);
	}
}