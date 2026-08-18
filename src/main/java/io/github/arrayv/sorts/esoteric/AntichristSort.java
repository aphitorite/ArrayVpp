package io.github.arrayv.sorts.esoteric;

import java.util.Arrays;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class AntichristSort extends Sort {
	public AntichristSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Antichrist");
		this.setRunAllSortsName("Antichrist Sort");
		this.setRunSortName("Antichrist Sort");
		this.setCategory("Esoteric Sorts");
		this.setAuthors("Distray");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	private void ac(int[] array, int start, int end, int[] ords, int f) {
		if(start >= end || f >= ords.length-1)
			return;
		if(Reads.compareIndices(array, start, end, 1, true) > 0) {
			Writes.swap(array, start, end, 1, true, false);
		}
		ac(array, start, end-1, ords, Math.max(f-1, 0));
		ac(array, start+1, end, ords, Math.max(f-1, 0));
		int[] c = Arrays.copyOf(ords, ords.length);
		Writes.changeAllocAmount(c.length);
		d:
		while(true) {
			if(start+c[c.length-1] < end && Reads.compareIndices(array, start, start+c[c.length-1], 1, true) > 0) {
				Writes.swap(array, start, start+c[c.length-1], 1, true, false);
			}
			ac(array, start, end, c, f+1);
			c[0]++;
			for(int i=f; i<c.length; i++) {
				if(c[i] >= end - start) {
					if(i == c.length - 1)
						break d;
					c[i+1]++;
					c[i] = 0;
				}
			}
		}
		Writes.changeAllocAmount(-c.length);
	}
	
	public void antichrist(int[] array, int start, int end) {
		Writes.changeAllocAmount(end-start);
		ac(array, start, end, new int[end-start], 0);
		Writes.changeAllocAmount(start-end);
	}
	public void runSort(int[] array, int currentLength, int bucketCount) {
		antichrist(array, 0, currentLength);
	}
}