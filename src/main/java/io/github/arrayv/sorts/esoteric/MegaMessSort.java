package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class MegaMessSort extends BogoSorting {
	public MegaMessSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Mega Mess");
		this.setRunAllSortsName("Mega Mess Sort");
		this.setRunSortName("Mega Mess Sort");
		this.setCategory("Esoteric Sorts");
		this.setAuthors("Potassium");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(1024);
		this.setBogoSort(false);
	}

	public void runSort(int[] array, int length, int bucketCount) {
		while (!isRangeSorted(array, 0, length, false, true)) {
			int index1 = randInt(1, length - 1);
			int index2 = randInt(index1, length);
			int index3 =  randInt(0, index1);
			if (this.Reads.compareIndices(array, index1, index2, 0.075D, true) > 0)
				this.Writes.swap(array, index1, index2, 0.075D, true, false); 
			if (this.Reads.compareIndices(array, index1, index3, 0.075D, true) < 0)
				this.Writes.swap(array, index1, index3, 0.075D, true, false); 
		} 
	}
}