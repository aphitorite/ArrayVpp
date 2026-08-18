package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class SinglePassIndexQuickSort2 extends Sort {
	public SinglePassIndexQuickSort2(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Single-Pass Index Quick II");
		this.setRunAllSortsName("Single-Pass Index Quick Sort II");
		this.setRunSortName("Single-Pass Index Quicksort II");
		this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	private void indexSort(int[] array, int[] idx, int a, int b) {
		while(a < b) {
			int t = idx[a], s = 0, tmp = array[a];
			while(Reads.compareOriginalValues(a, t) != 0) {
				int tmp2 = array[t], t2 = idx[t];
				Writes.write(array, t, tmp, 1, true, false);
				Writes.write(idx, t, t, 1, true, true);
				t = t2;
				tmp = tmp2;
				s++;
			}
			if(s > 0) {
				Writes.write(array, a, tmp, 1, true, false);
				// Writes.write(idx, a, a, 1, true, true);
			}
			a++;
		}
	}
	
	private int medianOf3(int[] array, int... indices) {
		// 3 element case (only one triggered, other cases removed)
		// only first 3 elements are considered if given an array of 4+ indices
		int tmp;
		if(Reads.compareIndices(array, indices[0], indices[1], 0.125, true) > 0) {
			tmp = indices[1];
			indices[1] = indices[0];
		} else
			tmp = indices[0];
		if(Reads.compareIndices(array, indices[1], indices[2], 0.125, true) > 0) {
			if(Reads.compareIndices(array, tmp, indices[2], 0.125, true) > 0) {
				return tmp;
			}
			return indices[2];
		}
		return indices[1];
	}

	private int medianOf9(int[] array, int start, int end) {
		// anti-overflow with good rounding
		int  length  =  end - start;
		int	 half    =  length / 2;
		int  quarter =	half / 2;
		int  eighth  =  quarter / 2;
		
		int med0 = medianOf3(array, start, start + eighth, start + quarter);
		
		int med1 = medianOf3(array, start + quarter + eighth, start + half, start + half + eighth);
		
		int med2 = medianOf3(array, start + half + quarter, start + half + quarter + eighth, end - 1);
		
		return medianOf3(array, med0, med1, med2);
	}

	private int mOMHelper(int[] array, int start, int length, int d) {
		if(length == 1) return start;
		Writes.recordDepth(d++);
		
		int[] meds = new int[3];
		
		int third = length / 3;
		
		Writes.recursion(3);
		meds[0] = mOMHelper(array, start, third, d);
		meds[1] = mOMHelper(array, start + third, third, d);
		meds[2] = mOMHelper(array, start + 2 * third, third, d);
				
		return medianOf3(array, meds);
	}

	private int medianOfMedians(int[] array, int start, int length, int d) {
		if(length == 1) return start;
		
		int[] meds = new int[3];
		
		int nearPower = (int) Math.pow(3, Math.round(Math.log(length)/Math.log(3)) - 1);
		if(nearPower == length)
			return mOMHelper(array, start, length, d);
		
		// uncommon but can happen with numbers slightly smaller than 2*3^k
		// (e.g., 17 < 18 or 47 < 54)
		if(2*nearPower >= length) nearPower /= 3;
		
		meds[0] = mOMHelper(array, start, nearPower, d);
		meds[2] = mOMHelper(array, start + length - nearPower, nearPower, d);
		meds[1] = medianOfMedians(array, start + nearPower, length - 2 * nearPower, d);
		
		return medianOf3(array, meds);
	}
	
	private int odd(int v) {
		return v-=~v&1;
	}
	
	private void sort(int[] array, int[] idx, int a, int b, int cmp, int d) {
		int p, f = 0, lp = 0;
		// cmp: [0] bias, [1] bad pivot, [2] antistable
		Writes.recordDepth(d++);
		while(b-a > 2) {
			p = a;
			if(f == 0)
				lp = (cmp & 2) > 0 ? medianOfMedians(array, a, odd(b-a), d) : medianOf9(array, a, b);
			int piv = array[lp];
			for(int i = a, q = b; i < b; i++) {
				if(Reads.compareIndexValue(array, i, piv, 1, true) < (cmp & 1)) {
					Writes.write(idx, i, p++, 0, true, true);
				} else {
					Writes.write(idx, i, --q, 0, true, true);
				}
			}
			if(p == b || p == a) {
				if(f == 1) {
					if(cmp > 3)
						Writes.reversal(array, a, b-1, 1, true, false);
					return;
				}
				f = 1;
				cmp ^= 1;
				continue;
			}
			int l = p-a, r = b-p;
			cmp = (cmp & 5) | ((l>=8*r || r>=8*l) ? 2 : 0);
			f = 0;
			indexSort(array, idx, a, b);
			if(l < r) {
				Writes.recursion();
				sort(array, idx, a, a = p, cmp, d);
				cmp ^= 4;
			} else {
				Writes.recursion();
				sort(array, idx, p, b, cmp ^ 4, d);
				b = p;
			}
		}
		if(b-a == 2) {
			if(Reads.compareIndices(array, a, a+1, 1, true) > (cmp > 3 ? -1 : 0))
				Writes.swap(array, a, a+1, 1, true, false);
		}
	}
	
	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		int[] idx = Writes.createExternalArray(length);
		this.sort(array, idx, 0, length, 0, 0);
		Writes.deleteExternalArray(idx);
	}
}