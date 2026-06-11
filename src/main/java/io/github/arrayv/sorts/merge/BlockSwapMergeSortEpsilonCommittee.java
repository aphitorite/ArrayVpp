package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class BlockSwapMergeSortEpsilonCommittee extends Sort {
    public BlockSwapMergeSortEpsilonCommittee(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Block-Swap Merge (Epsilon Committee)");
        this.setRunAllSortsName("The Epsilon Committee's Block-Swap Merge Sort");
        this.setRunSortName("The Epsilon Committee's Block-Swap Mergesort");
        this.setCategory("Merge Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Piotr Grochowski, aphitorite");
    }
    
    private void multiSwap(int[] array, int a, int b, int len) {
        for(int i = 0; i < len; i++)
            Writes.swap(array, a+i, b+i, 1, true, false);
    }
    
    private int linearSearchMid(int[] array, int start, int mid, int end) {
        int b = Math.min(mid-start, end-mid);
        
        for(; b > 0 && Reads.compareIndices(array, mid-b, mid+b-1, 1, true) <= 0; b--);
        
        return b;
    }
    private void merge(int[] array, int a, int b) {
    	int p, r, z;
    	do {
    		p = 0;
    		for(int i = z = r = a; i < b - (~p & 1); i++) {
	    		if(p % 2 > 0) {
	    			if(Reads.compareIndices(array, i, r, 1, true) >= 0) {
	    				int l = linearSearchMid(array, z, r + 1, i + 1);
	    				multiSwap(array, r - l + 1, r + 1, l);
	    				z = r = i;
	    				p++;
	    			}
	    		} else {
	    			if(Reads.compareIndices(array, i, i+1, 1, true) > 0) {
	    				r = i;
	    				p++;
	    			}
	    		}
	    	}
    		if(p % 2 > 0 && z < r + 1) {
				int l = linearSearchMid(array, z, r + 1, b);
				multiSwap(array, r - l + 1, r + 1, l);
				p++;
    		}
    	} while(p > 0);
    }
    
    private String ternary(int v, int min) {
    	String o = "";
    	while(v > 0 || min > 0) {
    		o = (v % 3) + o;
    		v /= 3;
    		min--;
    	}
    	return o;
    }
    private void mergimentBlockSwap(int[] array, int a, int b) {
    	int zl = 32 - Integer.numberOfLeadingZeros(b-a-1), y = zl, z = 1;
    	while(zl-- > 0) z *= 3;
    	for(int i=0; i<z; i++) {
    		int l = a, r = b;
			arrayVisualizer.setExtraHeading(" ["+ternary(i, y)+"]");
    		for(int x=z/3; x>0; x/=3) {
    			switch((i/x)%3) {
    				case 0: r = l+(r-l)/2; break;
    				case 1: l += (r-l)/2; break;
    				case 2: merge(array, l, r);
    			}
    		}
    	}
		arrayVisualizer.setExtraHeading("");
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	mergimentBlockSwap(array, 0, length);
    }
}