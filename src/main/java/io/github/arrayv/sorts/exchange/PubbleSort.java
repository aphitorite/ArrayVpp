package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
,_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_.
* ~~~~~~~~~ Pushy bUBBLE Sort ~~~~~~~~~ *
|              Part of the              |
*        "Dissort Can You Make"         *
|                series                 |
'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'
*/

final public class PubbleSort extends Sort {
    public PubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Pubble");
        this.setRunAllSortsName("Pubble Sort");
        this.setRunSortName("Pubblesort");
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
    	for(int ij = length - 1; ij > 0; ij--) {
            for(int i = 0; i <= ij; i++) {
	            for(int j = i; j < ij; j++) {
	                if(Reads.compareValues(array[j], array[i]) == 1) {
	                    Writes.multiSwap(array, i, j, 0.075, true, false);
	                }
	                if(j > i && Reads.compareValues(array[j-1], array[j]) == 1) {
	                    Writes.swap(array, j - 1, j, 0.075, true, false);
	                }
	                if(j + 1 <= ij && Reads.compareValues(array[j], array[j+1]) == 1) {
	                    Writes.swap(array, j, j + 1, 0.075, true, false);
	                }
	                Highlights.markArray(1, j);
	                Highlights.markArray(2, j + 1);
	                Delays.sleep(0.025);
	            }
            }
        }
    }
}