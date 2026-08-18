package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import java.util.Random;

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

final public class ChaosSort extends Sort {
	public ChaosSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Chaos");
		this.setRunAllSortsName("Chaos Sort");
		this.setRunSortName("Chaos Sort");
		this.setCategory("Exchange Sorts");
		this.setAuthors("aphitorite");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("aphitorite");
	}

	@Override
	public void runSort(int[] array, int n, int bucketCount) {
		Random r = new Random();
		
		for(int k = 0;; k++) {
			int m = Math.min(n, (int)Math.ceil(n * Math.pow(2, 1-k/(n*10)))); // a constant of 10 seems to be enough to sort in arrayv
			
			if(m < 2) break;
			
			int i = r.nextInt(2*n-m);
			int j = r.nextInt(m-1);
			
			if(i+1+j < n) j = i+j+1;
			
			else {
				i = (i+j+1)%n;
				j = i+m-1-j;
			}
			
			if(Reads.compareIndices(array, i, j, 0.05, true) > 0)
				Writes.swap(array, i, j, 0.5, true, false);
		}
	}
}
