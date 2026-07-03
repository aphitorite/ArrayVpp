package io.github.arrayv.sorts.insert;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;

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

public final class MergeInsertionSort4 extends Sort {
	public MergeInsertionSort4(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Merge-Insertion (In-Place)");
		this.setRunAllSortsName("Merge-Insertion Sort (In-Place)");
		this.setRunSortName("Merge-Insertion Sort (In-Place)");
		this.setCategory("Insertion Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
		this.setConstant("n^2");
		this.setAuthors("aphitorite");
	}
	
	private void swap(int[] array, int a, int b, int s) {
		for(int i = a*s, j = b*s; s-- > 0; )
			Writes.swap(array, i++, j++, 0.5, true, false);
	}
	private void insert(int[] array, int a, int b, int s, double sleep) {
		for(int i = 0; i < s; i++) {
			int t = array[a*s + i];
			
			for(int j = a*s; j > b*s; j -= s)
				Writes.write(array, j + i, array[j-s + i], sleep, true, false);
			
			Writes.write(array, b*s + i, t, sleep, true, false);
		}
	}
	
	private void compSwap(int[] array, int a, int b, int s) {
		if(Reads.compareIndices(array, a*s, b*s, 0.5, true) > 0)
			this.swap(array, a, b, s);
	}
	
	private int binarySearchNext(int[] array, int a, int b, int s) {
		int i = b;
		
		while(a < b) {
			int m = a+(b-a)/2;

			if(Reads.compareIndices(array, i*s, m*s, 0.25, true) < 0)
				b = m;
			else
				a = m+1;
		}
		return a;
	}
	
	private void reversal(int[] array, int a, int b, int s) {
		while(b-a > 1) this.swap(array, a++, --b, s);
	}
	
	private void bitReversal(int[] array, int a, int b, int s) {
		int len = b-a, m = 0;
		int d1 = len>>1, d2 = d1+(d1>>1);
					
		for(int i = 1; i < len-1; i++) {
			int j = d1;
			
			for(
				int k = i, n = d2; 
				(k&1) == 0; 
				j -= n, k >>= 1, n >>= 1
			);
			m += j;
			
			if(m > i) this.swap(array, a+i, a+m, s);
		}
	}
	private void unshuffle(int[] array, int a, int b, int s) {
		int n = b-a;
		
		for(int j = a, m = 0, k = 2; n/k > 0; k *= 2) {
			if(((n/k)&1) == 1) {
				this.bitReversal(array, j, j+k, s);
				this.bitReversal(array, j, j+k/2, s);
				this.bitReversal(array, j+k/2, j+k, s);
				
				Highlights.clearMark(2);
				IndexedRotations.simpleBeaker(array, (j-m)*s, j*s, (j+k/2)*s, 0.5, true, false);

				m += k/2;
				j += k;
			}
		}
	}
	
	private void mergeInsertionSort(int[] array, int n, int s) {
		if(n < 2) return;
		
		int m = n/2;
		
		for(int i = 1; i < n; i += 2)
			this.compSwap(array, i-1, i, s);
		
		this.mergeInsertionSort(array, m, 2*s);
		this.unshuffle(array, 0, 2*m, s); // reorder()
		this.reversal(array, m, 2*m, s);
		
		for(int i = m+1, j = 2, k = 4; i < n; i += j, j = k-j, k *= 2)
			this.reversal(array, i, Math.min(n, i+j), s);
		
		Highlights.clearMark(2);
		double sleep = Math.min(0.5, 4d/n);
		
		for(int i = m+1, j = 2, k = 4, i0 = Math.max((n+1)%2, i-(k-1)), i1 = i+j; i < n; i++) {
			if(i == i1) { j = k-j; k *= 2; i0 = Math.max((n+1)%2, i-(k-1)); i1 += j; }
			
			Highlights.markArray(3, i0*s);
			int p = this.binarySearchNext(array, i0++, i, s);
			this.insert(array, i, p, s, sleep);
		}
		Highlights.clearMark(2);
		Highlights.clearMark(3);
	}

	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		this.mergeInsertionSort(array, length, 1);
	}
}