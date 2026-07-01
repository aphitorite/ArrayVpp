package io.github.arrayv.sorts.quick;

import io.github.arrayv.sorts.templates.Sort;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;

final public class JugglingQuickSort extends Sort {
    public JugglingQuickSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Juggling Quick");
        this.setRunAllSortsName("Juggling Quick Sort");
        this.setRunSortName("Juggling Quicksort");
        this.setCategory("Quick Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
	
	private void pass(int[] array, int left, int right) {
		int a = left, b;
		while(left < right) {
	        b = right;
	        while(a < b) {
	        	while(a < b && Reads.compareIndices(array, a, b, 0.25, true) <= 0) {
	        		b--;
	        	}
	        	if(a < b) {
	        		Highlights.clearColor(a);
	        		Highlights.colorCode(b, "pivot");
		    		Writes.swap(array, a, b, 1, true, false);
	        	}
	        	while(a == b || (a < b && Reads.compareIndices(array, a, b, 0.25, true) <= 0)) {
	        		a++;
	        	}
	        	if(a < b) {
	        		Highlights.clearColor(b);
	        		Highlights.colorCode(a, "pivot");
		    		Writes.swap(array, a, b, 1, true, false);
	        	} else break;
	        }
	        pass(array, left, --b);
	        left = a;
		}
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	Highlights.retainColorMarks(true);
    	Highlights.defineColor("pivot", Color.MAGENTA);
    	this.pass(array, 0, length-1);
    }
}