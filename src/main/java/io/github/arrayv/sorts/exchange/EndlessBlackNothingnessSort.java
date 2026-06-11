package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class EndlessBlackNothingnessSort extends Sort {
	public EndlessBlackNothingnessSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("EBN");
		this.setRunAllSortsName("Endless Black Nothingness Sort (Void Sort III)");
		this.setRunSortName("EBNsort (Voidsort III)");
		this.setCategory("Impractical Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(1024);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	// code taken from Buvusort
    public void omegaPush(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		Writes.multiSwap(array, end-1, start, 0.01, true, false);
    	}
    }
    public void omegaPushBW(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		Writes.multiSwap(array, start, end-1, 0.01, true, false);
    	}
    }
    public void omega2Push(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		omegaPushBW(array, start, end);
    	}
    }
    public void omega2PushBW(int[] array, int start, int end) {
    	for(int i=0; i<end-start-1; i++) {
    		omegaPush(array, start, end);
    	}
    }
    // O(2n-1 * (2^(n/2 + 1)))?
    private void omegaSwap(int[] array, int start, int end, int r) {
    	if(start >= end)
    		return;
    	Writes.recordDepth(r++);
    	this.omega2Push(array, start, end+1);
    	this.omega2PushBW(array, start, end);
    	this.omegaSwap(array, start+1, end-1, r);
    	this.omegaSwap(array, start+1, end-1, r);
    }
    private void omegaOmegaPush1(int[] array, int start, int end, int depth) { // Clamber-esque push, because I want it to be the worst possible.
    	depth++;
    	for(int j=end-1; j>=start; j--) {
    		omegaSwap(array, j, end-1, depth);
    	}
    }
    private void omegaOmegaPushBW1(int[] array, int start, int end, int depth) {
    	depth++;
    	for(int j=start+1; j<end; j++) {
    		omegaSwap(array, start, j, depth);
    	}
    }
    private void omegaOmegaPush(int[] array, int start, int end, int depth) {
    	depth++;
    	for(int i=start; i<end-1; i++) {
    		omegaOmegaPushBW1(array, start, end, depth);
    	}
    }
    private void omegaOmegaPushBW(int[] array, int start, int end, int depth) {
    	depth++;
    	for(int i=start; i<end-1; i++) {
    		omegaOmegaPush1(array, start, end, depth);
    	}
    }
    private void omegaOmegaSwap(int[] array, int start, int end, int r) {
    	if(start >= end)
    		return;
    	Writes.recordDepth(r++);
    	this.omegaOmegaPush(array, start, end+1, r);
    	this.omegaOmegaPushBW(array, start, end, r);
    	this.omegaOmegaSwap(array, start+1, end-1, r);
    	this.omegaOmegaSwap(array, start+1, end-1, r);
    }
    private void omegaOmegaOmegaPushBW(int[] array, int start, int end, int depth) {
    	depth++;
    	for(int j=start+1; j<end; j++) {
    		omegaOmegaSwap(array, start, j, depth);
    	}
    }
    private void what_why(int[] array, int start, int end, int d) {
    	Writes.recordDepth(d++);
    	int m=(end-start)/2;
    	if(m==0)
    		return;
    	for(int i=0;i<m;i++) {
    		omegaOmegaOmegaPushBW(array, start, end, d);
    	}
    	what_why(array, start, start+m, d);
    	what_why(array, start+m, end, d);
    }
    
    // onto the endless and hungry blackness
	private void voidSortJacked(int[] A, int i, int j, int d, int o) {
		Writes.recordDepth(d++);
		
		if (Reads.compareValues(A[i], A[j]) == 1) {
			Writes.swap(A, i, j, 0.005, true, false);
		}
		
		Delays.sleep(0.0025);
		
		Highlights.markArray(1, i);
		Highlights.markArray(2, j);
		
		for(int k=0; k<=o; k++) {
			for(int p=1; p<j-i; p++) {
				Writes.recursion(2);
				voidSortJacked(A, i+p, j, d, o*o);
				voidSortJacked(A, i, j-p, d, o*o);
				voidSortJacked(A, i+p, j, d, o*o);
				if(o > 0) {
					what_why(A, i, j, d);
					Writes.recursion();
					voidSortJacked(A, i, j, d, o - 1);
				}
			}
		}
	}
	private void voidVoidSortJacked(int[] A, int i, int j, int d, int o) {
		Writes.recordDepth(d++);
		int O = o;
		for(int k=0; k<=O; k++) {
			for(int p=1; p<j-i; p++) {
				Writes.recursion(2);
				voidVoidSortJacked(A, i, j-p, d, o*=o);
				voidVoidSortJacked(A, i+p, j, d, o*=o);
				voidVoidSortJacked(A, i, j-p, d, o*=o);
				if(O > 0) {
					what_why(A, i, j, d);
					Writes.recursion();
					voidVoidSortJacked(A, i, j, d, O - 1);
				} else {
					voidSortJacked(A, i, j, d, p * (j - i));
				}
			}
		}
	}
	private void endlessBlackNothingness(int[] A, int i, int j, int d, int o) {
		Writes.recordDepth(d++);
		int O = o;
		for(int k=0; k<=O; k++) {
			for(int p=1; p<j-i; p++) {
				Writes.recursion(2);
				endlessBlackNothingness(A, i, j-p, d, o*=o);
				endlessBlackNothingness(A, i+p, j, d, o*=o);
				endlessBlackNothingness(A, i, j-p, d, o*=o);
				if(O > 0) {
					what_why(A, i, j, d);
					Writes.recursion();
					endlessBlackNothingness(A, i, j, d, O - 1);
				} else {
					voidVoidSortJacked(A, i, j, d, p * (j - i));
				}
			}
		}
	}

	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		this.endlessBlackNothingness(array, 0, currentLength - 1, 0, currentLength);
	}
}