package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class FastMergimentSort extends Sort {
    public FastMergimentSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Fast Mergiment");
        this.setRunAllSortsName("Fast Mergiment Sort");
        this.setRunSortName("Fast Mergiment Sort");
        this.setCategory("Merge Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private void merge(int[] array, int[] tmp, int start, int mid, int end) {
    	int left = start, right = mid, to = 0;
    	while(left < mid && right < end) {
    		Highlights.markArray(1, left);
    		Highlights.markArray(2, right);
    		if(Reads.compareValues(array[left], array[right]) <= 0) {
    			Writes.write(tmp, to++, array[left++], 1, true, true);
    		} else {
    			Writes.write(tmp, to++, array[right++], 1, true, true);
    		}
    	}
    	while(left < mid) {
    		Highlights.markArray(1, left);
			Writes.write(tmp, to++, array[left++], 1, true, true);
    	}
    	Highlights.clearMark(1);
    	Highlights.clearMark(2);
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
    
    private String debugTernary(int v, int min) {
    	String o = "";
    	while(v > 0 || min > 0) {
    		o = (v % 3) + o;
    		v /= 3;
    		min--;
    	}
    	return o;
    }
    
    public void mergiment(int[] array, int start, int end) {
    	System.out.print("\033[H\033[2J");
    	System.out.flush();
    	int z = ceillog(end-start),
    		y = 1;
    	int[] t = Writes.createExternalArray(end-start);
    	for(int i=0; i<z; i++)
    		y *= 3;
    	iterationloop:
    	for(int i=2; i<y;) { // skip 2 pointless iterations
    		int a = start, b = end;
    		for(int x=y/3; 0<x && a<b; x/=3) {
    			switch((i/x)%3) {
    				case 0:
    					b = a+(b-a)/2;
    					break;
    				case 1:
    					a += (b-a)/2;
    					break;
    				case 2:
    		    		System.out.println(debugTernary(i, z)); // only debug the merge events
    					merge(array, t, a, a+(b-a)/2, b);
    					i += x;
    					continue iterationloop;
    			}
    		}
    		i+=2; // we already know that the next iteration is pointless, so we're able to skip it
    	}
    	Writes.deleteExternalArray(t);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	mergiment(array, 0, length);
    }
}