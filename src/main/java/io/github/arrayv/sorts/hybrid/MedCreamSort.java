package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;


final public class MedCreamSort extends Sort {

    public MedCreamSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Med-Cream");
        this.setRunAllSortsName("Med-Cream Sort");
        this.setRunSortName("Medium Cream Sort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setConstant("n^2");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    int sig(int a, int b, int d) {
    	return ((a + b) + d * Math.abs(a - b)) / 2;
    }
    private int findRun(int[] array, int start, int end) {
    	if(start >= end - 1)
    		return start + 1;
    	int cmp = -Reads.compareIndices(array, start++, start, 1, true),
    		k = start - 1, d;
    	boolean lUniq = false;
    	if(cmp==0) {cmp++; lUniq=true;}
    	do {
    		d = Reads.compareIndices(array, start++, start, 1, true);
    		lUniq = lUniq || d == 0;
    	} while(start < end && d != cmp);
    	int m = (start - k) / 2,
    		q = sig(k, start-1, -cmp);
    	for(int i=0; i<m; i++) {
    		Writes.swap(array, k+i, q+cmp*i, 1, true, false);
    	}
    	return start;
    }
    
    public int bindoub(int[] array, int start, int mid, int end) {
    	int a = 0, b = Math.min(mid-start, end-mid), m;
    	while(a<b) {
    		m=a+(b-a)/2;
    		if(Reads.compareValues(array[mid-m-1], array[mid+m]) > 0) {
    			a = m + 1;
    		} else {
    			b = m;
    		}
    	}
    	return a;
    }
    
    private void ms(int[] array, int start, int end, int size) {
    	while(size-- > 0)
    		Writes.swap(array, start++, end++, 1, true, false);
    }
    
    public void M8FW(int[] array, int start, int mid, int end, int depthRec, int depthEsc) {
    	if(start>=mid || mid>=end)
    		return;
    	Writes.recordDepth(depthRec++);
    	int l = start, r = mid, diter = depthEsc / 4, escape = (int) Math.pow(end-start, 0.35d);
    	while(r < end && l < mid && diter < escape) {
    		while(l < mid && Reads.compareValues(array[l], array[r]) <= 0) {
    			l++;
    		}
    		if(l >= mid)
    			return;
    		int z = l;
    		while(l < mid && r < end && Reads.compareValues(array[l], array[r]) > 0) {
    			l++; r++;
    		}
    		IndexedRotations.juggling(array, z, mid, r, 1, true, false);
    		Writes.recursion();
    		M8(array, z, l, l+(r-mid), depthRec, depthEsc+1);
    		diter++;
    		mid=r;
    	}
    	if(diter >= escape && r < end && l < mid) {
    		while(l < mid && Reads.compareValues(array[l], array[r]) <= 0) {
    			l++;
    		}
    		if(l >= mid)
    			return;
    		int z;
    		do {
    			z = bindoub(array, l, r, end);
    			ms(array,r-z,r,z);
    			Writes.recursion();
    			M8(array,l,r-z,r,depthRec,0);
    			l=r;
    			r+=z;
    			if(r >= end || z == 0)
    				break;
    		} while(z == r - l);
			M8(array,r-z,r,end,depthRec,0);
    	}
    }
    
    public void M8BW(int[] array, int start, int mid, int end, int depthRec, int depthEsc) {
    	if(start>=mid || mid>=end)
    		return;
    	Writes.recordDepth(depthRec++);
    	int l = mid-1, r = end-1, diter = depthEsc / 4, escape = (int) Math.pow(end-start, 0.35d);
    	while(r >= mid && l >= start && diter < escape) {
    		while(r >= mid && Reads.compareValues(array[l], array[r]) <= 0) {
    			r--;
    		}
    		if(r < mid)
    			return;
    		int z = r;
    		while(l >= start && r >= mid && Reads.compareValues(array[l], array[r]) >= 0) {
    			l--; r--;
    		}
    		IndexedRotations.juggling(array, l+1, mid, z+1, 1, true, false);
    		Writes.recursion();
    		M8(array, r-(mid-l)+1, r+1, z+1, depthRec, depthEsc+1);
    		diter++;
    		mid=l+1;
    	}
    	if(diter >= escape && r >= mid && l >= start) {
    		while(r >= mid && Reads.compareValues(array[l], array[r]) <= 0) {
    			r--;
    		}
    		if(r < mid)
    			return;
    		int z;
    		do {
    			z = bindoub(array, start, l+1, r+1);
    			ms(array,l-z+1,l+1,z);
    			Writes.recursion();
    			M8(array,l+1,l+z+1,r+1,depthRec,0);
    			r=l;
    			l-=z;
        		if(l < start || z == 0)
        			break;
    		} while(z == r - l);
			M8(array,start,l+1,l+z+1,depthRec,0);
    	}
    }
    
    public void M8(int[] array, int start, int mid, int end, int depthRec, int depthEsc) {
    	if(mid-start < end-mid) {
    		M8FW(array, start, mid, end, depthRec, depthEsc);
    	} else {
    		M8BW(array, start, mid, end, depthRec, depthEsc);
    	}
    }
    
    public int S(int[] array, int start, int end, int depthRun, int depthOverall) {
    	if(start >= end)
    		return start;
    	Writes.recursion();
    	Writes.recordDepth(depthOverall++);
    	if(depthRun < 2) {
    		return findRun(array, start, end);
    	}
    	int l = S(array, start, end, depthRun/2, depthOverall),
    		r = S(array, l, end, depthRun/2, depthOverall);
    	if(r > end && l >= end)
    		return l;
    	else if(r > end)
    		r=end;
    	M8(array, start, l, r, depthOverall, depthOverall);
    	return r;
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	S(array, 0, currentLength, currentLength, 0);
    }
}
