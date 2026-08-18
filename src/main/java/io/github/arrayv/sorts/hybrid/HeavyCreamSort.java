package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;

final public class HeavyCreamSort extends Sort {
    public HeavyCreamSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Heavy Cream");
        this.setRunAllSortsName("Heavy Cream Sort");
        this.setRunSortName("Heavy Cream Sort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
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
    
    public void M8FW(int[] array, int start, int mid, int end, int depth) {
    	if(start>=mid || mid>=end)
    		return;
    	Writes.recordDepth(depth++);
    	int l = start, r = mid, diter = depth / 4, escape = (int) Math.pow(end-start, 0.35d);
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
    		M8(array, z, l, l+(r-mid), depth);
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
    			M8(array,l,r-z,r,depth);
    			l=r;
    			r+=z;
    			if(r >= end || z == 0)
    				break;
    		} while(z == r - l);
			M8(array,r-z,r,end,depth);
    	}
    }
    
    public void M8BW(int[] array, int start, int mid, int end, int depth) {
    	if(start>=mid || mid>=end)
    		return;
    	Writes.recordDepth(depth++);
    	int l = mid-1, r = end-1, diter = depth / 4, escape = (int) Math.pow(end-start, 0.35d);
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
    		M8(array, r-(mid-l)+1, r+1, z+1, depth);
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
    			M8(array,l+1,l+z+1,r+1,depth);
    			r=l;
    			l-=z;
        		if(l < start || z == 0)
        			break;
    		} while(z == r - l);
			M8(array,start,l+1,l+z+1,depth);
    	}
    }
    
    public void M8(int[] array, int start, int mid, int end, int depth) {
    	if(mid-start < end-mid) {
    		M8FW(array, start, mid, end, depth);
    	} else {
    		M8BW(array, start, mid, end, depth);
    	}
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	for(int i=1; i<currentLength; i*=2) {
    		for(int j=0; j<currentLength; j+=2*i) {
    			if(j+i>currentLength)
    				break;
    			if(j+2*i<currentLength)
    				M8(array,j,j+i,j+2*i,0);
    			else
    				M8(array,j,j+i,currentLength,0);
    		}
    	}
    }
}
