package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 * 
MIT License

Copyright (c) 2024 aphitorite & Gaming32

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

final public class SwapMapSort2 extends Sort {
    public SwapMapSort2(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Swap Map II");
        this.setRunAllSortsName("Swap Map Sort");
        this.setRunSortName("Swap Map Sort");
        this.setCategory("Exchange Sorts");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
	
	private boolean compSwap(int[] array, int a, int b) {
		if(Reads.compareIndices(array, a, b, 0.5, true) > 0) {
			Writes.swap(array, a, b, 0.5, false, false);
			return true;
		}
		return false;
	}

    @Override
    public void runSort(int[] array, int n, int bucketCount) throws Exception {
		int[] map    = Writes.createExternalArray(n/2 + 2);
		int[] newMap = Writes.createExternalArray(n/2 + 2);
		
		Writes.write(map,    0, 0, 0, false, true);
		Writes.write(newMap, 0, 0, 0, false, true);
		
		int k = 1;
		
		for(int i = 1; i < n; i += 2) {
			this.compSwap(array, i-1, i);
			Writes.write(map, k++, i+1, 0, false, true);
		}
		if(map[k-1] == n) k--;
		
		for(int j = 1; j < n && k > 1; j++) {
			int m = 1;
			
			for(int i = 1; i < k; i++) {
				int idx = map[i];
				
				if(this.compSwap(array, idx-1, idx)) {
					
					if(idx-1 > newMap[m-1]) 
						Writes.write(newMap, m++, idx-1, 0, false, true);
					
					Writes.write(newMap, m++, idx+1, 0, false, true);
				}
			}
			if(newMap[m-1] == n) m--;
			
			k = m;
			int[] t = map; map = newMap; newMap = t;
		}
		Writes.deleteExternalArray(map);
		Writes.deleteExternalArray(newMap);
    }
}