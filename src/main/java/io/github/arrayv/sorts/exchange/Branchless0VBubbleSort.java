package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Timer;

final public class Branchless0VBubbleSort extends Sort {
	private Timer Timer;
    public Branchless0VBubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.Timer = arrayVisualizer.getTimer();
        this.setSortListName("Branchless Null Bubble");
        this.setRunAllSortsName("Branchless Null Bubble Sort");
        this.setRunSortName("Branchless Null Bubblesort");
        this.setCategory("Exchange Sorts");
        this.setAuthors("Distray");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    // Branchless Double Bubble allocation improvement: Down from 1 variable to 0, by abusing arguments and widening
    
    private void compSwap(int[] a, int l, int r) {
    	Reads.addComparison();
    	Timer.startLap("Compare");
    	r -= (a[l+1]-a[l]) >> 31;
    	Timer.stopLap();

    	Writes.write(a, l, a[l] ^ (((l - r) >> 31) & a[r]), 0.025, true, false);
    	Writes.write(a, r, a[r] ^ (((l - r) >> 31) & a[l]), 0.025, true, false);
    	Writes.write(a, l, a[l] ^ (((l - r) >> 31) & a[r]), 0.025, true, false);
    }
    
    private void bb(int[] array, long dualLength) {
    	--dualLength;
    	dualLength = (dualLength * (dualLength+1)) | (dualLength << 32L);
        while((dualLength & 0xFFFFFFFFL) > 0) {
        	compSwap(array,
        			(int) ((--dualLength & 0xFFFFFFFFL) % (dualLength >> 32L)),
        			(int) ((dualLength & 0xFFFFFFFFL) % (dualLength >> 32L)));
        }
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	bb(array, length);
    }
}