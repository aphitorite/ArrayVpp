package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 * 
The MIT License (MIT)

Copyright (c) 2021 aphitorite

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

final public class DualPatienceSort extends Sort {
    public DualPatienceSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Dual Patience");
        this.setRunAllSortsName("Dual Patience Sort");
        this.setRunSortName("Dual Patience Sort");
        this.setCategory("Merge Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
	
	private int pileSearch(int[] array, int a, int b, int val, boolean top) {
		int cmp = top ? 1 : -1;
		
		while(a < b) {
			int m = (a+b)/2;
			
			Highlights.markArray(2, m);
			Delays.sleep(0.5);
			
			if(Reads.compareValues(array[m], val) == cmp) 
				a = m+1;
			else 
				b = m;
		}
		
		return a;
	}
	
	private boolean keyLessThan(int[] src, int[] pa, int a, int b) {
		int cmp = Reads.compareValues(src[pa[a]], src[pa[b]]);
		return cmp < 0 || (cmp == 0 && Reads.compareOriginalValues(a, b) < 0);
	}

	private void siftDown(int[] src, int[] heap, int[] pa, int t, int r, int size) {
		while(2*r+2 < size) {
			int nxt = 2*r+1;
			int min = nxt + (this.keyLessThan(src, pa, heap[nxt], heap[nxt+1]) ? 0 : 1);

			if(this.keyLessThan(src, pa, heap[min], t)) {
				Writes.write(heap, r, heap[min], 0.5, true, true);
				r = min;
			}
			else break;
		}
		int min = 2*r+1;

		if(min < size && this.keyLessThan(src, pa, heap[min], t)) {
			Writes.write(heap, r, heap[min], 0.5, true, true);
			r = min;
		}
		Writes.write(heap, r, t, 0.5, true, true);
	}

	private void kWayMerge(int[] src, int[] dest, int[] heap, int[] pa, int[] pb, int size) {
		for(int i = 0; i < size; i++)
			Writes.write(heap, i, i, 0, false, true);

		for(int i = (size-1)/2; i >= 0; i--)
			this.siftDown(src, heap, pa, heap[i], i, size);
			
		for(int i = 0; size > 0; i++) {
			int min = heap[0];
			
			Highlights.markArray(2, i);
			Highlights.markArray(3, pa[min]);
			
			Writes.write(dest, i, src[pa[min]], 0.5, false, false);
			Writes.write(pa, min, pa[min]+1, 0, false, true);

			if(pa[min] == pb[min])
				this.siftDown(src, heap, pa, heap[--size], 0, size);
			else 
				this.siftDown(src, heap, pa, heap[0], 0, size);
		}
	}
	
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
		int[] tmp = Writes.createExternalArray(length+length%2);
		int[] loc = Writes.createExternalArray(length);
		int s = (length+1)/2;
		
		int size = 1;
		Writes.write(tmp, 0, array[0], 1, true, true);
		Writes.write(tmp, s, array[0], 1, true, true);
		
		boolean did = false;
		
		for(int i = 1; i < length; i++) {
			Highlights.markArray(1, i);
			
			int l = this.pileSearch(tmp, 0, size, array[i], true);
			
			if(l == size) {
				did = true;
				l = this.pileSearch(tmp, s, s+size, array[i], false);
				
				Writes.write(tmp, l, array[i], 1, false, true);
				
				if(l == s+size) {
					Writes.write(tmp, l-s, array[i], 1, false, true);
					size++;
				}
			}
			else Writes.write(tmp, l, array[i], 1, false, true);
			
			Writes.write(loc, i, l, 0, false, true);
		}
		Highlights.clearMark(2);
		
		int alloc = 0;
		
		if(did) {
			int[] pa   = new int[size];
			int[] pb   = new int[size];
			int[] bot  = new int[size];
			int[] top  = new int[size];
			int[] heap = new int[size];
			
			alloc = 5*size;
			Writes.changeAllocAmount(alloc);
			
			for(int i = 0; i < length; i++) {
				Highlights.markArray(1, i);
				
				int l = loc[i] < s ? loc[i] : loc[i]-s;
				
				Writes.write(pb, l, pb[l]+1, 0.5, false, true);
			}
			
			for(int i = 1; i < size; i++)
				Writes.write(pb, i, pb[i]+pb[i-1], 0, false, true);
			Writes.arraycopy(pb, 0, pa, 1, size-1, 0, false, true);
			Writes.arraycopy(pa, 0, bot, 0, size, 0, false, true);
			Writes.arraycopy(pb, 0, top, 0, size, 0, false, true);
			
			for(int i = length-1; i >= 0; i--) {
				Highlights.markArray(2, i);
				
				boolean isTop = loc[i] < s;
				int l = isTop ? loc[i] : loc[i]-s;
				
				if(isTop) {
					Writes.write(top, l, top[l]-1, 0, false, true);
					Writes.write(tmp, top[l], array[i], 1, true, true);
				}
				else {
					Writes.write(tmp, bot[l], array[i], 1, true, true);
					Writes.write(bot, l, bot[l]+1, 0, false, true);
				}
			}
			
			if(size > 1) this.kWayMerge(tmp, array, heap, pa, pb, size);
			else         Writes.arraycopy(tmp, 0, array, 0, length, 1, true, false);
		}
		
		Writes.deleteExternalArray(tmp);
		Writes.deleteExternalArray(loc);
		Writes.changeAllocAmount(-alloc);
    }
}