package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.GrailSorting;
import io.github.arrayv.sorts.insert.BlockShellSort;
import io.github.arrayv.utils.Rotations;

final public class LaziteSort extends GrailSorting {

    public LaziteSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Lazite");
        this.setRunAllSortsName("Lazite Sort");
        this.setRunSortName("Lazite Sort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private BlockShellSort bss;
    
    private int K, Ks;
    
    private void laziteMerge(int[] array, int start, int mid, int end) {
    	int l = start, r = mid, f = 0, lb = K, rb = mid, ls = Ks, rs = 0;
		while(l < mid && r < end && ls > 0) {
			if(Reads.compareValues(array[l], array[r]) <= 0) {
				Writes.swap(array, lb++, l++, 1, true, false);
			} else {
				Writes.swap(array, lb++, r++, 1, true, false);
				ls--; rs++;
			}
		}
		if(ls > 0) {
			while(l < mid) {
				Writes.swap(array, lb++, l++, 1, true, false);
			}
			while(r < end && ls > 0) {
				Writes.swap(array, lb++, r++, 1, true, false);
				ls--; rs++;
			}
		}
    	while(l < mid || r < end) {
    		if(f > rs * 3) {
    			Rotations.juggling(array, lb, rb - lb + rs, f, 1, true, false);
    			lb += f; l += f; mid += f; rb += f;
    			f = 0;
    		}
    		int b = rb, j = 0;
    		Rotations.juggling(array, rb, rs, f, 1, true, false);
    		j+=f; b+=f;
    		while(l < mid && r < end && rs > 0) {
    			j++;
    			if(Reads.compareValues(array[l], array[r]) <= 0) {
    				Writes.swap(array, b++, l++, 1, true, false);
    				ls++; rs--;
    			} else {
    				Writes.swap(array, b++, r++, 1, true, false);
    			}
    		}
    		if(rs > 0) {
    			while(l < mid && rs > 0) {
        			j++;
    				Writes.swap(array, b++, l++, 1, true, false);
    				ls++; rs--;
    			}
    			while(r < end) {
        			j++;
    				Writes.swap(array, b++, r++, 1, true, false);
    			}
    		}
    		for(int i = 0; i < ls && i < j; i++) {
    			Writes.swap(array, lb++, rb+i, 1, true, false);
    		}
    		rs += Math.min(ls, j);
    		f = Math.max(j - ls, 0);
    		ls = Math.max(ls - j, 0);
    	}
    	Rotations.juggling(array, lb, ls, rb-(lb+ls), 1, true, false);
    	K = end - Ks;
    }
    
    private void sort(int[] array, int start, int end) {
    	bss = new BlockShellSort(arrayVisualizer);
    	K = start; Ks = (int) Math.pow(end-start, 0.5);
    	int z = (end - start) / 2;
    	for(int i = K + Ks; i < end; i += z) {
    		bss.shellSort(array, i, Math.min(i+z, end));
    	}
		for(int j=z; j<end-start; j*=2) {
			for(int i=K+Ks; i<end; i+=2*j) {
				if(i+j>end)
					break;
				if(i+2*j<end) {
					laziteMerge(array, i, i+j, i+2*j);
				} else {
					laziteMerge(array, i, i+j, end);
				}
			}
		}
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucket) {
    	sort(array, 0, currentLength);
    }
}
