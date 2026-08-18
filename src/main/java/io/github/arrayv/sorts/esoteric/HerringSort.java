package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

final public class HerringSort extends BogoSorting {

	// Improved Horror Sort
    public HerringSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Herring");
        this.setRunAllSortsName("Herring Sort");
        this.setRunSortName("Herringsort");
        this.setCategory("Esoteric Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private int CPO2LT(int l) {
    	int z=1;
    	while(z < l)
    		z<<=1;
    	return z>>1;
    }
    private int CPO3LT(int l) {
    	int z=1;
    	while(z < l)
    		z*=3;
    	return z/3;
    }
    private void bitComp(int[] arr, int a, int b, boolean d) throws StackOverflowError {
    	if(d == (Reads.compareValues(arr[a], arr[b]) == 1)) {
    		Writes.swap(arr, a, b, 1, true, false);
    	}
    }
    private void bitMerge(int[] arr, int a, int b, boolean d) throws StackOverflowError {
    	if(b < 2)
    		return;
    	int m = CPO2LT(b);
    	for(int i=0; i<b-m; i++) {
    		this.bitComp(arr, a+i, a+m+i, d);
    	}
    	if(m < 1)
    		return;
    	this.bitMerge(arr, a, b-m, d);
    	this.bitMerge(arr, a+b-m, m, d);
    	
    	return;
    }
    private void bitBitMerge(int[] arr, int a, int b, boolean d) throws StackOverflowError {
    	if(b < 2)
    		return;
    	int m = CPO2LT(b);

    	if(m < 1)
    		return;

    	this.bitMerge(arr, a, b, d);

    	this.bitBitMerge(arr, a, b-m, !d);
    	this.bitBitMerge(arr, a+b-m, m, d);

    	this.bitMerge(arr, a, b, !d);

    	this.bitBitMerge(arr, a, b-m, d);
    	this.bitBitMerge(arr, a+b-m, m, d);
    	
    	return;
    }
    
    private void bitBitBitStoogeMerge(int[] arr, int a, int b, boolean d) throws StackOverflowError {
    	if(b < 2)
    		return;
    	int m = CPO3LT(b);
    	if(m*2 >= b)
    		m /= 3;
    	if(m <= 81) {
    		this.bitBitMerge(arr, a, b, d);
    		return;
    	}
    	int t2 = (a+b)-(2*m);
		this.bitBitBitStoogeMerge(arr, a, m*2, !d);
    	this.bitBitBitStoogeMerge(arr, a+m, m*2, d);
    	this.bitBitBitStoogeMerge(arr, t2, m*2, !d);
    	this.bitBitBitStoogeMerge(arr, a+m, m*2, !d);
    	this.bitBitBitStoogeMerge(arr, a, m*2, d);
    	
    	this.bitBitMerge(arr,a,b,d);
    	
    	this.bitBitBitStoogeMerge(arr, a, m*2, d);
    	this.bitBitBitStoogeMerge(arr, a+m, m*2, d);
    	this.bitBitBitStoogeMerge(arr, t2, m*2, d);
    	this.bitBitBitStoogeMerge(arr, a+m, m*2, d);
    	this.bitBitBitStoogeMerge(arr, a, m*2, d);
    	
    	return;
    }
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	this.bitBitBitStoogeMerge(array, 0, currentLength, false);
    	this.bitBitMerge(array, 0, currentLength, false);
    }
}