package io.github.arrayv.sorts.quick;

import static java.lang.Math.cbrt;
import static java.lang.Math.log;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public class PseudoPDPruneSort extends Sort {
	public PseudoPDPruneSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Pseudo-PD Prune");
		this.setRunAllSortsName("Pseudo-Pattern-Defeating Prune Sort");
		this.setRunSortName("Pseudo-PD Prunesort");
		this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
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
		
		int p = (int) cbrt(R-r),
			b = (int) 2 * log2(R-r),
			C = r, Z = 0;
		
		boolean e = false;
		
		int[] s = Writes.createExternalArray(b*(p+1)),
			  S = new int[p+1],
			  P = new int[p],
			  B = new int[p+1],
			  Y = Writes.createExternalArray((R-r-1)/b+1);
		Writes.changeAllocAmount(3*p+2);
		
		int D = (R - r) / p;
		
		for(int i = r, j = 0; j < p; i+=D, j++) {
			Writes.write(P, j, A[i], 1, true, false);
		}
		
		sort(P, 0, p, 0);
		
		for(int i=r; i<R; i++) {
			int L = 0, E = p, c = -2;
			while(L<E) {
				int M=L+(E-L)/2;
				if((c = Reads.compareIndexValue(A, i, P[M], 0.25, true)) >= 0) {
					L=M+1;
				} else {
					E=M;
				}
			}
			if(c != 0)
				e = true;
			if(S[L] >= b) {
				Writes.write(Y, Z++, s[L*b], 1, true, true);
				Writes.arraycopy(s, L*b+1, A, C+1, b-1, 1, true, false);
				Writes.write(A, C, L, 1, true, false);
				C += b;
				Writes.write(B, L, B[L] + 1, 1, true, true);
				Writes.write(s, L*b, A[i], 1, true, true);
				Writes.write(S, L, 1, 1, true, true);
			} else {
				Writes.write(s, L*b+S[L], A[i], 1, true, true);
				Writes.write(S, L, S[L] + 1, 1, true, true);
			}
		}
		for(int i=0, j=0; i<=p; i++) {
			int t = B[i];
			Writes.write(B, i, j, 1, true, true);
			j += t;
		}
		for(int i = r; i < C; i+=b) {
			Writes.write(B, A[i], B[A[i]] + 1, 1, true, true);
			Writes.write(A, i, B[A[i]] - 1, 1, true, false);
		}
		for(int i = r, w = 0; i < C; i+=b, w++) {
			while(Reads.compareOriginalValues(A[i], w) != 0) {
				Writes.swap(Y, w, A[i], 1, true, true);
				multiSwap(A, i, r+A[i]*b, b);
			}
			Writes.write(A, i, Y[w], 10, true, false);
		}
		int[] PTRS = new int[p+1];
		Writes.changeAllocAmount(p+1);
		Writes.deleteExternalArrays(Y, P);
		for(int i=p, E=R; i>=0; i--) {
			Writes.reversearraycopy(s, b*i, A, E-=S[i], S[i], 1, true, false);
			if(i > 0) {
				int w = b*B[i],
					w1 = b*B[i-1];
				Writes.reversearraycopy(A, r+w1, A, E-=w-=w1, w, 1, true, false);
				Writes.write(PTRS, i, E, 1, true, true);
			}
		}
		Writes.deleteExternalArray(s); // no vis
		Writes.changeAllocAmount(-2*(p+1));
		if(!e) {
			Writes.changeAllocAmount(-(p+1));
			return;
		}
		int z = r;
		for(int i=0; i<p; i++) {
			Writes.recursion();
			sort(A, z, z = PTRS[i + 1], bad);
		}
		Writes.recursion();
		sort(A, z, R, bad);
		Writes.changeAllocAmount(-(p+1));
	}

	@Override
	public void runSort(int[] arr, int length, int buckets) {
		this.sort(arr, 0, length, 0);
	}
}