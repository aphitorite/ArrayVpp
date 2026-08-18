package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Timer;

final public class Branchless2VBubbleSort extends Sort {
	private Timer Timer;
    public Branchless2VBubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.Timer = arrayVisualizer.getTimer();
        this.setSortListName("Branchless Double Bubble");
        this.setRunAllSortsName("Branchless Double Bubble Sort");
        this.setRunSortName("Branchless Double Bubblesort");
        this.setCategory("Exchange Sorts");
        this.setAuthors("Distray");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    // Branchless Bubble allocation improvement: Down from 4 variables to 2
    
    private boolean compSwap(int[] a, int l, int r) {
    	Reads.addComparison();
    	Timer.startLap("Compare");
    	int c = ((a[r]-a[l]) >> 31) * ((l - r) >>> 31);
    	Timer.stopLap();

    	Writes.write(a, l, a[l] ^ (c & a[r]), 0.025, true, false);
    	Writes.write(a, r, a[r] ^ (c & a[l]), 0.025, true, false);
    	Writes.write(a, l, a[l] ^ (c & a[r]), 0.025, true, false);
    	
    	return c < 0;
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        for(int i = 0; i <= length * (length - 1);) {
        	compSwap(array, i % length, ++i % length);
        }
    }
}