package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 *
MIT No Attribution

Copyright (c) 2025 Distray

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

public final class LessModuloMergeSort extends Sort {
    public LessModuloMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Less Modulo Merge");
        this.setRunAllSortsName("Less Modulo Merge Sort");
        this.setRunSortName("Mergesort");
        this.setCategory("Merge Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    private int log(int n) {
    	return 32-Integer.numberOfLeadingZeros(n-1);
    }
    private int sqlog(int n) {
    	return (int)Math.ceil(Math.sqrt(log(n)));
    }
    private void evict(int[] array, int idx, int bit, int mask, int msh) {
    	Writes.write(array, idx, ((array[idx] & ~(mask << msh)) % bit) | ((array[idx] / bit) << msh), 1, true, false);
    }
    private void populate(int[] array, int idx, int val, int bit) {
    	Writes.write(array, idx, (array[idx] % bit) | (val * bit), 1, true, false);
    }
    private void merge(int[] array, int a, int m, int b, int n) {
    	int s = sqlog(n)/2+1, bit = 1 << log(n);
    	int v = (1 << s) - 1, vs = 0;
    	for(int vd = v; vd > 0; vs += s, vd = (vd << s) % bit) {
    		int l = a, r = m, t = a;
    		while(l < m || r < b) {
    			int vl = (array[l] % bit) >> vs, vr = (array[r] % bit) >> vs;
    			int d = r == b || (l < m && Reads.compareValues(vl, vr) <= 0) ? l++ : r++,
    				dv = d < m ? vl : vr;
				if(vs > 0) evict(array, t, bit, v, vs-s);
				populate(array, t++, dv & v, bit);
    		}
    	}
    	for(int i = a; i < b; i++) {
			evict(array, i, bit, v, vs-s);
    	}
    }
    private void mergeSort(int[] array, int a, int b, int n) {
    	int m = a + (b - a) / 2;
    	if(a < m) {
    		mergeSort(array, a, m, n);
    		mergeSort(array, m, b, n);
    		merge(array, a, m, b, n);
    	}
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	mergeSort(array, 0, length, length);
    }
}
