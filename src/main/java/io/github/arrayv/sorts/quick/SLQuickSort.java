package io.github.arrayv.sorts.quick;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.PriorityQueue;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class SLQuickSort extends Sort {
    public SLQuickSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("SL Quick");
        this.setRunAllSortsName("SL Quicksort");
        this.setRunSortName("SL Quicksort");
        this.setCategory("Quick Sorts");
		this.setAuthors("Distray");
		this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private BigInteger bitlist;
    
    private class Head implements Comparable<Head> {
    	private int start, end;
    	public int depth;
    	public Head(int start, int end, int depth) {
    		this.start = start;
    		this.end = end;
    		this.depth = depth;
    	} 
    	
    	public int length() {
    		return this.end-this.start;
    	}
    	
    	public int branchlessCompare(int a, int b) {
    		return ((a-b)>>31)|-((b-a)>>31);
    	}
    	
    	@Override
    	public int compareTo(Head head) {
    		return branchlessCompare(length(),head.length());
    	}
    }
    
    public void siftDown(int[] array, int root, int dist, int start, double sleep) {
        int compareVal = -1;
        
        while (root <= dist / 2) {
            int leaf = 2 * root;
            if (leaf < dist && Reads.compareValues(array[start + leaf - 1], array[start + leaf]) == compareVal) {
                leaf++;
            }
            Highlights.markArray(1, start + root - 1);
            Highlights.markArray(2, start + leaf - 1);
            Delays.sleep(sleep);
            if (Reads.compareValues(array[start + root - 1], array[start + leaf - 1]) == compareVal) {
                Writes.swap(array, start + root - 1, start + leaf - 1, 0, true, false);
                root = leaf;
            }
            else break;
        }
    }

    public void heapify(int[] arr, int low, int high, double sleep) {
        int length = high - low;
        for (int i = length / 2; i >= 1; i--) {
            siftDown(arr, i, length, low, sleep);
        }
    }
    
    private void quickSort(int[] a, PriorityQueue<Head> q, int p, int r, int d) {
    	int s = (int) Math.sqrt(r-p);
    	
    	this.heapify(a, p, p+s, 0.125);
    	
    	for(int i=p+s-1; i>p+s/2; i--) {
    		Writes.swap(a, i, p, 1, true, false);
    		this.siftDown(a, 1, i-p-1, p, 0.5);
    	}
    	
    	int x = a[p];
    	
    	int i = p, j = r;
        
        while (i <= j) {
            while (i <= j && Reads.compareValues(a[i], x) == -1){
                i++;
                Highlights.markArray(1, i);
                Delays.sleep(0.5);
            }
            while (i <= j && Reads.compareValues(a[j], x) == 1){
                j--;
                Highlights.markArray(2, j);
                Delays.sleep(0.5);
            }

            if (i <= j) {
                Writes.swap(a, i++, j--, 1, true, false);
            }
        }
        
        q.offer(new Head(p, j, d));
        q.offer(new Head(i, r, d));
    }
	
    private boolean bitIsSet(BigInteger b, int loc) {
		return b.and(BigInteger.ZERO.setBit(loc)).signum() == 1;
	}
	private void setBit(int loc) {
		bitlist = bitlist.setBit(loc);
	}
	private int[] minMax(int[] array, int start, int end) {
		int min = array[start], max = array[start];
		for(int i=start+1; i<end; i++) {
			if(max < array[i]) {
				max = array[i];
			} else if(min > array[i]) {
				min = array[i];
			}
		}
		return new int[] {min, max};
	}
	public void MOPS(int[] array, int start, int end) {
		int[] mx = minMax(array, start, end);
        int min = mx[0],
        	max = mx[1];
        this.bitlist = BigInteger.ZERO; // A BigInteger to keep track of uniques
        HashMap<Integer, Integer> overflow = new HashMap<>(); // A HashMap to keep track of duplicates
        for(int i=start; i<end; i++) {
        	int v = array[i] - min;
        	Highlights.markArray(1, i);
        	Delays.sleep(1);
        	if(bitIsSet(bitlist, v)) { // Duplicate number case
        		Writes.mockWrite(max-min+1, v, 1, 0);
        		overflow.put(v, overflow.getOrDefault(v, 1) + 1); // Track the dupe with the overflow map
        	} else {
        		setBit(v); // Let the bit-list know that we've found a unique number
        	}
        }
        
        for(int i=start, cnt=-1, lead = 0; i<end; i++) {
        	if(lead == 0) // If we're tracking dupes, don't advance
        		do {
	        		cnt++;
	        	} while(!bitIsSet(bitlist, cnt));
        	if(overflow.getOrDefault(cnt, 1) > 1) { // If overflow[cnt] has a dupe count greater than one,
        		lead = overflow.get(cnt); // put the dupe count in lead,
        		overflow.put(cnt, 1); // and reset the overflow dupe count, to avoid infinite loop.
        	}
    		Writes.write(array, i, cnt + min, 1, true, false);
        	if(lead > 0) { // Decrement the dupe count when still operating on an overflowed bit
        		lead--;
        	}
        }
	}

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	PriorityQueue<Head> q = new PriorityQueue<>();
        this.quickSort(array, q, 0, currentLength - 1, 0);
        while(q.size() > 0) {
        	Head poll = q.poll();
        	if(poll.length() < 1)
        		continue;
        	if(poll.end-poll.start < 64) {
        		this.MOPS(array, poll.start, poll.end+1);
        		continue;
        	}
        	this.quickSort(array, q, poll.start, poll.end, poll.depth+1);
        }
    }
}