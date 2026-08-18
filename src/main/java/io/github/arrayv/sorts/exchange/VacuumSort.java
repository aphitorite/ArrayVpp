package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

/*
,_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_.
* ~~~~~~~~~~~~ Vacuum Sort ~~~~~~~~~~~~ *
|              Part of the              |
*        "Dissort Can You Make"         *
|                series                 |
'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'
*/

final public class VacuumSort extends BogoSorting {   
    public VacuumSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Vacuum");
        this.setRunAllSortsName("Vacuum Sort");
        this.setRunSortName("Vacuum Sort");
        this.setCategory("Exchange Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private int pull(int[] array, int a, int b, int t) {
    	int s = 0;
    	--b;
    	while(b - t > a) {
    		int l = 2 * t + 1, l2 = 2 * t + 2;
    		
    		int l3 = l2 > b - a || Reads.compareIndices(array, b - l, b - l2, 1, true) > 0 ? l : l2;
    		
    		if(l3 <= b - a && Reads.compareIndices(array, b - t, b - l3, 1, true) < 0) {
    			s = 1;
    			Writes.multiSwap(array, b - l3, b - t, 1, true, false);
    		}
    		t = l3;
    	}
    	return s;
    }
    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
    	while(sortLength > 0) {
    		int t = 0;
    		while(!isMaxSorted(array, 0, sortLength))
    			t = (t + 1) * (1 - pull(array, 0, sortLength, t));
    		sortLength--;
    	}
    }
}