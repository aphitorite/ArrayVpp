package io.github.arrayv.sorts.quick;

import static java.lang.Math.cbrt;
import static java.lang.Math.log;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public class PruneSort extends Sort {
	public PruneSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Prune");
		this.setRunAllSortsName("Prune Sort");
		this.setRunSortName("Prunesort");
		this.setCategory("Quick Sorts");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private int log2(int val) {
		return (int) (log(val) / log(2d));
	}
	
	private void multiSwap(int[] array, int locA, int locB, int size) {
		for(int i=0; i<size; i++) {
			Writes.swap(array, locA+i, locB+i, 1, true, false);
		}
	}
	
	private BinaryInsertionSort b;

	// Very "compact" and "optimized" code for Log-esque cube root partitioning in O(n / log n) space
	public void sort(int[] A, int r, int R, int bad) {
		if(bad > (2 * log2(R-r)) / 3) {
			DualPivotTernaryRotateQuickSort d = new DualPivotTernaryRotateQuickSort(arrayVisualizer);
			d.quicksort(A, r, R, bad);
			return;
		}
		if(R-r < 32) {
			if(b == null)
				b = new BinaryInsertionSort(arrayVisualizer);
			b.customBinaryInsert(A, r, R, 0.25);
			return;
		}
		
		Writes.recordDepth(bad++);
		
		int p = (int) cbrt(R-r),                           // amount of pivots
		    b = (int) 2 * log2(R-r),                       // block size
		    C = r, Z = 0;                                  // C: block copy location, Z: amount of tags
		
		sort(A, r, r+p, bad);
		
		boolean e = false;                                 // equal check (comp optimization)
		
		int[] s = Writes.createExternalArray(b*(p+1)),     // element buckets
		      S = new int[p+1],                            // size trackers
		      P = new int[p],                              // pivot holders
		      B = new int[p+1],                            // block trackers
		      Y = Writes.createExternalArray((R-r-1)/b+1); // tags
		Writes.changeAllocAmount(3*p+2);
		
		int MB = p + 1;                                    // lowest bucket written to (write optimization)
		
		Writes.arraycopy(A, r, P, 0, p, 1, true, true);
		
		for(int i=r; i<R; i++) {
			// L: left of binary search, E: right of binary search
			// c: last compare (comp optimization used in equal check)
			int L = 0, E = p, c = -2;
			while(L<E) {
				int M=L+(E-L)/2;
				if((c = Reads.compareIndexValue(A, i, P[M], 0.25, true)) >= 0) {
					L=M+1;
				} else {
					E=M;
				}
			}
			if(L < MB) {
				MB = L;
			}
			if(c != 0)
				e = true;
			Writes.write(s, L*b+S[L], A[i], 1, true, true);
			if(S[L] + 1 >= b) {
				Writes.write(Y, Z++, L, 1, true, true);
				Writes.arraycopy(s, L*b, A, C, b, 1, true, false);
				C += b;
				Writes.write(B, L, B[L] + 1, 1, true, true);
				Writes.write(S, L, 0, 1, true, true);
			} else {
				Writes.write(S, L, S[L] + 1, 1, true, true);
			}
			if(C < i)
				Writes.visualClear(A, i);
		}
		for(int i=0, j=0; i<=p; i++) {
			int t = B[i];
			Writes.write(B, i, j, 1, true, true);
			j += t;
		}
		for(int i = 0; i < Z; i++) {
			Writes.write(B, Y[i], B[Y[i]] + 1, 1, true, true);
			Writes.write(Y, i, (B[Y[i]] - 1)*b, 1, true, true);
		}
		for(int w = 0; w < Z; w++) {
			int tmp = Y[w], m = 0;
			while(Reads.compareOriginalValues(tmp, w*b) != 0) {
				int nxt = Y[tmp/b];
				Writes.write(Y, tmp/b, tmp, 1, true, true);
				multiSwap(A, r+w*b, r+tmp, b);
				tmp = nxt;
				m++;
			}
			if(m > 0) {
				Writes.write(Y, w, tmp, 0, false, true);
			}
			Highlights.markArray(1, r+w*b);
			Delays.sleep(10);
		}
		int[] PTRS = new int[p]; // the cbrt(n) partition pointers
		Writes.changeAllocAmount(p);
		Writes.deleteExternalArrays(Y, P);
		for(int i=p, E=R; i>=0; i--) {
			Writes.reversearraycopy(s, b*i, A, E-=S[i], S[i], 1, true, false);
			if(i > 0) {
				int w = b*B[i],
					w1 = b*B[i-1];
				E-=w-=w1;
				if(i > MB)
					Writes.reversearraycopy(A, r+w1, A, E, w, 1, true, false);
				Writes.write(PTRS, i-1, E, 1, true, true);
			}
		}
		Writes.deleteExternalArray(s); // no vis
		Writes.changeAllocAmount(-2*(p+1));
		if(!e) {
			Writes.changeAllocAmount(-p);
			return;
		}
		int z = r - 1;            // start of next partition
		for(int i=0; i<p; i++) {
			Writes.recursion();
			sort(A, z + 1, z = PTRS[i], bad);
		}
		Writes.recursion();
		sort(A, z + 1, R, bad);
		Writes.changeAllocAmount(-p);
	}

	@Override
	public void runSort(int[] arr, int length, int buckets) {
		this.sort(arr, 0, length, 0);
	}
}