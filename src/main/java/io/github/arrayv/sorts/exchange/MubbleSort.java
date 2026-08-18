package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
,_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_.
* ~~~~ Monobound bUBBLE Sort (II!) ~~~~ *
|              Part of the              |
*        "Dissort Can You Make"         *
|                series                 |
'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'
*/

final public class MubbleSort extends Sort {
    public MubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Mubble");
        this.setRunAllSortsName("Mubble Sort");
        this.setRunSortName("Mubblesort");
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
        for(int i = length - 1; length - i - 1 < i; i--) {
        	int ii = length - i - 1, jj = i;
            if(Reads.compareValues(array[ii], array[jj]) == 1) {
                Writes.swap(array, ii, jj, 0.075, true, false);
            }
            for(int j = ii + 1, k = jj - 1; j < k; j++, k--) {
                if(Reads.compareValues(array[j], array[k]) == 1) {
                    Writes.swap(array, j, k, 0.075, true, false);
                }
                if(Reads.compareValues(array[j], array[ii]) == -1) {
                    Writes.swap(array, j, ii, 0.075, true, false);
                }
                if(Reads.compareValues(array[k], array[jj]) == 1) {
                    Writes.swap(array, k, jj, 0.075, true, false);
                }
            }
        }
    }
}