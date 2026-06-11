package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

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

final public class OddEvenSandpaperSort extends Sort {
    public OddEvenSandpaperSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Odd-Even Sandpaper");
        this.setRunAllSortsName("Odd-Even Sandpaper Sort");
        this.setRunSortName("Odd-Even Sandpaper Sort");
        this.setCategory("Exchange Sorts");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("w0rthy");
    }
	
	private void compSwap(int[] array, int a, int b) {
		if(Reads.compareIndices(array, a, b, 0.5, true) > 0)
			Writes.swap(array, a, b, 1, false, false);
	}
    
    @Override
    public void runSort(int[] array, int N, int bucketCount) {
		for(int i, j = 0, k = N-1; j < k; j++, k--) {
			this.compSwap(array, j, k);
			
			for(i = j+2; i < k; i += 2) {
				this.compSwap(array, i-1, i);
				this.compSwap(array, j, i-1);
				this.compSwap(array, i, k);
			}
			if(i-1 < k) {
				this.compSwap(array, j, i-1);
				this.compSwap(array, i-1, k);
			}
		}
    }
}
