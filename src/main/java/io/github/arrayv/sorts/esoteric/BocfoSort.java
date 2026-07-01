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
 * BoCfosort does something. It's sorted.
 */
public final class BocfoSort extends BogoSorting {
    public BocfoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Bocfo");
        this.setRunAllSortsName("Bocfo Sort");
        this.setRunSortName("Bocfosort");
        this.setCategory("Esoteric Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(10);
        this.setBogoSort(true);
        this.setAuthors("Distray");
    }
    private int sqrt(int n) {
    	return (int)Math.ceil(Math.sqrt(n));
    }
    protected boolean isRangeMaybePartitioned(int[] array, int start, int pivot, int end, int luck) {
        for (int i = start; i < pivot; i++) {
            if ((randInt(0, 100) >= luck) ^ Reads.compareIndices(array, i, pivot, this.delay, true) > 0)
                return false;
        }
        for (int i = pivot + 1; i < end; i++) {
            if ((randInt(0, 100) >= luck) ^ Reads.compareIndices(array, pivot, i, this.delay, true) > 0)
                return false;
        }
        return true;
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        while(!this.isArraySorted(array, length)) {
        	for(int i=length; i>sqrt(length);) {
        		for(int j=0; j<i-sqrt(i); j++) {
        			Writes.reversal(array, j, randInt(i-sqrt(i), i), 0.1, true, false);
        		}
        		for(int j=i+1; j<length; j++) {
        			Writes.multiSwap(array, j, randInt(i, j), 0.1, true, false);
        		}
        		if(randInt(0, i) != 0 || !isRangeMaybePartitioned(array, 0, i-sqrt(i), i, (99*i)/length)) {
        			for(int j=0; j<i; j++) {
        				Writes.swap(array, j, randInt(0, i), 1, true, false); // cry about it
        			}
        			i = Math.min(i+sqrt(i), length);
        		} else {
        			i -= sqrt(i);
        		}
        	}
        }
    }
}
