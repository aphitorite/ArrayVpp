package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 * 
MIT License

Copyright (c) 2026 aphitorite

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

final public class OptimizedSlowSort extends Sort {
    public OptimizedSlowSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Optimized Slow");
        this.setRunAllSortsName("Optimized Slowsort");
        this.setRunSortName("Optimized Slowsort");
        this.setCategory("Exchange Sorts");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
	
	private void compSwap(int[] array, int a, int b) {
		if(Reads.compareIndices(array, a, b, 0.5, true) > 0)
			Writes.swap(array, a, b, 0.5, false, false);
	}
	
	private void swapRight(int[] array, int a, int b) {
		while(b-a > 1) this.compSwap(array, b-2, --b);
	}
	private void swapLeft(int[] array, int a, int b) {
		if(b-a < 2) return;
		
		int m = (a+b)/2;
		
		this.swapLeft(array, a, m);
		this.compSwap(array, m-1, b-1);
		this.swapLeft(array, m-1, b-1);
	}
	
	private void slowMerge(int[] array, int a, int b) {
		while(b-a > 1) {
			int m = (a+b)/2;
			
			this.compSwap(array, m-1, --b);
			
			if((b-a)%2 == 0)
				this.swapRight(array, a, m);
			else
				this.swapLeft(array, m-1, b);
		}
	}
	
	private void optiSlowsort(int[] array, int a, int b) {
		if(b-a < 2) return;
		
		int m = (a+b)/2;
		
		this.optiSlowsort(array, a, m);
		this.optiSlowsort(array, m, b);
		this.slowMerge(array, a, b);
	}

    @Override
    public void runSort(int[] array, int n, int bucketCount) throws Exception {
		this.optiSlowsort(array, 0, n);
    }
}