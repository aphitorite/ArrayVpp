package io.github.arrayv.sorts.quick;

import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.main.ArrayVisualizer;

final public class StacklessQuickSortII extends Sort {
    public StacklessQuickSortII(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Stackless Quick II");
        this.setRunAllSortsName("Stackless Quick Sort II");
        this.setRunSortName("Stackless Quicksort II");
        this.setCategory("Quick Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private int medOf3(int[] array, int a, int b, int c) {
    	int d;
    	if(Reads.compareIndices(array, a, b, 0.5, true) > 0) {
    		d = b; b = a;
    	} else
    		d = a;
    	if(Reads.compareIndices(array, b, c, 0.5, true) > 0) {
    		if(Reads.compareIndices(array, d, c, 0.5, true) > 0) {
        		return d;
        	}
    		return c;
    	}
    	return b;
    }
    
    private int giveNinther(int[] array, int a, int b) {
    	if(b-a<=9)
    		return array[a];
    	int len = b - a, half = len / 2, quart = len / 4, eight = len / 8;
    	int c = medOf3(array, a, a+eight, a+quart);
    	int d = medOf3(array, a+quart+eight, a+half, a+half+eight);
    	int e = medOf3(array, b-quart, b-eight, b-1);
    	int f = medOf3(array, c, d, e), fe = array[f];
    	Writes.write(array, f, array[a], 1, true, false);
    	return fe;
    }

    public int[] partition(int[] a, int p, int r) { 
        int x = giveNinther(a, p, r);
        
        int i = p, c = 0;
        int j = r-1;
    	boolean eq = true, opposing = true;
        
        while (i <= j) {
            while(i <= j && (c = Reads.compareIndexValue(a, j, x, 1, true)) > 0) {
            	eq=false;
            	j--;
            }
        	eq=eq&&c==0;
        	opposing=opposing&&c==0;
            if(i < j) {
            	Writes.write(a, i++, a[j], 1, true, false);
            } else {
            	break;
            }
            while(i < j && (c = Reads.compareIndexValue(a, i, x, 1, true)) <= 0) {
            	eq=eq&&c==0;
            	opposing=opposing&&c==0;
            	i++;
            }
            if(i < j) {
            	Writes.write(a, j--, a[i], 1, true, false);
            } else {
            	break;
            }
        }
        Writes.write(a, i, x, 1, true, false);
        return new int[] {i, eq?1:opposing?2:0};
    }

    private int bs(int[] array, int a, int b, int k) {
    	if(a>b) return b;
    	while(a<b) {
    		int m=a+(b-a)/2;
    		if(Reads.compareValueIndex(array, k, m, 1, true) < 0) {
    			b=m;
    		} else {
    			a=m+1;
    		}
    	}
    	return a;
    }
    
    // this could be written a lot more recklessly, but this needs sanity checks to avoid out-of-bounds
    private void quickSort(int[] array, int l, int r) {
    	int a = l, b = r, d, c, m[] = new int[2], mq;
    	InsertionSort ins = new InsertionSort(arrayVisualizer);
    	while(a < b - 16) {
    		d = mq = 0;
    		do {
    			c = b;
    	    	while(a < b - 16) {
    	    		m = partition(array, a, c);
    	    		if(m[1] == 2) {
    	    			a = m[0] + 1;
    	    			mq = 0;
    	    			continue;
    	    		}
    	    		mq = m[1];
    	    		if(mq == 1) break;
    	    		if(m[0] >= c - 1) { // bad depth
    	    			c--;
    	    			continue;
    	    		}
    	    		d++;
    	    		Writes.swap(array, m[0], r-1, 1, true, false);
    	    		b = c = m[0];
    	    	}
    	    	d--;
    	    	if(mq == 0)
    	    		ins.customInsertSort(array, a, c, 0.25, false);
    	    	a = b;
    	    	if(d > 0) {
	    	    	b = bs(array, a+1, r-1, array[b]);
    	    	} else {
    	    		b = r;
    	    	}
	        	if(a<r) Writes.swap(array, a++, r-1, 1, true, false);
    		} while(d > 0);
    	}
    	ins.customInsertSort(array, a, r, 0.25, false);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
		this.quickSort(array, 0, length);
    }
}