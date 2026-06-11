package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

/*
 * 
The MIT License (MIT)

Copyright (c) 2025 aphitorite

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

//refactored version of original implementation by @Piotr Grochowski (in place merge 2)
final public class BridgeSwapMergeSort extends Sort {
    public BridgeSwapMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Bridge-Swap Merge");
        this.setRunAllSortsName("Bridge-Swap Merge Sort");
        this.setRunSortName("Bridge-Swap Mergesort");
        this.setCategory("Hybrid Sorts");
        this.setConstant("n log^2 n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
		this.setQuestion("Set buffer size (default: 32, min: 3)", 32);
        this.setAuthors("aphitorite");
    }
	
	private void bridge_smallsort(int[] array, int a, int n) {
		BinaryInsertionSort smallsort = new BinaryInsertionSort(this.arrayVisualizer);
		smallsort.customBinaryInsert(array, a, a+n, 0.25);
	}
	
	private void bridge_merge_left(int[] array, int[] swap, int a, int l, int r) {
		int m = a+l, s = 0;
		int i = 0, j = 0;
		
		Writes.arraycopy(array, a, swap, s, l, 0.5, true, true);
		
		while(i < l && j < r) {
			Highlights.markArray(2, m+j);
			Delays.sleep(0.5);
			
			if(Reads.compareValues(swap[s+i], array[m+j]) <= 0)
				Writes.write(array, a++, swap[s+(i++)], 0.5, true, false);
			else
				Writes.write(array, a++, array[m+(j++)], 0.5, true, false);
		}
		Highlights.clearMark(2);
		while(i < l) Writes.write(array, a++, swap[s+(i++)], 0.5, true, false);
	}
	
	private void bridge_merge_right(int[] array, int[] swap, int a, int l, int r) {
		int b = a+l+r, s = 0;
		int i = l, j = r;
		
		Writes.arraycopy(array, a+l, swap, s, r, 0.5, true, true);
		
		while(i > 0 && j > 0) {
			Highlights.markArray(2, a+i-1);
			Delays.sleep(0.5);
			
			if(Reads.compareValues(swap[s+j-1], array[a+i-1]) >= 0)
				Writes.write(array, --b, swap[s+(--j)], 0.5, true, false);
			else
				Writes.write(array, --b, array[a+(--i)], 0.5, true, false);
		}
		Highlights.clearMark(2);
		while(j > 0) Writes.write(array, --b, swap[s+(--j)], 0.5, true, false);
	}
	
	// searches: [* * * *][x * * * *]
	//           <-bSize-> ^m
	private int bridge_bin_search(int[] array, int m, int bSize) {
		int bot, top, mid;
		
		bot = 0;
		top = bSize;
		
		while(bot < top) {
			mid = bot + (top-bot)/2;
			
			if(Reads.compareIndices(array, m-1-mid, m+1+mid, 0.5, true) > 0)
				bot = mid+1;
			else
				top = mid;
		}
		return bot;
	}
	   
	// [0 0 0 0][1 1 1 1 1] -> [1 1 1 1 1][0 0 0 0]
	// <-bSize-> ^m
	private void bridge_swap_left(int[] array, int m, int bSize) {
		int i;
		int b, t;
		
		b = m + bSize;
		t = array[m];
		
		for(i = bSize; i > 0; i--) {
			Highlights.markArray(1, m);
			Writes.write(array, m--, array[b], 0.5, false, false);
			Highlights.markArray(2, b);
			Writes.write(array, b--, array[m], 0.5, false, false);
		}
		Highlights.clearMark(2);
		Writes.write(array, m, t, 0.5, true, false);
	}
	
	// [0 0 0 0 0][1 1 1 1] -> [1 1 1 1][0 0 0 0 0]
	//         m^ <-bSize->
	private void bridge_swap_right(int[] array, int m, int bSize) {
		int i;
		int a, t;
		
		a = m - bSize;
		t = array[m];
		
		for(i = bSize; i > 0; i--) {
			Highlights.markArray(1, m);
			Writes.write(array, m++, array[a], 0.5, false, false);
			Highlights.markArray(2, a);
			Writes.write(array, a++, array[m], 0.5, false, false);
		}
		Highlights.clearMark(2);
		Writes.write(array, m, t, 0.5, true, false);
	}
	
	private void bridge_swap_merge(int[] array, int[] swap, int a, int l, int r) {
		int mSize, m1, m2;
		
		while(true) {
			if(l < r) {
				if(l <= swap.length) {
					bridge_merge_left(array, swap, a, l, r);
					return;
				}
				mSize = bridge_bin_search(array, a+l, l);
				m1 = mSize+1;
				m2 = mSize;
				
				bridge_swap_left(array, a+l, mSize);
				bridge_swap_merge(array, swap, a, l-m2, m1);
				
				a += l-m2+m1;
				l = m2;
				r -= m1;
			}
			else {
				if(r <= swap.length) {
					bridge_merge_right(array, swap, a, l, r);
					return;
				}
				mSize = bridge_bin_search(array, a+l-1, r - (l == r ? 1 : 0));
				m1 = mSize;
				m2 = mSize+1;
				
				bridge_swap_right(array, a+l-1, mSize);
				bridge_swap_merge(array, swap, a+l-m2+m1, m2, r-m1);
				
				l -= m2;
				r = m1;
			}
		}
	}
	
	private void bridge_swap_mergesort(int[] array, int a, int n, int s) {
		int i, j, minrun;
		int[] swap = Writes.createExternalArray(s);
		
		for(minrun = n; minrun >= 32; minrun = (minrun+1)/2);
		
		for(i = 0; i+minrun < n; i += minrun)
			bridge_smallsort(array, a+i, minrun);
		bridge_smallsort(array, a+i, n-i);
		
		for(j = minrun; j < n; j *= 2) {
			for(i = 0; i+2*j < n; i += 2*j)
				bridge_swap_merge(array, swap, a+i, j, j);
			if(i+j < n)
				bridge_swap_merge(array, swap, a+i, j, n-i-j);
		}
		Writes.deleteExternalArray(swap);
	}
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
		bridge_swap_mergesort(array, 0, length, Math.max(3, bucketCount));
    }
}
