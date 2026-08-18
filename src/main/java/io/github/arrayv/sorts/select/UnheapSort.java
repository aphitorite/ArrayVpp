package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

/*
,_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_.
* ~~~~~~~~~~~~ Unheap Sort ~~~~~~~~~~~~ *
|          An extension of the          |
*        "Dissort Can You Make"         *
|                series                 |
'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'*'
*/

final public class UnheapSort extends BogoSorting {   
    public UnheapSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Unheap");
        this.setRunAllSortsName("Unheap Sort");
        this.setRunSortName("Unheap Sort");
        this.setCategory("Selection Sorts");
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
    			Writes.swap(array, b - t, b - l3, 1, true, false);
    		}
    		t = l3;
    	}
    	return s;
    }
    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
    	while(sortLength > 0) {
    		int t = 0;
    		while(!isMaxSorted(array, 0, sortLength)) {
    			t = 0;
    			int v = array[sortLength-1];
    			while(t <= sortLength / 2 && Reads.compareIndexValue(array, sortLength-1, v, 0.5, true) <= 0) {
    				int i = pull(array, 0, sortLength, t);
    				//if(i == 1) while(pull(array, 0, sortLength, t) == 1);
    				t = (t + 1) * (1 - i);
    			}
    				//t = (t - 1) * (1 + pull(array, 0, sortLength, t));
    		}
    		sortLength--;
    	}
    }
}