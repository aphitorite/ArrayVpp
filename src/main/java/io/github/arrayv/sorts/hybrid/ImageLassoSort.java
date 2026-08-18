package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.GrailSorting;
import io.github.arrayv.sorts.templates.PingPongMergeSorting;

final public class ImageLassoSort extends PingPongMergeSorting {
    public ImageLassoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Image Lasso");
        this.setRunAllSortsName("Image Lasso Sort");
        this.setRunSortName("Image-Lassosort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private GrailSorting mergeBack;
    private int bufSz, bufLoc;
    private int[] ectaKeys;
    
    protected int ceilSqrt(int l) {
    	long k = 1;
    	while(k*k < l) {
    		k<<=1;
    	}
    	return (int) k;
    }
    
	protected void multiSwapBW(int[] array, int locA, int locB, int size) {
		for(int i=size-1; i>=0; i--) {
			Writes.swap(array, locA+i, locB+i, 1, true, false);
		}
	}
    
	protected void multiSwap(int[] array, int locA, int locB, int size) {
		while(size-- > 0) {
			Writes.swap(array, locA++, locB++, 1, true, false);
		}
	}

    protected void mergeRight(int[] array, int start, int mid, int end) {
    	if(end == mid || mid == start)
    		return;
    	this.multiSwapBW(array, mid, bufLoc, end-mid);
    	int left = mid-1, right = bufLoc+(end-mid)-1, to = end-1;
    	while(left >= start && right >= bufLoc) {
    		if(Reads.compareValues(array[left], array[right])==1) {
    			Writes.swap(array,left--,to--,1,true,false);
    		} else {
    			Writes.swap(array,right--,to--,1,true,false);
    		}
    	}
    	while(right >= bufLoc) {
			Writes.swap(array,right--,to--,1,true,false);
    	}
    }
    
    // cost: O((2 sqrt n) - k)
    protected int blockIndexMerge(int[] array, int start, int mid, int end) {
    	if(end == mid || mid == start)
    		return 0;
    	if(end-start <= 2*bufSz)
    		return 0;
    	int midKey = (mid - start - 1) / bufSz + 1,
    		l = start, r = mid, keyLeft = 0,
    		keyRight = midKey, currentBlock = 0;
    	while(l <= mid - bufSz && r <= end - bufSz) {
    		Highlights.markArray(1, l);
    		Highlights.markArray(2, r);
    		Delays.sleep(4);
    		if(Reads.compareValues(array[l], array[r]) <= 0) {
    			Writes.write(ectaKeys, keyLeft++, currentBlock++, 1, true, true);
    			l += bufSz;
    		} else {
    			Writes.write(ectaKeys, keyRight++, currentBlock++, 1, true, true);
    			r += bufSz;
    		}
    	}
    	while(l <= mid - bufSz) {
    		Highlights.markArray(1, l);
    		Delays.sleep(4);
			Writes.write(ectaKeys, keyLeft++, currentBlock++, 1, true, true);
			l += bufSz;
    	}
    	while(r <= end - bufSz) {
    		Highlights.markArray(1, r);
    		Delays.sleep(4);
			Writes.write(ectaKeys, keyRight++, currentBlock++, 1, true, true);
			r += bufSz;
    	}
    	int actions = 0;
    	for(int i=0; i<currentBlock; i++) {
    		int comps = 0;
    		
    		if(ectaKeys[i] == i)
    			continue;
    		
    		while(Reads.compareOriginalValues(ectaKeys[i], i) != 0 && comps < currentBlock) {
    			int index = ectaKeys[i];
    			Writes.swap(ectaKeys, i, index, 1, true, true);
    			this.multiSwap(array, start+i*bufSz, start+index*bufSz, bufSz);
    			actions++;
    		}
    		if(comps >= currentBlock-1)
    			break;
    	}
    	return actions;
    }
    
    // fast check to ensure that we don't waste unnecessary comps
    // cost: O(sqrt n) sorted, O(k) otherwise
    private boolean blockCheck(int[] array, int start, int end) {
    	for(int i=start+bufSz; i<end; i+=bufSz) {
    		if(Reads.compareValues(array[i-1], array[i]) > 0)
    			return false;
    	}
    	return true;
    }
    
    // slower check to ensure we aren't missing something
    private boolean sortCheck(int[] array, int start, int end) {
    	for(int i=start+1; i<end; i++) {
    		if(Reads.compareValues(array[i-1], array[i]) > 0)
    			return false;
    	}
    	return true;
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	bufSz = ceilSqrt(length);
    	int keysNeeded = (length-bufSz-1)/bufSz+1,
    		start = bufSz;
    	bufLoc = 0;
    	mergeBack = new GrailSort(arrayVisualizer);
    	ectaKeys = Writes.createExternalArray(keysNeeded);
    	for(int i=0; i<keysNeeded; i++) {
    		Highlights.markArray(1, i);
    		Writes.write(ectaKeys, i, i, 1, true, true);
    	}
    	for(int i=start; i<length; i+=bufSz) {
    		this.pingPong(array, bufLoc, i, Math.min(i+bufSz, length), 4);
    	}
    	for(int i=start; i<length; i+=2*bufSz) {
    		this.mergeRight(array, i, i+bufSz, Math.min(i+2*bufSz, length));
    	}
    	boolean w = blockCheck(array, start, length),
    			oe = false,
    			ad = true;
    	while(!w || !sortCheck(array, start, length)) {
    		int m = 0;
    		if(ad) {
    			for(int j=4*bufSz; j<=length; j*=2) {
        			for(int i=start; i<length; i+=j) {
        				int k=Math.min(i+j, length);
        				m += this.blockIndexMerge(array, i, i+j/2, k);
        			}
        		}
        		ad = m > 0;
    		}
    		if(w)
    			oe = !oe;
    		for(int i=start + (oe?1:0); i<length; i+=bufSz*(ad?2:1)) {
    			this.mergeRight(array, i, i+bufSz, Math.min(i+2*bufSz, length));
    		}
    		w = blockCheck(array, start, length);
    	}
    	Writes.deleteExternalArray(ectaKeys);
    	this.BidirectionalExpoInsert(array, 0, start, 0.1, false);
    	mergeBack.grailMergeWithoutBuffer(array, 0, bufSz, length-bufSz);
    }
}