package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BlockInsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;

final public class LaziererSort extends Sort {
    public LaziererSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Lazierer Stable");
        this.setRunAllSortsName("Lazierer Stable Sort");
        this.setRunSortName("Lazierer Sort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setConstant("n^1.75");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private BlockInsertionSort b;
    private static final int tolerance = 64;
	
	private int ceilPERT(int n) {
		int log=0;
		while(1<<(8*++log) < n);
		return 1<<log;
	}
	
	private int ceilPTSRT(int n) {
		int log=0;
		while(1<<(4*++log) < n);
		return 1<<log;
	}
	private void rotate(int[] array, int l, int m, int r) {
		IndexedRotations.cycleReverse(array, l, m, r, 0.52, true, false);
	}
	private void rotateUI(int[] array, int l, int ll, int lr) {
		IndexedRotations.cycleReverse(array, l, l+ll, l+ll+lr, 0.52, true, false);
	}
    
    private int binSearchE(int[] array, int l, int r, int k) {
    	int a=0, b=r-l, m;
    	while(a<b) {
    		m=a+((b-a)>>1);
    		switch(Reads.compareIndexValue(array, l+m, k, 1.25, true)) {
    			case 1:
    				b=m;
    				break;
    			case 0:
    				return -1;
    			case -1:
    				a=m+1;
    				break;
    		}
    	}
    	return l+a;
    }
	
	private void shift(int[] array, int from, int to, double sleep) {
    	if(from == to)
    		return;
		int k = array[from];
    	if(from < to) {
        	Writes.arraycopy(array, from+1, array, from, to-from, sleep/2d, true, false);
    	} else {
        	Writes.reversearraycopy(array, to, array, to+1, from-to, sleep/2d, true, false);
    	}
    	Writes.write(array, to, k, sleep, true, false);
    }
    
    private int getKeys(int[] array, int start, int end, int keysNeeded) {
    	int keysNow = 1, keysAt = start, i = start + 1, uniquesPush = 0;
    	while(i < end && keysNow < keysNeeded) {
    		Highlights.markArray(3, i);
    		int search = binSearchE(array, keysAt, keysAt + keysNow, array[i]);
    		if(search == -1) {
    			uniquesPush++;
    		} else {
    			if(uniquesPush > Math.min(tolerance, keysNow / 2)) {
    				rotateUI(array, keysAt, keysNow, uniquesPush);
    				search += uniquesPush;
    				keysAt += uniquesPush;
    				uniquesPush = 0;
    			}
    			shift(array, i, search, 0.5);
    			keysNow++;
    		}
    		i++;
    	}
    	Highlights.clearMark(3);
    	rotateUI(array, start, keysAt-start, keysNow);
    	return keysNow;
    }
	
	private int binary(int[] array, int left, int right, int key, int direction) {
		left--;
		while(left < right - 1) {
			int mid = left+(right-left)/2;
			if(Reads.compareValues(array[mid], key) - direction > 0) {
				right=mid;
			} else {
				left=mid;
			}
		}
		return right;
	}
	private void merge(int[] array, int start, int mid, int end) {
		if(start>=mid || mid>=end)
			return;
		int direction = ((mid - start) - (end - mid)) >> 31;
		if(direction == -1) {
			while(start < mid) {
				int j = binary(array, mid, end, array[start], -1);
				if(j > mid) {
					rotate(array, start, mid, j);
					start += j - mid;
					mid = j;
				}
				if(mid >= end)
					break;
				do {
					start++;
				} while(start < mid && Reads.compareValues(array[start], array[mid]) <= 0);
			}
		} else {
			while(mid < end) {
				int j = binary(array, start, mid, array[end-1], 0);
				if(j < mid) {
					rotate(array, j, mid, end);
					end -= mid - j;
					mid = j;
				}
				if(start >= mid)
					break;
				do {
					end--;
				} while(mid < end && Reads.compareValues(array[mid-1], array[end-1]) <= 0);
			}
		}
	}
	private void mergeStatic(int[] array, int buffer, int start, int mid, int end) {
		if(mid-start > end-mid) {
			for(int i=0; i<end-mid; i++) {
				Writes.swap(array, buffer+i, mid+i, 1, true, false);
			}
			int l = mid - 1, h = buffer + (end - mid) - 1;
			while(l >= start && h >= buffer) {
				if(Reads.compareValues(array[l], array[h]) > 0) {
					Writes.swap(array, l--, --end, 1, true, false);
				} else {
					Writes.swap(array, h--, --end, 1, true, false);
				}
			}
			while(h >= buffer) {
				Writes.swap(array, h--, --end, 1, true, false);
			}
			return;
		}
		for(int i=0; i<mid-start; i++) {
			Writes.swap(array, buffer+i, start+i, 1, true, false);
		}
		int l = buffer, le = l + (mid - start), h = mid;
		while(l < le && h < end) {
			if(Reads.compareValues(array[l], array[h]) <= 0) {
				Writes.swap(array, start++, l++, 1, true, false);
			} else {
				Writes.swap(array, start++, h++, 1, true, false);
			}
		}
		while(l < le) {
			Writes.swap(array, start++, l++, 1, true, false);
		}
	}
    private void mergeStepped(int[] array, int start, int mid, int end, int min, int threshold) {
    	int staticB = start - min, startTemp = start;
    	while(start < mid) {
    		int z = binary(array, mid, end, array[start+min], -1);
    		if(z > mid) {
    			rotate(array, start+min, mid, z);
    			if(min <= threshold) {
    				mergeStatic(array, staticB, start, start+min, start + (z - mid) + min);
    			} else {
    				merge(array, startTemp, start+min, start+(z-mid)+min);
    			}
    			start += z - mid;
    			mid = z;
    		}
			if(mid >= end)
				break;
			start += min;
    	}
		merge(array, startTemp, start, end);
    }
	
	public void laziererS(int[] array, int start, int end) {
		int k = ceilPTSRT(end-start), l = ceilPERT(end-start);
		int z = getKeys(array, start, end, k);
		if(z == k) {
			start += k;
		} else {
			z = 0;
		}
		b = new BlockInsertionSort(arrayVisualizer);
		for(int i=start; i<end; i+=k*k) {
			for(int j=0; j<k;)
				b.insertionSort(array, i+j*k, Math.min(i+(++j)*k, end));
			for(int j=1; j<k;)
				merge(array, i, i+j*k, Math.min(i+(++j)*k, end));
		}
		for(int i=start; i<end; i+=k*k*l) {
			for(int j=1; j<l;)
				merge(array, i, i+(j*k*k), Math.min(i+(++j*k*k), end));
		}
		int m=k*k*l;
		for(int i=start+m; i<end; i+=m) {
			mergeStepped(array, start, i, Math.min(i+m, end), k, z);
		}
		b.insertionSort(array, start-z, start);
		merge(array, start-z, start, end);
	}
    
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	laziererS(array, 0, currentLength);
    }
}