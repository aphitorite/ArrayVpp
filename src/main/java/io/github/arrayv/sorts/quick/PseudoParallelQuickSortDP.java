package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ImplQueue;

final public class PseudoParallelQuickSortDP extends Sort {
    public PseudoParallelQuickSortDP(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Pseudo-Parallel Quick (Dual Pivot)");
        this.setRunAllSortsName("Pseudo-Parallel Dual Pivot Quicksort");
        this.setRunSortName("Pseudo-Parallel Dual-Pivot Quicksort");
        this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    class dp {
    	public int a, b, i, l, g, p0, p1;
    	public dp(int[] array, int start, int end) {
    		a = start;
    		b = end;
    		l = i = a + 1;
    		g = b - 1;
    		p0 = array[a];
    		p1 = array[b];
    		if(Reads.compareValues(p0, p1) > 0) {
    			Writes.swap(array, a, b, 1, true, false);
        		p0 = array[a];
        		p1 = array[b];
    		}
    	}
    }
    public void quick(int[] array, int start, int end) {
    	ImplQueue<dp> q = new ImplQueue<>();
    	q.add(new dp(array, start, end-1));
    	int i;
    	while(!q.isEmpty()) {
    		for(i=0; i<q.size();) {
        		double s = 1d / q.size();
    			dp now = q.peek(i);
    			if(now.i > now.g) {
    				q.remove(i);
    				Writes.swap(array, now.a, now.l-1, s, true, false);
    				Writes.swap(array, now.b, now.g+1, s, true, false);
    				if(now.a < now.l - 2) {
    					q.add(new dp(array, now.a, now.l-2));
    				}
    				if(now.l < now.g) {
    					q.add(new dp(array, now.l, now.g));
    				}
    				if(now.g + 2 < now.b) {
    					q.add(new dp(array, now.g+2, now.b));
    				}
    				continue;
    			}
    			if(Reads.compareIndexValue(array, now.i, now.p0, 0.33*s, true) < 0) {
    				Writes.swap(array, now.i, now.l++, s, true, false);
    			} else if(Reads.compareIndexValue(array, now.i, now.p1, 0.33*s, true) > 0) {
    				while(now.g > now.i && Reads.compareIndexValue(array, now.g, now.p1, 0.33*s, true) > 0) {
    					now.g--;
    				}
    				Writes.swap(array, now.i, now.g--, s, true, false);
    				if(Reads.compareIndexValue(array, now.i, now.p0, 0.33*s, true) < 0)
    					Writes.swap(array, now.i, now.l++, s, true, false);
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