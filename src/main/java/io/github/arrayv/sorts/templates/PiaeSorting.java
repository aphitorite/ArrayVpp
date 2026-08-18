package io.github.arrayv.sorts.templates;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Range;

import io.github.arrayv.sorts.insert.InsertionSort;

// can you tell this was from before i knew what a block merge did?
public abstract class PiaeSorting extends Sort {
	public PiaeSorting(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
	}
	protected int key, keySize, buffer, bufferSize, localStart;
	private InsertionSort inserter;
	protected Range keySubsection(int index) {
		return new Range(localStart + (index * bufferSize), localStart + ((index + 1) * bufferSize) - 1);
	}
	protected void multiSwap(int[] array, int locA, int locB, int size) {
		for(int i=0; i<size; i++) {
			Writes.swap(array, locA+i, locB+i, 1, true, false);
		}
	}
	protected void rotate(int[] array, int loc, int lenA, int lenB) {
		if(lenA <= 0 || lenB <= 0)
			return;
		if(lenA >= lenB) {
			this.multiSwap(array, loc, loc+lenA, lenB);
			loc += lenB;
			int sPos = loc + lenA - 2*lenB;
			int i=sPos;
	    	for(; i >= loc; i -= lenB) {
	    		this.multiSwap(array, i, i+lenB, lenB);
	    	}
			int leftOver = (i + lenB) - loc;
	    	if(leftOver > 0) {
	    		this.rotate(array, loc, leftOver, lenB);
	    	}
		} else {
			int i = loc+lenB;
			for(; i>=loc+lenA; i-=lenA) {
				this.multiSwap(array, loc, i, lenA);
			}
			int leftOver = i - loc;
	    	if(leftOver > 0) {
	    		this.rotate(array, loc, lenA, leftOver);
	    	}
		}
	}
	protected void shiftKey(int[] array, int index, int dist) {
		int z = array[key+index+dist];
		Writes.reversearraycopy(array, key+index, array, key+index+1, dist, 1, true, false);
		Writes.write(array, key+index, z, 1, true, false);
	}
	protected void shift(int[] array, int start, int len) {
		int z = array[start+len];
		Writes.reversearraycopy(array, start, array, start+1, len, 1, true, false);
		Writes.write(array, start, z, 1, true, false);
	}
	protected void shiftBW(int[] array, int start, int len) {
		int z = array[start];
		Writes.arraycopy(array, start+1, array, start, len, 1, true, false);
		Writes.write(array, start+len, z, 1, true, false);
	}
	
	protected void scrollMerge(int[] array, int start, int mid, int end) {
		int left = start, right = mid;
		while(left < mid && right < end) {
			if(Reads.compareValues(array[left], array[right]) <= 0) {
				Writes.swap(array, this.buffer++, left++, 1, true, false);
			} else {
				Writes.swap(array, this.buffer++, right++, 1, true, false);
			}
		}
		while(left < mid) {
			Writes.swap(array, this.buffer++, left++, 1, true, false);
		}
		while(right < end) {
			Writes.swap(array, this.buffer++, right++, 1, true, false);
		}
	}
	
	protected int scrollMergeF(int[] array, int start, int mid, int end) {
		int left = start, right = mid;
		while(left < mid && right < end) {
			if(Reads.compareValues(array[left], array[right]) <= 0) {
				Writes.swap(array, this.buffer++, left++, 1, true, false);
			} else {
				Writes.swap(array, this.buffer++, right++, 1, true, false);
			}
		}
		if(left < mid) {
			int z = mid-left;
			this.multiSwap(array, left, end-z, z);
			return z;
		} else {
			return end-right;
		}
	}
	
	protected void rotateInBlocks(int[] array, int start, int mid, int end) {
		int blocks = (int) Math.ceil((end - start + 1) / (double)bufferSize);
		this.localStart = start;
		int midKey = (blocks / 2), midNum = array[key + midKey], k = 0;
		for(int i=mid; i<end; i+=bufferSize) {
			Range j = this.keySubsection(k);
			while(Reads.compareValues(array[j.start], array[i]) < 0) {
				j = this.keySubsection(++k);
			}
			int z = Math.min(i+bufferSize, end)-i;
			this.rotate(array, j.start, i-j.start, z);
			this.shiftKey(array, k, midKey++ - k);
		}
		int f = start;
		for(int i=1; i<blocks; i++) {
			int z = start+i*bufferSize;
			if(z+bufferSize <= end)
				f = z+bufferSize - this.scrollMergeF(array, f, z, z+bufferSize);
		}
		this.rotate(array, buffer, bufferSize, end-(buffer+bufferSize));
		buffer = end-bufferSize;
		int below = 0;
		for(int i=blocks; i>=0; i--) {
			if(Reads.compareValues(array[key+i], midNum) >= 0) {
				this.shiftBW(array, key+i, below);
			} else below++;
		}
		//this.unguarded(array, key, key+blocks);
	}
	protected void unguarded(int[] array, int start, int end) {
		for(int i=start+1; i<end; i++) {
			int t = array[i];
			if(Reads.compareValues(array[start], t) == 1) {
				this.shift(array, start, i-start);
				continue;
			} else if(Reads.compareValues(array[i-1], t) == -1) {
				continue;
			}
			int k=i;
			do {
				Writes.write(array, k, array[k-1], 1, true, false);
				k--;
			} while(k > start && Reads.compareValues(array[k-1], t) == 1);
			Writes.write(array, k, t, 1, true, false);
		}
	}
	protected void CollapsedMerge(int[] array, int start, int mid, int end) {
		int left = start, right = mid, to = 0, min = 0, min2 = 0;
		boolean highHit = false, lowHit = true;
		while(left < mid && right < end) {
			Highlights.markArray(2, left);
			Highlights.markArray(3, right);
			if(Reads.compareValues(array[left], array[right]) <= 0) {
				if(!highHit) {
					left++;
					to++;
					min++;
				} else {
					Writes.swap(array, this.buffer+to++, left++, 1, true, false);
				}
				lowHit = true;
			} else {
				if(!lowHit) {
					right++;
					to++;
					min2++;
					min++;
				} else {
					Writes.swap(array, this.buffer+to++, right++, 1, true, false);
				}
				highHit = true;
			}
		}
		Highlights.clearMark(2);
		Highlights.clearMark(3);
		int leftSize = mid - left;
		if(leftSize > 0) {
			while(leftSize > 0) {
				Writes.swap(array, end - leftSize--, left++, 1, true, false);
			}
		}
		for(int j = 0; j < min2; j++) {
			Writes.swap(array, j+start, j+mid, 1, true, false);
		}
		for(int k = min; k < to; k++) {
			Writes.swap(array, k+start, k+this.buffer, 1, true, false);
		}
	}
	
	protected int ceilSqrt(long length) {
		long z = 1;
		while(z*z < length)
			z <<= 1;
		return (int)z;
	}
	
	protected int binSearch(int[] array, int start, int end, int key, boolean goLeft) {
		while(start < end) {
			int mid = start + ((end - start) / 2);
			if(goLeft)
				if(Reads.compareValues(array[mid], key) <= 0) {
					start = mid+1;
				} else {
					end = mid;
				}
			else
				if(Reads.compareValues(array[mid], key) < 0) {
					start = mid+1;
				} else {
					end = mid;
				}
			Highlights.markArray(1, mid);
			Delays.sleep(1);
		}
		return start;
	}
	
	protected void rotateMerge(int[] array, int start, int mid, int end) {
		if(start==mid||mid==end)
			return;
		
		int mid1, mid2, rotatedmid;
		if(mid-start >= end-mid) {
			mid1 = (mid+start) / 2;
			mid2 = this.binSearch(array, mid, end, array[mid1], true);
			rotatedmid = mid1 + (mid2 - mid);
		} else {
			mid2 = (mid+end+1) / 2;
			mid1 = this.binSearch(array, start, mid, array[mid2], false);
			rotatedmid = mid2++ - (mid - mid1);
		}
		
		this.rotate(array, mid1, mid-mid1, mid2-mid);
		
		this.rotateMerge(array, start, mid1, rotatedmid);
		this.rotateMerge(array, rotatedmid+1, mid2, end);
	}
	
	protected void piae(int[] array, int start, int end) {
		this.inserter = new InsertionSort(arrayVisualizer);
		if(end-start < 16) {
			this.inserter.customInsertSort(array, start, end, 1, false);
			return;
		}
		this.bufferSize = this.ceilSqrt(end-start);
		this.key = start;
		this.keySize = ((end-start-1) / this.bufferSize) + 1;
		this.buffer = this.key + this.keySize;
		start = this.buffer + this.bufferSize;
		int t = this.buffer;
		this.unguarded(array, this.key, this.key+this.keySize);
		for(int i=2; i<=this.bufferSize; i*=2) {
			for(int j=start; j<end; j+=i) {
				int z=Math.min(j+i, end);
				this.CollapsedMerge(array, j, j+(i/2), z);
			}
		}
		for(int i=start; i<end; i+=2*this.bufferSize) {
			if(i+2*bufferSize <= end) {
				this.scrollMerge(array, i, i+this.bufferSize, i+2*this.bufferSize);
			}
		}
		for(int j=4*this.bufferSize; j<=end-this.key; j*=2) {
			this.rotate(array, t, buffer-t, this.bufferSize);
			buffer=t;
			for(int i=start; i<end; i+=j) {
				int k = Math.min(i+j, end);
				if(k-i <= 3*this.bufferSize) {
					this.scrollMerge(array, i, i+(j/2), k);
				} else {
					this.rotateInBlocks(array, i, i+(j/2), k);
				}
			}
		}
		this.rotate(array, t, buffer-t, this.bufferSize);
		buffer=t;
		int tK = this.key, tKs = this.keySize, tBs = this.bufferSize;
		this.piae(array, buffer, buffer+this.bufferSize);
		this.rotateMerge(array, tK, tK+tKs, t+tBs);
		this.rotateMerge(array, tK, t+tBs, end);
	}
}