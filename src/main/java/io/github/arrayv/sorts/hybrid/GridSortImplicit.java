package io.github.arrayv.sorts.hybrid;


import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.merge.OnlinePDMSort;
import io.github.arrayv.sorts.templates.Sort;

final public class GridSortImplicit extends Sort {
    public GridSortImplicit(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Grid (Implicit)");
        this.setRunAllSortsName("Gridsort");
        this.setRunSortName("\"Gridsort\"");
        this.setCategory("Hybrid Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Scandum, aphitorite, Distray");
    }
    
    private void tailmerge(int[] array, int[] tmp, int start, int mid, int end, int t2, int p) {
    	if(start >= mid || mid >= end)
    		return;
    	Writes.arraycopy(array, mid, tmp, 0, end-mid, 0.25, true, true);
    	int[][] table = new int[][] {array, tmp};
    	int[] ptrs = new int[] {mid-1, end-mid-1},
    		  vals = new int[] {mid-start, end-mid};
    	int cmp, to = t2 + p;
    	while(vals[0] > 0 && vals[1] > 0) {
    		cmp = -((Reads.compareValues(array[ptrs[0]], tmp[ptrs[1]])-1) >> 31);
    		Highlights.markArray(2, ptrs[cmp]);
    		Writes.write(array, --to, table[cmp][ptrs[cmp]--], 0.5, true, true);
    		if(to == t2) {
    			to = end - p;
    		}
    		--vals[cmp];
    	}
    	while(vals[1] > 0) {
    		if(to == t2) {
    			to = end - p;
    		}
    		Writes.write(array, --to, tmp[ptrs[1]--], 0.5, true, true);
    		--vals[1];
    	}
    }
    
    private void oopmerge(int[] from, int[] to, int start, int mid, int end, int t, boolean taux) {
    	int l = start, r = mid;
    	while(l < mid && r < end) {
    		if(Reads.compareValues(from[l], from[r]) <= 0) {
    			Writes.write(to, t, from[l++], 1, true, taux);
    		} else {
    			Writes.write(to, t, from[r++], 1, true, taux);
    		}
    		t++;
    	}
    	while(l < mid) {
			Writes.write(to, t++, from[l++], 1, true, taux);
    	}
    	while(r < end) {
			Writes.write(to, t++, from[r++], 1, true, taux);
    	}
    }
    
    private void ppmerge(int[] array, int[] tmp, int start, int end) {
    	boolean c=false;
    	for(int i=1; i<end-start; i*=2) {
    		int p=c?0:start;
    		for(int j=0; j<end-start; j+=2*i) {
    			int m=Math.min(j+i, end-start),
    				e=Math.min(m+i, end-start);
    			oopmerge(c?tmp:array, c?array:tmp, p+j, p+m, p+e, j+start-p, true);
    		}
    		c=!c;
    	}
    	if(c) {
    		Writes.arraycopy(tmp, 0, array, start, end-start, 0.5, true, true);
    	}
    }
    
    private int binsearchindices(int[] grid, int[] indices, int left, int right, int key, int gap) {
    	while(left < right) {
    		int mid = right - (right - left) / 2;
    		Highlights.markArray(2, indices[mid]);
    		if(Reads.compareIndexValue(grid, indices[mid]*gap, key, 0.25, true) <= 0) {
    			left = mid;
    		} else {
    			right = mid - 1;
    		}
    	}
    	Highlights.clearMark(2);
    	return left;
    }
    
    private OnlinePDMSort op;
    
    public void grid(int[] array, int start, int end) {
    	op = new OnlinePDMSort(arrayVisualizer);
    	int s = (int) Math.sqrt(end-start-1) + 1, b = 2 * s, t = 2;
    	int[] sizes = new int[s],
    		indices = new int[s],
    		   grid = Writes.createExternalArray(b*s),
    		 mrgaux = Writes.createExternalArray(s);
    	
    	op.ms(array, mrgaux, 0, start, start+b, b, 0, false);
    	
    	Writes.arraycopy(array, start, grid, 0, s, 1, true, false);
    	Writes.arraycopy(array, start+s, grid, b, s, 1, true, false);
    	sizes[0] = sizes[1] = s;
    	indices[1] = 1;
    	
    	for(int i=start+b; i<end; i++) {
    		int m = binsearchindices(grid, indices, 0, t-1, array[i], b), n = indices[m];
    		Writes.write(grid, b*n+sizes[n]++, array[i], 0.75, true, true);
    		if(sizes[n] == b) {
    			ppmerge(grid, mrgaux, b*n+s, b*(n+1));
    			tailmerge(grid, mrgaux, b*n, b*n+s, b*(n+1), b*t, s);
    			sizes[n] = sizes[t] = s;
    			for(int j=t-1; j>m; j--) {
    				indices[j+1] = indices[j];
    			}
    			indices[m+1] = t++;
    		}
    	}
    	
    	for(int j=start, k=0; k<t; k++) {
    		int i = indices[k];
    		if(sizes[i] > s) {
    			ppmerge(grid, mrgaux, b*i+s, b*i+sizes[i]);
    			oopmerge(grid, array, b*i, b*i+s, b*i+sizes[i], j, false);
    		} else {
    			Writes.arraycopy(grid, b*i, array, j, sizes[i], 1, true, false);
    		}
			j += sizes[i];
    	}
    	Writes.deleteExternalArrays(grid, mrgaux);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	grid(array, 0, length);
    }
}