package io.github.arrayv.sorts.insert;

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

public final class MergeInsertionSort5 extends Sort {
	public MergeInsertionSort5(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Merge-Insertion (Out-of-Place)");
		this.setRunAllSortsName("Merge-Insertion Sort (Out-of-Place)");
		this.setRunSortName("Merge-Insertion Sort (Out-of-Place)");
		this.setCategory("Insertion Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
		this.setConstant("n^2");
		this.setAuthors("aphitorite");
	}
	
	private int binarySearchNext(int[] array, int[] keys, int a, int b) {
		int i = b;

		while(a < b) {
			int m = (a+b)/2;
			
			if(Reads.compareIndices(array, keys[i], keys[m], 0.25, true) < 0)
				b = m;
			else
				a = m+1;
		}
		return a;
	}
	
	private void reorder(int[] keys, int[] table, int m) {
		for(int i = 0; i < m; i++)
			Writes.write(table, keys[i], m-1-i, 0.5, true, true);
		
		for(int i = 0; i < m; i++)
			while(table[table[keys[m+i]]] != i)
				Writes.swap(keys, m+i, m+table[table[keys[m+i]]], 0.5, true, true);
	}
	
	private void mergeInsertion(int[] array, int[] keys, int[] table, int n) {
		if(n < 2) return;
		
		int m = n/2;
		
		for(int i = 0; i < m; i++) {
			if(Reads.compareIndices(array, keys[i], keys[m+i], 0.5, true) > 0)
				Writes.swap(keys, i, m+i, 0.5, true, true);
			
			Highlights.clearMark(2);
			Writes.write(table, keys[m+i], keys[i], 0.5, true, true);
		}
			
		this.mergeInsertion(array, keys, table, m);
		this.reorder(keys, table, m);
		
		for(int i = m+1, j = 2, k = 4; i < n; i += j, j = k-j, k *= 2)
			Writes.reversal(keys, i, Math.min(n, i+j)-1, 1, true, true);
		
		Highlights.clearMark(2);
		double sleep = Math.min(1, 4d/n);
		
		for(int i = m+1, j = 2, k = 4, i0 = Math.max((n+1)%2, i-(k-1)), i1 = i+j; i < n; i++) {
			if(i == i1) { j = k-j; k *= 2; i0 = Math.max((n+1)%2, i-(k-1)); i1 += j; }
			
			Highlights.markArray(3, keys[i0]);
			int p = this.binarySearchNext(array, keys, i0++, i);
			
			int tk = keys[i]; // insert()
			Writes.arraycopy(keys, p, keys, p+1, i-p, sleep, true, true);
			Writes.write(keys, p, tk, sleep, true, true);
		}
		Highlights.clearMark(2);
		Highlights.clearMark(3);
	}

	private void transport(int[] array, int[] table) {
		for(int i = 0; i < table.length; i++) {
			Highlights.markArray(2, i);

			if(i != table[i]) {
				int t = array[i];
				int j = i, next = table[i];

				do {
					Writes.write(array, j, array[next], 0.5, true, false);
					Writes.write(table, j, j, 0.5, true, true);

					j = next;
					next = table[next];
				}
				while(next != i);

				Writes.write(array, j, t, 0.5, true, false);
				Writes.write(table, j, j, 0.5, true, true);
			}
		}
	}

	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		int[] keys  = Writes.createExternalArray(length);
		int[] table = Writes.createExternalArray(length);
		
		for(int i = 0; i < length; i++)
			Writes.write(keys, i, i, 0.5, true, true);
		
		this.mergeInsertion(array, keys, table, length);
		this.transport(array, keys);
		
		Writes.deleteExternalArray(keys);
		Writes.deleteExternalArray(table);
	}
}
