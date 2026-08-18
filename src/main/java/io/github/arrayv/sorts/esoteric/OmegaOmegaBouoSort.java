package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

/*
 * 
MIT License

Copyright (c) 2019 w0rthy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

/**
 * Bouosort selectively exchanges, then reverses the array until sorted
 */
public final class OmegaOmegaBouoSort extends BogoSorting {
    public OmegaOmegaBouoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Omega Omega Bouo");
        this.setRunAllSortsName("Omega Omega Bouo Sort");
        this.setRunSortName("\u03A9^2 Bouosort");
        this.setCategory("Bogo Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(10);
        this.setBogoSort(true);
        this.setAuthors("w0rthy");
    }

    /** Pushes start to end **/
    private void omegaPush(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		int t = array[end-1];
    		for(int k=end-1; k>start; k--) {
    			Writes.write(array, k, array[k-1], 0.01, true, false);
    		}
    		Writes.write(array, start, t, 0.01, true, false);
    	}
    }

    /** Pushes end to start **/
    private void omegaPushRev(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		int t = array[start];
    		for(int k=start; k<end-1; k++) {
    			Writes.write(array, k, array[k+1], 0.01, true, false);
    		}
    		Writes.write(array, end-1, t, 0.01, true, false);
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
            }
    	} else if(order == 1) {
    		this.omegaOmegaReversal(array, a, b);
    	} else {
    		this.nOmegaReversal(array, order-1, a, b);
    		for(int i=1; i<(b-a+1)/2; i++)
    			this.nOmegaReversal(array, order, a+i+1, b-i-1);
    	}
    }
    
    private void omega2BogSwap(int[] array, int length) {
    	if(length < 2)
    		return;
    	int rand = randInt(0, length),
    		rand2 = randInt(0, length);
    	for(int i=0; i<length; i++)
    		this.omega2BogSwap(array, length-1);
    	while(Reads.compareValues(array[rand], array[rand2]) == 1) {
    		while(rand > rand2) {
        		if(Reads.compareValues(array[rand], array[rand2]) == 1)
        			this.omegaSwap(array, rand2, rand);
    			this.omega2BogSwap(array, length-1);
    			rand = randInt(0, length);
        	    rand2 = randInt(0, length);
    		}
    		if(Reads.compareValues(array[rand], array[rand2]) == 1)
    			this.omegaSwap(array, rand, rand2);
    		this.omega2BogSwap(array, length-1);
    		rand = randInt(0, length);
    	    rand2 = randInt(0, length);
    	}
    	for(int i=0; i<length; i++)
    		this.omega2BogSwap(array, length-1);
    	this.nOmegaReversal(array, length-1, 0, length-1);
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	while(!this.isArraySorted(array, length))
        	this.omega2BogSwap(array, length);
    }
}
