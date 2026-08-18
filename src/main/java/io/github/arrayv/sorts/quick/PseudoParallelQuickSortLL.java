package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ImplQueue;

final public class PseudoParallelQuickSortLL extends Sort {
    public PseudoParallelQuickSortLL(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Pseudo-Parallel Quick (L/L)");
        this.setRunAllSortsName("Pseudo-Parallel Quicksort (Left/Left)");
        this.setRunSortName("Pseudo-Parallel Quicksort (Left/Left)");
        this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    class lomuto {
    	public int a, b, i, j, p;
    	public lomuto(int[] array, int start, int end) {
    		a = i = j = start;
    		b = end;
    		p = array[b];
    	}
    }
    public void quick(int[] array, int start, int end) {
    	ImplQueue<lomuto> q = new ImplQueue<>();
    	q.add(new lomuto(array, start, end-1));
    	while(!q.isEmpty()) {
    		for(int i=0; i<q.size();) {
        		double s = 1d / q.size();
    			lomuto now = q.peek(i);
    			if(now.a >= now.b) {
    				q.remove(i);
    				continue;
    			}
    			if(now.i == now.b || Reads.compareIndexValue(array, now.i, now.p, 0.5*s, true) < 0) {
    				Writes.swap(array, now.i, now.j++, s, true, false);
    				if(now.i == now.b) {
    					q.remove(i);
    					if(now.a < now.j - 2)
    						q.add(new lomuto(array, now.a, now.j-2));
    					if(now.j < now.b)
    						q.add(new lomuto(array, now.j, now.b));
    					continue;
    				}
    			}
    			now.i++;
    			i++;
    		}
    	}
    }
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	quick(array, 0, currentLength);
    }
}