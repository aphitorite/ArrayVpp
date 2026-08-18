package io.github.arrayv.sorts.concurrent;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class OddEvenMergeSortRefactor extends Sort {
    public OddEvenMergeSortRefactor(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Odd-Even Merge (Refactor)");
        this.setRunAllSortsName("Refactored Odd-Even Merge Sort");
        this.setRunSortName("Refactored Odd-Even Mergesort");
        this.setCategory("Concurrent Sorts");
        this.setAuthors("Ken Batcher");
        this.setConstant("n log^2 n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Ken Batcher");
    }
    
    // compare and swap blocks of indices
    private void compSwapRange(int[] array, int a, int b, int end, int s) {
    	while(s-->0) {
        	if(b >= end) break;
        	if(Reads.compareIndices(array, a, b, 0.5, true) > 0)
        		Writes.swap(array, a, b, 0.5, true, false);
        	a++; b++;
    	}
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
    	// 2^n subsections from [1...ceil(log(n))]
        for(int i=2; i<2*sortLength; i*=2) {
        	// 2^n subsections from [1...i)
        	for(int j=i/2, i1=0; j>0; j=i1=j/2) {
        		// iterate on each subsection as long as applicable
        		for(int k=0; k+i1+j<=sortLength; k+=i) {
        			// compare and swap adjacent blocks of 2^j in subsection
        			for(int j1=k+i1; j1+j<k+i; j1+=2*j) {
        				compSwapRange(array, j1, j1+j, sortLength, j);
        			}
        		}
        	}
        }
    }
}