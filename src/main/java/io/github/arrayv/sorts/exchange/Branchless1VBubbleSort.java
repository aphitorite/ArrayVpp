package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Timer;

final public class Branchless1VBubbleSort extends Sort {
	private Timer Timer;
    public Branchless1VBubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.Timer = arrayVisualizer.getTimer();
        this.setSortListName("Branchless Single Bubble");
        this.setRunAllSortsName("Branchless Single Bubble Sort");
        this.setRunSortName("Branchless Single Bubblesort");
        this.setCategory("Exchange Sorts");
        this.setAuthors("Distray");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private int cmpClock(int[] k, int a, int b) {
    	return ((k[b]-k[a]) >> 31) * ((a - b) >>> 31);
    }
    // Branchless Double Bubble allocation improvement: Down from 2 variables to 1
    
    private void compSwap(int[] a, int l, int r) {
    	Reads.addComparison();
    	Timer.startLap("Compare");
    	cmpClock(a, l, r);
    	Timer.stopLap();

    	Writes.write(a, l, a[l] ^ ((((a[r]-a[l]) >> 31) * ((l - r) >>> 31)) & a[r]), 0.025, true, false);
    	Writes.write(a, r, a[r] ^ ((((a[r]-a[l]) >> 31) * ((l - r) >>> 31)) & a[l]), 0.025, true, false);
    	Writes.write(a, l, a[l] ^ ((((a[r]-a[l]) >> 31) * ((l - r) >>> 31)) & a[r]), 0.025, true, false);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        for(int i = 0; i <= length * (length - 1);) {
        	compSwap(array, i % length, ++i % length);
        }
    }
}