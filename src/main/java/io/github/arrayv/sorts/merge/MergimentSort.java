package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class MergimentSort extends Sort {
    public MergimentSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Mergiment");
        this.setRunAllSortsName("Mergiment Sort");
        this.setRunSortName("Mergiment Sort");
        this.setCategory("Merge Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    
    private void merge(int[] array, int[] tmp, int start, int mid, int end) {
    	int left = start, right = mid, to = 0;
    	while(left < mid && right < end) {
    		if(Reads.compareValues(array[left], array[right]) <= 0) {
    			Writes.write(tmp, to++, array[left++], 1, true, true);
    		} else {
    			Writes.write(tmp, to++, array[right++], 1, true, true);
    		}
    	}
    	while(left < mid)
			Writes.write(tmp, to++, array[left++], 1, true, true);
    	while(to-- > 0) {
			Writes.write(array, start + to, tmp[to], 1, true, false);
    	}
    }
    
    private int log2(int v) {
    	int l = -1;
    	while(v > 0) {
    		l++;
    		v /= 2;
    	}
    	return l;
    }
    
    private int ceillog(int v) {
    	return log2(v-1)+1;
    }
    
    
    public void mergiment(int[] array, int start, int end) {
    	int z = ceillog(end-start),
    		y = 1;
    	int[] t = Writes.createExternalArray(end-start);
    	for(int i=0; i<z; i++)
    		y *= 3;
    	for(int i=0; i<y; i++) {
    		int a = start, b = end;
    		for(int x=y/3; 0<x && a<b; x/=3) {
    			switch((i/x)%3) {
    				case 0:
    					b = a+(b-a)/2;
    					break;
    				case 1:
    					a = a+(b-a)/2;
    					break;
    				case 2:
    					merge(array, t, a, a+(b-a)/2, b);
    					break;
    			}
    		}
    	}
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	mergiment(array, 0, length);
    }
}