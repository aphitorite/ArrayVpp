package io.github.arrayv.sorts.misc;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 * <b>IDeserve <br>
 * <a href="<a class="vglnk" href="https://www.youtube.com/c/IDeserve" rel="nofollow"><span>https</span><span>://</span><span>www</span><span>.</span><span>youtube</span><span>.</span><span>com</span><span>/</span><span>c</span><span>/</span><span>IDeserve</span></a>"><a class="vglnk" href="https://www.youtube.com/c/IDeserve" rel="nofollow"><span>https</span><span>://</span><span>www</span><span>.</span><span>youtube</span><span>.</span><span>com</span><span>/</span><span>c</span><span>/</span><span>IDeserve</span></a></a>
 * Given an array, sort the array using Pancake sort.
 * 
 * @author Saurabh
 * https://www.ideserve.co.in/learn/pancake-sorting
 */

final public class OptimizedPancakeSort2 extends Sort {
    public OptimizedPancakeSort2(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Optimized Pancake II");
        this.setRunAllSortsName("Optimized Pancake Sort");
        this.setRunSortName("Optimized Pancake Sort");
        this.setCategory("Miscellaneous Sorts");
        this.setConstant("n log^2 n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Saurabh");
    }
	
	private void flip(int[] array, int n) {
		Writes.reversal(array, 0, n-1, 0.25, true, false);
	}
	
	private boolean merge(int[] array, int h1, int h2) {
		if(h1 == 1 && h2 == 1) {
			if(Reads.compareIndices(array, 0, 1, 0.5, true) > 0)
				this.flip(array, 2);
		}
		else if(h2 < h1) {
			if(h2 < 1) return true;
			
			int m = (h1+h2)/2;
			int i = 0, j = h2;
			
			while(i < j) {
				int k = (i+j)/2;
				
				if(Reads.compareIndices(array, h1+h2-1-k-m, h1+h2-1-k, 0.5, true) > 0)
					i = k+1;
				else
					j = k;
			}
			this.flip(array, h1+h2-m-i);
			this.flip(array, h1+h2-i);
			if(this.merge(array, h2-i, i+m-h2))
				this.flip(array, m);
			this.flip(array, h1+h2);
			if(!this.merge(array, i, h1+h2-m-i))
				this.flip(array, h1+h2-m);
		}
		else {
			if(h1 < 1) return false;
			
			int m = (h1+h2)/2;
			int i = 0, j = h1;
			
			while(i < j) {
				int k = (i+j)/2;
				
				if(Reads.compareIndices(array, k, k+m, 0.5, true) < 0)
					i = k+1;
				else
					j = k;
			}
			this.flip(array, i);
			this.flip(array, i+m);
			if(this.merge(array, i+m-h1, h1-i))
				this.flip(array, m);
			this.flip(array, h1+h2);
			if(!this.merge(array, h1+h2-m-i, i))
				this.flip(array, h1+h2-m);
		}
		return true;
	}
	
	private void sort(int[] array, int n) {
		if(n < 2) return;
		
		int h = n/2;
		
		this.sort(array, h);
		this.flip(array, n);
		this.sort(array, n-h);
		this.merge(array, n-h, h);
	}
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
		this.sort(array, length);
    }
}
