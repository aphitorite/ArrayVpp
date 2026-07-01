package io.github.arrayv.sorts.insert;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 *
MIT License

Copyright (c) 2023 aphitorite

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

public final class MergeInsertionSort3 extends Sort {
	public MergeInsertionSort3(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Merge-Insertion");
		this.setRunAllSortsName("Merge-Insertion Sort");
		this.setRunSortName("Merge-Insertion Sort");
		this.setCategory("Insertion Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
		this.setConstant("n^2");
		this.setAuthors("aphitorite");
	}
	
	private void dualSwap(int[] array, int[] keys, int a, int b, double sleep) {
		Writes.swap(keys, a, b, 0, false, true);
		Writes.swap(array, a, b, sleep, true, false);
	}
	
	private void insert(int[] array, int[] keys, int a, int b, double sleep) {
		int t = array[a], tk = keys[a];
		
		while(a > b) {
			Writes.write(keys, a, keys[a-1], 0, false, true);
			Writes.write(array, a, array[--a], sleep, true, false);
		}
		Writes.write(keys, b, tk, 0, false, true);
		Writes.write(array, b, t, sleep, true, false);
	}
	
	private void reversal(int[] array, int[] keys, int a, int b, double sleep) {
		while(b-a > 1) this.dualSwap(array, keys, a++, --b, sleep);
	}
	
	private int binarySearch(int[] array, int a, int b, int val) {
		while(a < b) {
			int m = (a+b)/2;
			
			Highlights.markArray(2, m);
			Delays.sleep(0.25);
			
			if(Reads.compareValues(val, array[m]) < 0)
				b = m;
			else
				a = m+1;
		}
		return a;
	}
	
	private void reorder(int[] array, int[] keys, int[] table, int m) {
		for(int i = 0; i < m; i++)
			Writes.write(table, keys[i], m-1-i, 0.5, true, true);
		
		for(int i = 0; i < m; i++)
			while(table[table[keys[m+i]]] != i)
				this.dualSwap(array, keys, m+i, m+table[table[keys[m+i]]], 0.5);
	}
	
	private void mergeInsertion(int[] array, int[] keys, int[] table, int n) {
		if(n < 2) return;
		
		int m = n/2;
		
		for(int i = 0; i < m; i++) {
			if(Reads.compareIndices(array, i, m+i, 0.5, true) > 0)
				this.dualSwap(array, keys, i, m+i, 0.5);
			
			Highlights.clearMark(2);
			Writes.write(table, keys[m+i], keys[i], 0.5, true, true);
		}
			
		this.mergeInsertion(array, keys, table, m);
		this.reorder(array, keys, table, m);
		
		for(int i = m+1, j = 2, k = 4; i < n; i += j, j = k-j, k *= 2)
			this.reversal(array, keys, i, Math.min(n, i+j), 1);
		
		Highlights.clearMark(2);
		double sleep = Math.min(1, 4d/n);
		
		for(int i = m+1, j = 2, k = 4, i0 = Math.max((n+1)%2, i-(k-1)), i1 = i+j; i < n; i++) {
			if(i == i1) { j = k-j; k *= 2; i0 = Math.max((n+1)%2, i-(k-1)); i1 += j; }
			
			Highlights.markArray(3, i0);
			int p = this.binarySearch(array, i0++, i, array[i]);
			this.insert(array, keys, i, p, sleep);
		}
		Highlights.clearMark(2);
		Highlights.clearMark(3);
	}

	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		int[] keys  = Writes.createExternalArray(length);
		int[] table = Writes.createExternalArray(length);
		
		for(int i = 0; i < length; i++)
			Writes.write(keys, i, i, 0.5, true, true);
		
		this.mergeInsertion(array, keys, table, length);
		
		Writes.deleteExternalArray(keys);
		Writes.deleteExternalArray(table);
	}
}
