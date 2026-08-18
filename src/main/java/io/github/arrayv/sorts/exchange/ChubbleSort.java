package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
,_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_.
* ~~~~ Cocktail sHaker bUBBLE Sort ~~~~ *
|              Part of the              |
*        "Dissort Can You Make"         *
|                series                 |
'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'
*/

final public class ChubbleSort extends Sort {
    public ChubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Chubble");
        this.setRunAllSortsName("Chubble Sort");
        this.setRunSortName("Chubblesort");
        this.setCategory("Exchange Sorts");
		this.setAuthors("Distray");
		this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        for(int i = length - 1; i > 0; i--) {
            for(int j = 0; j < i; j++) {
                if(Reads.compareValues(array[j], array[j + 1]) == (i & 1) * 2 - 1) {
                    Writes.swap(array, j, j + 1, 0.075, true, false);
                }
                Highlights.markArray(1, j);
                Highlights.markArray(2, j + 1);
                Delays.sleep(0.025);
            }
        }
        int c;
        for(int i = c = ((length / 2) & ~1) + 1; i > 0; i /= 2) {
        	for(int j = 1; j < (i==1?length-1:c); j += 2) {
        		if(Reads.compareIndices(array, j, j + i, 0.5, true) > 0) {
        			Writes.swap(array, j, j + i, 0.5, true, false);
        		}
        	}
        	for(int j = 0; j < (i==1?length-1:c); j += 2) {
        		if(Reads.compareIndices(array, j, j + i, 0.5, true) > 0) {
        			Writes.swap(array, j, j + i, 0.5, true, false);
        		}
        	}
        }
        for(int i = ((length / 2) & ~1) - 1; i > 0; i /= 2) {
        	for(int j = 1; j + i < length; j += 2) {
        		if(Reads.compareIndices(array, j, j + i, 0.5, true) > 0) {
        			Writes.swap(array, j, j + i, 0.5, true, false);
        		}
        	}
        }
    }
}