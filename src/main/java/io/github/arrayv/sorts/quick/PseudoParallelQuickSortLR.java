package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ImplQueue;

final public class PseudoParallelQuickSortLR extends Sort {
    public PseudoParallelQuickSortLR(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Pseudo-Parallel Quick (L/R)");
        this.setRunAllSortsName("Pseudo-Parallel Quicksort (Left/Right)");
        this.setRunSortName("Pseudo-Parallel Quicksort (Left/Right)");
        this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    class hoare {
    	public int a, b, i, j, p, c0, c1;
    	public hoare(int[] array, int start, int end) {
    		a = i = start;
    		b = j = end;
    		p = array[b-(b-a)/2];
    		c0 = c1 = -2;
    	}
    }
    public void quick(int[] array, int start, int end) {
    	ImplQueue<hoare> q = new ImplQueue<>();
    	q.add(new hoare(array, start, end-1));
    	while(!q.isEmpty()) {
    		for(int i=0; i<q.size();) {
        		double s = 1d / q.size();
    			hoare now = q.peek(i);
    			if(now.c0 == -2 || now.c0 < 0) {
    				if(now.c0 != -2) now.i++;
    				now.c0 = Reads.compareIndexValue(array, now.i, now.p, 0.25*s, true);
    			}
    			if(now.c1 == -2 || now.c1 > 0) {
    				if(now.c1 != -2) now.j--;
    				now.c1 = Reads.compareIndexValue(array, now.j, now.p, 0.25*s, true);
    			}
    			if(now.c0 >= 0 && now.c1 <= 0 && now.i <= now.j) {
    				Writes.swap(array, now.i, now.j, s, true, false);
    				now.c0 = -1; now.c1 = 1;
    			}
    			if(now.i > now.j) {
    				q.remove(i);
    				if(now.a < now.j)
    					q.add(new hoare(array, now.a, now.j));
    				if(now.i < now.b)
    					q.add(new hoare(array, now.i, now.b));
    				continue;
    			}
    			i++;
    		}
    	}
    }
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	quick(array, 0, currentLength);
    }
}