package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

final public class ChiselSort extends BogoSorting {
    public ChiselSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Chisel");
        this.setRunAllSortsName("Chisel Sort");
        this.setRunSortName("Chisel Sort");
        this.setCategory("Exchange Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    @Override
    public void runSort(int[] array, int currentLength, int buckets) {
    	int i, s, t=0;
    	while(t<currentLength) {s=1;while(s++<currentLength) {
    		i=0;
    		while(++i<currentLength) {
    			int l=(i/((i%s)+1)+t)%currentLength;
    			if(l != i && ((l > i) ^ Reads.compareIndices(array, l, i, 0, true) > 0)) {
    				Writes.multiSwap(array, i, l, 1, true, false);
    			}
    		}
    	}t++;}
    }
}
