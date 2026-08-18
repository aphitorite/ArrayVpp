package io.github.arrayv.sorts.hybrid;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Rotations;

final public class ControlKMergeSort extends Sort {
    public ControlKMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Control's Kmerge");
        this.setRunAllSortsName("Control's Kmerge Sort");
        this.setRunSortName("Control's Kmerge Sort (WIP)");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Control");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setQuestion("Set k of the sort:", 2);
        this.setBogoSort(false);
    }
    
    private void multiSwap(int[] array, int locA, int locB, int size) {
    	for(int i=0; i<size; i++) {
    		Writes.swap(array, locA+i, locB+i, 1, true, false);
    	}
    }
    
    private int binSearch(int[] array, int l, int r, int d, int k) {
    	int a=0, b=r-l, m;
    	while(a<b) {
    		m=a+((b-a)>>1);
    		if(Reads.compareIndices(array, l+m, k, 0.25, true) > -d) {
    			b=m;
    		} else {
    			a=m+1;
    		}
    	}
    	return Math.min(l+a, r);
    }

    @Override
    public int validateAnswer(int answer) {
        if (answer < 2) return 2;
        return answer;
    }
    
    private void merge(int[] array, int a, int m, int b, int c, int base) {
    	if(b - m == 0 || m - a == 0) return;
    	while(m - a >= b - m && b - m > 1) {
    		int d = binSearch(array, m, b, c, m - 1), l = d - m;
    		multiSwap(array, m - l, m, l);
    		mergerev(array, binSearch(array, m-l, m, 1, m), m, binSearch(array, m, d, 0, m - 1), c^1, base);
    		b = m; m -= l;
    	}
    	if(b - m == 1) {
    		Writes.multiSwap(array, m, binSearch(array, a, m, c, m), 1, true, false);
    	}
    	if(m - a < b - m) {
    		mergerev(array, a, m, b, c, base);
    		//for(int i=0, l=b-m; i<base; i++) {
    		//	merge(array, a, m+((l*i)/base), m+((l*i+l)/base), base);
    		//}
    	}
    }
    
    private void mergerev(int[] array, int a, int m, int b, int c, int base) {
    	if(b - m == 0 || m - a == 0) return;
    	while(m - a < b - m && m - a > 1) {
    		int d = binSearch(array, a, m, c, m), l = m - d;
    		multiSwap(array, d, m, l);
    		merge(array, binSearch(array, a, m, 1, m), m, binSearch(array, m, m+l, 0, m-1), c^1, base);
    		a = m; m += l;
    	}
    	if(m - a == 1) {
    		Writes.multiSwap(array, a, binSearch(array, m, b, c, a)-1, 1, true, false);
    	}
      	if(m - a >= b - m) {
    		merge(array, a, m, b, c, base);
    		//for(int i=0, l=b-m; i<base; i++) {
    		//	merge(array, a, m+((l*i)/base), m+((l*i+l)/base), base);
    		//}
    	}
    }
    
    private void kmerge(int[] array, int a, int b, int base) {
    	int m = a + (b - a) / 2;
    	if(a < m) {
    		InsertionSort si = new InsertionSort(arrayVisualizer);
    		kmerge(array, a, m,  base);
			//si.customInsertSort(array, a, m, 0.05, false);
    		for(int i=0, l=b-m; i<base; i++) {
    			//si.customInsertSort(array, m+((l*i)/base), m+((l*i+l)/base), 0.05, false);
    			kmerge(array, m+((l*i)/base), m+((l*i+l)/base), base);
    		}
    		for(int i=0, l=b-m; i<base; i++) {
    			merge(array, a, m+((l*i)/base), m+((l*i+l)/base), 1, base);
    		}
    	}
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	kmerge(array, 0, length, bucketCount);
    }
}