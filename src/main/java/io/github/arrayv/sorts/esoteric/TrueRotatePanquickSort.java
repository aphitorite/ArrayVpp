package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class TrueRotatePanquickSort extends Sort {
	public TrueRotatePanquickSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("True Rotate Panquick");
		this.setRunAllSortsName("True Rotate Panquick Sort");
		this.setRunSortName("True Rotate Panquick Sort");
		this.setCategory("Esoteric Sorts");
		this.setConstant("n log^2 n");
        this.setAuthors("Distray");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private int start = 0;
    
    private int log(int val, int base) {
    	return (int) (Math.log(val) / Math.log(base));
    }
	
	private void flip(int[] array, int len) {
		Writes.reversal(array, start, start+len, 1, true, false);
	}
    
    // median of 3
	private int medOf3(int[] array, int l0, int l1, int l2) {
		int t;
		if(Reads.compareIndices(array, l0, l1, 5, true) > 0) {
			t = l0; l0 = l1; l1 = t;
		}
		if(Reads.compareIndices(array, l1, l2, 5, true) > 0) {
			t = l1; l1 = l2; l2 = t;
			if(Reads.compareIndices(array, l0, l1, 5, true) > 0) {
				return l0;
			}
		}
		return l1;
	}
	
	// median of medians with customizable depth
	private int medOfMed(int[] array, int start, int end, int depth) {
		if(end-start < 5 && depth > 0) {
			return start;
		}
		if(end-start < 9 || depth <= 0) {
			return medOf3(array, start, start+(end-start)/2, end);
		}
		int e = (end - start) / 8;
		int m0 = medOfMed(array, start, start + 2 * e, --depth);
		int m1 = medOfMed(array, start + 3 * e, start + 5 * e, depth);
		int m2 = medOfMed(array, start + 6 * e, end, depth);
		return medOf3(array, m0, m1, m2);
	}
	
	private int pancakePartition(int[] array, int len, int cmpBias, int piv, boolean antistable, int depth) {
		if(len < 2) {
			// comparator case with Aeos-esque comparator bias
			return Reads.compareIndexValue(array, start, piv, 1, true) > -cmpBias ? 1 : 0;
		}
		Writes.recordDepth(depth++);
		int mid = len / 2, mid2 = len - mid;
		
		// partition left side
		Writes.recursion();
		int l = pancakePartition(array, mid, cmpBias, piv, antistable, depth);
		
		// push right side to start, and flip the right side to keep it in order
		// (don't know how to preserve stability without keeping it in order)
		// [if you do know, DM me]
		flip(array, len - 1);
		flip(array, mid2 - 1);
		
		// partition right side
		Writes.recursion();
		int r = pancakePartition(array, mid2, cmpBias, piv, true, depth);
		
		// flip left side's high partition to the start
		flip(array, mid2 + l - 1);
		
		// push the high left partition to the end
		if(l + r > 0 && !antistable) {
			// stable case: flip the entire combined partition for stability
			flip(array, l + r - 1);
		} else if(l > 0 && antistable) {
			// antistable case: just flip the left side of the combined partition,
			// because it's going to be made stable soon
			flip(array, l - 1);
		}
		
		// flip the list back in order
		flip(array, len - 1);
		return l + r;
	}
	
	private void rpq(int[] array, int len, boolean bad, int rrec) {
		if (len > 1) {
			Writes.recordDepth(rrec++);
			int i = medOfMed(array, start, start + len - 1, bad ? log(len, 9) : 1),
			    p = array[i];
			int q = pancakePartition(array, len, 1, p, false, rrec);
			
			// taking an Aeos-esque approach to preventing bad partitions
			if(q == len) {
				bad = true;
				q = pancakePartition(array, len, 0, p, false, rrec);
				// when comparison bias doesn't work: no unique
				if(q == 0)
					return;
			} else
				bad = false;
			
			// partition left side
			Writes.recursion();
			rpq(array, len - q, bad, rrec);
			
			// push left side to start
			flip(array, len - 1);
			flip(array, q - 1);
			
			// partition right side
			Writes.recursion();
			rpq(array, q, bad, rrec);
			
			flip(array, q - 1);
			flip(array, len - 1);
		}
	}
	
	public void rotPanquick(int[] array, int start, int len) {
		this.start = start;
		rpq(array, len, false, 0);
	}


	public void runSort(int[] array, int currentLength, int bucketCount) {
		rotPanquick(array, 0, currentLength);
	}
}