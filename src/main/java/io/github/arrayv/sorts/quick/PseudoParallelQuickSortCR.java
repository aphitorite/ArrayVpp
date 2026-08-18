package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ImplQueue;

final public class PseudoParallelQuickSortCR extends Sort {
    public PseudoParallelQuickSortCR(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Pseudo-Parallel Quick (Cube Root)");
        this.setRunAllSortsName("Pseudo-Parallel Cube Root Quicksort");
        this.setRunSortName("Pseudo-Parallel Cube Root Quicksort");
        this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private int cbrt(int v) {
    	return (int) Math.cbrt(v);
    }
    public int[] createNoVis(int size) {
    	int[] n = new int[size];
    	Writes.changeAllocAmount(size);
    	return n;
    }
    public void clear(int[] a) {
    	Writes.changeAllocAmount(-a.length);
    }
    private BinaryInsertionSort bis;
    class cr {
    	// a: start, b: end, i: i, P: pivots, t: temp, p: position now,
    	// m: mode (binsert/binsearch/insert), pos: positions of partitions
    	public int a, b, i, P, l, r, t, m, p, pd, pos[];
    	public cr(int[] array, int start, int end) {
    		a = start;
    		b = end;
    		P = cbrt(b - a);
    		i = a + P;
    		p = start;
    		pd = P - 1;
    		l = 0;
    		r = P;
    		m = 0;
    		pos = createNoVis(P);
    		for(int i=0; i<P; i++) {
    			Writes.write(pos, i, a+i, 0, false, true);
    		}
    	}
    }
    public void quick(int[] array, int start, int end) {
    	bis = new BinaryInsertionSort(arrayVisualizer);
    	ImplQueue<cr> q = new ImplQueue<>();
    	q.add(new cr(array, start, end-1));
    	int i;
    	while(!q.isEmpty()) {
    		for(i=0; i<q.size();) {
        		double s = 1d / q.size();
    			cr n = q.peek(i);
    			if(n.i > n.b) {
    				q.remove(i);
    				int z = n.a-1;
    				for(int j=0; j<n.P; j++) {
    					int y = n.pos[j], p = z + 1;
    					if(p < y)
    						q.add(new cr(array, p, y));
    					z = y;
    				}
					if(z+1 < n.b)
						q.add(new cr(array, z+1, n.b));
    				clear(n.pos);
    				continue;
    			}
    			if(n.m == 0) {
    				bis.customBinaryInsert(array, n.a, n.i, s);
    				n.m = 1;
    			}
    			if(n.l < n.r) {
    				int m = n.l + (n.r - n.l) / 2;
    				if(Reads.compareIndices(array, n.i, n.pos[m], 0.33*s, true) >= 0) {
    					n.l = m + 1;
    				} else {
    					n.r = m;
    				}
    			} else {
    				if(n.m == 1) {
    					n.t = array[n.i];
    					n.pd = n.P - 1;
    					n.p = n.i;
    					n.m = 2;
    				}
    				if(n.pd >= n.l) {
    					int z = n.pos[n.pd];
    					Writes.write(array, n.p, array[z+1], 0.166*s, true, false);
    					Writes.write(array, z+1, array[n.p = z], 0.166*s, true, false);
    					Writes.write(n.pos, n.pd--, z+1, 0, false, true);
    				} else {
    	    			if(n.l < n.P) {
	    					Writes.write(array, n.p, n.t, 0.166*s, true, false);
	    				}
    					n.i++;
    					n.m = 1;
    					n.l = 0;
    					n.r = n.P;
    				}
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