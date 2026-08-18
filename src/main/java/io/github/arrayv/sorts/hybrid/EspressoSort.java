package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.sorts.templates.GrailSorting;
import io.github.arrayv.utils.IndexedRotations;
import io.github.arrayv.sorts.hybrid.NilSort;
import io.github.arrayv.main.ArrayVisualizer;

class BoundariesKey {
	public int keysloc, start, mid, end, keys;
	public boolean tails;
	public BoundariesKey(int keysloc0, int start0, int mid0, int end0, int keys0, boolean tails0) {
		keysloc = keysloc0; start = start0; mid = mid0; end = end0; keys = keys0; tails = tails0;
	}
}
//i'm_old!
final public class EspressoSort extends GrailSorting {
    public EspressoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Espresso");
        this.setRunAllSortsName("Espresso Sort");
        this.setRunSortName("Espressort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
	
	private int threshold;
	private NilSort smallSort;
	
	private int binSearch(int[] array, int start, int end, int keyPos, boolean exclusive) {
		while(start < end) {
			int mid = start + (end - start) / 2;
			switch(Reads.compareIndices(array, mid, keyPos, 1, true)) {
				case 0:
					if(exclusive)
						return -1;
				case 1:
					end = mid;
					break;
				default:
					start = mid + 1;
					break;
			}
		}
		return start;
	}
	
	private void multiSwap(int[] array, int locA, int locB, int length) {
		while(length-- > 0) {
			Writes.swap(array, locA++, locB++, 1, true, false);
		}
	}
	
	private BoundariesKey collectKeys(int[] array, int start, int mid, int end, int desired) {
		int keysFound = 1,
			firstKey = start;
		for(int i=start+1; i<mid && keysFound < desired; i++) {
			if(Reads.compareValues(array[i], array[firstKey+keysFound]) == 1) // found highest key, rotate over
				IndexedRotations.helium(array, firstKey, firstKey + keysFound, i, 1, true, false);
				firstKey = i - keysFound++;
		}
		if(keysFound < desired) {
			int keysFoundInFirst = keysFound;
			for(int i=mid; i<end && keysFound < desired; i++) {
				switch(Reads.compareValues(array[i], array[firstKey+keysFound])) {
					case 1: // found highest key, rotate over
						IndexedRotations.helium(array, firstKey, firstKey + keysFound, i, 1, true, false);
						firstKey = i - keysFound++;
						break;
					case -1: // search for its potential place in the keys
						int to = binSearch(array, firstKey, firstKey + keysFound, i, true);
						if(to < 0) break;
						IndexedRotations.helium(array, firstKey, firstKey + keysFound, i, 1, true, false);
						to = i - firstKey + to;
						firstKey = i - keysFound++;
						IndexedRotations.helium(array, to, i-1, i, 1, true, false);
						break;
				}
			}
			if(keysFoundInFirst != keysFound) { // no change, rotate back
				IndexedRotations.helium(array, firstKey, firstKey + keysFound, end, 1, true, false);
				return new BoundariesKey(end - keysFound, start, mid - keysFoundInFirst, end - keysFound, keysFound, true);
			}
		}
		IndexedRotations.helium(array, start, firstKey, firstKey + keysFound, 1, true, false);
		return new BoundariesKey(start, start + keysFound, mid, end, keysFound, false);
	}
	
	private int buffer;

	private int mergeFW(int[] array, int start, int mid, int end) {
		int lo=start, hi=mid;
		while(lo < mid && hi < end) {
			if(Reads.compareValues(array[lo], array[hi]) <= 0) {
				Writes.swap(array, buffer++, lo++, 1, true, false);
			} else {
				Writes.swap(array, buffer++, hi++, 1, true, false);
			}
		}
		if(lo < mid) {
			int j = mid-lo;
			multiSwap(array, end-j, lo, j);
			return j;
		} else {
			return end-hi;
		}
	}
	private int mergeBW(int[] array, int start, int mid, int end) {
		int lo=mid-1, hi=end-1;
		while(lo >= start && hi >= mid) {
			if(Reads.compareValues(array[lo], array[hi]) <= 0) {
				Writes.swap(array, --buffer, lo--, 1, true, false);
			} else {
				Writes.swap(array, --buffer, hi--, 1, true, false);
			}
		}
		if(lo >= start) {
			return lo - start + 1;
		} else {
			multiSwap(array, mid, start, hi-mid+1);
			return hi - mid + 1;
		}
	}
	
	private void blockselect(int[] array, int start, int mid, int end) {
		int size = (int) Math.sqrt(end-start), ogsize = end-start;
		int endTemp = end;
		BoundariesKey keys = collectKeys(array, start, mid, end, size);
		start = keys.start; mid = keys.mid; end = keys.end;
		size = (end - start) / keys.keys;
		int leftOver = (mid - start) % size,
			rightOver = (end - mid) % size;
		if(rightOver > 0) {
			IndexedRotations.holyGriesMills(array, end - rightOver, end, endTemp, 1, true, false);
			end -= rightOver;
			endTemp -= rightOver;
			if(keys.tails)
				keys.keysloc -= rightOver;
		}
		if(leftOver > 0) {
			IndexedRotations.holyGriesMills(array, mid - leftOver, mid, endTemp, 1, true, false);
			end -= leftOver;
			mid -= leftOver;
			endTemp -= leftOver;
			if(keys.tails)
				keys.keysloc -= leftOver;
		}
		int ogkeys = keys.keys;
		keys.keys = Math.min(keys.keys, (end - start + 1) / keys.keys);
		int offset = keys.tails ? size-1 : 0,
			midKey = keys.keys / 2,
			endKey = keys.keys,
			midK = midKey;
		for(int i=0; i<=endKey; i++) {
			int min = i;
			for(int j = Math.max(midKey, i+1); j <= midK; j++) {
				int compare = Reads.compareIndices(array, start + offset + size * min, start + offset + size * j, 0.25, true);
				if(compare > 0 || (compare == 0 && Reads.compareIndices(array, keys.keysloc + min, keys.keysloc + j, 1, true) > 0)) {
					min = j;
				}
			}
			if(i != min) {
				if(min == midK && midK < endKey) midK++;
				multiSwap(array, start + size * min, start + size * i, size);
				Writes.swap(array, keys.keysloc + min, keys.keysloc + i, 1, true, false);
			}
		}
		keys.keys = ogkeys;
		buffer = keys.keysloc;
		int keysOver = keys.keys - size, frag;
		if(keys.tails) {
			frag=end;
			buffer += Math.min(keys.keys, size);
			for(int i=1; i<endKey; i++) {
				int now=end-i*size;
				if(keys.keys < size) {
					grailMergeWithoutBuffer(array, now-size, size, frag-now);
					frag-=size;
				} else {
					frag=now-size+mergeBW(array, now-size, now, frag);
				}
			}
			if(keys.keys >= size) {
				multiSwap(array, start, buffer+size-frag-start, frag-start);
				IndexedRotations.holyGriesMills(array, start+size, keys.keysloc+size, keys.keysloc+keys.keys, 1, true, false);
				keys.keysloc = start;
			} else {
				IndexedRotations.holyGriesMills(array, start, buffer-keys.keys, buffer, 1, true, false);
				keys.keysloc = start;
			}
		} else {
			frag=start;
			for(int i=1; i<endKey; i++) {
				int now=start+i*size;
				if(keys.keys < size) {
					grailMergeWithoutBuffer(array, frag, size, size);
					frag+=size;
				} else {
					frag=now+size-mergeFW(array, frag, now, now+size);
				}
			}
			if(keys.keys >= size) {
				multiSwap(array, buffer, frag, end-frag);
				IndexedRotations.holyGriesMills(array, keys.keysloc+keysOver, buffer+end-frag, end, 1, true, false);
			}
		}
		smallSort.runZero(array, keys.keysloc, keys.keysloc+keys.keys+1);
		grailMergeWithoutBuffer(array, keys.keysloc, keys.keys+1, end-start);
		grailMergeWithoutBuffer(array, endTemp, leftOver, rightOver);
		grailMergeWithoutBuffer(array, keys.keysloc, endTemp-keys.keysloc, leftOver+rightOver);
		
		Delays.togglePaused(); // stub
	}
    
	private void merge(int[] array, int start, int end) {
		int mid = start+(end-start)/2;
		if(mid==start) return;
		if(end-start < threshold) {
			Delays.setSleepRatio(1000);
			smallSort.runZero(array, start, end);
			Delays.setSleepRatio(0.1);
			return;
		}
		merge(array, start, mid);
		merge(array, mid, end);
		blockselect(array, start, mid, end);
	}
	
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	threshold = (int) Math.pow(length, 0.67d);
    	smallSort = new NilSort(arrayVisualizer);
    	merge(array, 0, length);
    }
}