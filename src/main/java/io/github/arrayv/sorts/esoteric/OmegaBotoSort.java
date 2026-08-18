package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

/**
 * Omega Botosort is coming after your family, and you soon after.
 * When you start it, you have a 5 second headstart, and if
 * you don't take it, your survival will become nothing but
 * pure luck. Omega Botosort knows where you are at all times,
 * and it's a matter of slowing the inevitable.
 * 
 * Best case: O(n) ops
 * Average case: O((n*(n-1))!) ops?
 * Worst case: O(inf)
 */
public final class OmegaBotoSort extends BogoSorting {
    public OmegaBotoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Omega Boto");
        this.setRunAllSortsName("Omega Boto Sort");
        this.setRunSortName("\u03A9 Botosort");
        this.setCategory("Bogo Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(5);
        this.setBogoSort(true);
    }
    
    /** Pushes start to end **/
    private void omegaPush(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		Writes.multiSwap(array, end-1, start, 0.001, true, false);
    	}
    }

    /** Pushes end to start **/
    private void omegaPushRev(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		Writes.multiSwap(array, start, end-1, 0.001, true, false);
    	}
    }
    
    private void omegaSwap(int[] array, int start, int end) {
    	if(start >= end)
    		return;
    	this.omegaPush(array, start, end+1);
    	this.omegaPushRev(array, start, end);
    	this.omegaSwap(array, start+1, end-1);
    	this.omegaSwap(array, start+1, end-1);
    }
    
    public void omegaOmegaReversal(int[] array, int a, int b) {
        this.omegaSwap(array, a, b);
        
        for(int i = a + 1; i < a + ((b - a + 1) / 2); i++) {
            this.omegaSwap(array, i, a + b - i);
            omegaOmegaReversal(array, i + 1, a + b - i - 1);
        }
    }
    
    public void nOmegaReversal(int[] array, int order, int a, int b) {
    	if(a > b)
    		return;
    	if(order <= 0) {
    		Writes.swap(array, a, b, 1, true, false);
    		for(int i = a + 1; i < a + ((b - a + 1) / 2); i++) {
    			Writes.swap(array, i, a+b-i, 1, true, false);
                nOmegaReversal(array, 0, i + 1, a + b - i - 1);
                murderCheck(array, 1, a+b-i-1);
            }
    	} else if(order == 1) {
    		this.omegaOmegaReversal(array, a, b);
            murderCheck(array, 2, b);
    	} else {
    		this.nOmegaReversal(array, order-1, a, b);
    		for(int i=1; i<(b-a+1)/2; i++)
    			this.nOmegaReversal(array, order, a+i+1, b-i-1);
    	}
    }
    private void murderCheck(int[] array, int order, int length) {
    	if(order == 0 || length<2)
    		return;
    	int r1 = randInt(0, length),
    		r2 = randInt(r1, length);
    	for(int i=0; i<r2-r1; i++)
    		for(int j=0; j<order*(i+1); j++)
    			for(int k=0; k<length*(j+1); k++) {
    				this.murderCheck(array, order-1, length);
    				this.murderCheck(array, order, length-1);
    			}
    	if(Reads.compareValues(array[r1], array[r2]) == 1) {
    		this.nOmegaReversal(array, order, r1, r2);
    		this.murderCheck(array, order, length-1);
    		this.murderCheck(array, order, r2-1);
    		this.murderCheck(array, order, r1-1);
    	}
    	for(int i=0; i<r2-r1; i++)
    		for(int j=0; j<order*(i+1); j++)
    			for(int k=0; k<length*(j+1); k++) {
    				this.murderCheck(array, order-1, length);
    				this.murderCheck(array, order, length-1);
    			}
    	
    	this.nOmegaReversal(array, order, 0, length-1);
		this.murderCheck(array, order, length-1);
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        while(!this.isArraySorted(array, length))
        	this.murderCheck(array, length-1, length);
    }
}
