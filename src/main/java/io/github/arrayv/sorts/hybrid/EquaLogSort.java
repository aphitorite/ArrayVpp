package io.github.arrayv.sorts.hybrid;


import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.main.ArrayVisualizer;


final public class EquaLogSort extends Sort {
	public EquaLogSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("EquaLog");
		this.setRunAllSortsName("Equal Optimized Logsort (EquaLogsort)");
		this.setRunSortName("EquaLog Sort");
		this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	private BinaryInsertionSort binaryInsert;
	
	private int productLog(int n) {
		int r = 1;
		while((r<<r)+r-1 < n) r++;
		return r;
	}
	
	private int medianOf3(int[] array, int... indices) {
		int swap;
		if(Reads.compareIndices(array, indices[0], indices[1], 0.125, true) > 0) {
			swap = indices[0];
			indices[0] = indices[1];
			indices[1] = swap;
		}
		if(Reads.compareIndices(array, indices[1], indices[2], 0.125, true) > 0) {
			swap = indices[1];
			indices[1] = indices[2];
			indices[2] = swap;
			if(Reads.compareIndices(array, indices[0], indices[1], 0.125, true) > 0) {
				return indices[0];
			}
		}
		return indices[1];
	}

	private int medianOf9(int[] array, int start, int end) {
		// anti-overflow with good rounding
		int  length  =  end - start;
		int	 half    =  length  / 2;
		int  quarter =	half    / 2;
		int  eighth  =  quarter / 2;
		
		int med0 = medianOf3(array, start, start + eighth, start + quarter);
		
		int med1 = medianOf3(array, start + quarter + eighth, start + half, start + half + eighth);
		
		int med2 = medianOf3(array, start + half + quarter, start + half + quarter + eighth, end - 1);
		
		return medianOf3(array, new int[] {med0, med1, med2});
	}

	private int mOMHelper(int[] array, int start, int length) {
		if(length == 1) return start;
		
		int[] meds = new int[3];
		
		int third = length / 3;
		
		meds[0] = mOMHelper(array, start, third);
		meds[1] = mOMHelper(array, start + third, third);
		meds[2] = mOMHelper(array, start + 2 * third, third);
				
		return medianOf3(array, meds);
	}

	private int medianOfMedians(int[] array, int start, int length) {
		if(length == 1) return start;
		
		int[] meds = new int[3];
		
		int nearPower = (int) Math.pow(3, Math.round(Math.log(length)/Math.log(3)) - 1);
		if(nearPower == length)
			return mOMHelper(array, start, length);
		
		// uncommon but can happen with numbers slightly smaller than 2*3^k
		// (e.g., 17 < 18 or 47 < 54)
		if(2*nearPower >= length) nearPower /= 3;
		
		meds[0] = mOMHelper(array, start, nearPower);
		meds[2] = mOMHelper(array, start + length - nearPower, nearPower);
		meds[1] = medianOfMedians(array, start + nearPower, length - 2 * nearPower);
		
		return medianOf3(array, meds);
	}
	
	private void multiSwap(int[] array, int a, int b, int s) {
		while(s-- > 0) Writes.swap(array, a++, b++, 1, true, false);
	}
	
	//@param pCmp - 0 for < piv, 1 for <= piv
	private boolean pivCmp(int v, int piv, int pCmp) {
		return Reads.compareValues(v, piv) < pCmp;
	}
	
	private void pivBufXor(int[] array, int pa, int pb, int v, int wLen) {
		while(wLen-- > 0) {
			if(v % 2 == 1) Writes.swap(array, pa+wLen, pb+wLen, 1, true, false);
			v /= 2;
		}
	}
	//@param bit - < pivot means this bit
	private int pivBufGet(int[] array, int pa, int piv, int bias, int wLen, int bit) {
		int r = 0;
		
		while(wLen-- > 0) {
			r *= 2;
			r |= (this.pivCmp(array[pa++], piv, bias) ? 0 : 1) ^ bit;
		}
		return r;
	}
	
	private void blockCycle(int[] array, int a, int b, int tag, int piv, int blk, int blog, int bias, int bit) {
		int i=a, j=0;
		for(; i<b-blk; i+=blk, j++) {
			int z = pivBufGet(array, i, piv, bias, blog, bit);
			while(z != j) {
				multiSwap(array, i, a+z*blk, blk);
				z = pivBufGet(array, i, piv, bias, blog, bit);
			}
			pivBufXor(array, i, tag+i-a, j, blog);
		}
		pivBufXor(array, i, tag+i-a, j, blog);
	}
	
	private int[] partition(int[] array, int[] aux, int a, int b, int piv, int bias) {
		final int blk = aux.length;
		boolean chkeq = false, chkop = true;
		int t, i, l = 0, r = 0, lb = 0, rb = 0;
		for(t = i = a; i < b; i++) {
			int cmp = Reads.compareValues(array[i], piv);
			chkeq = chkeq || cmp != 0;
			chkop = chkop && (!(bias == 0 ^ cmp < bias) || cmp == 0);
			if(cmp < bias) {
				if(t + l < i)
					Writes.write(array, t + l, array[i], 1, true, false);
				if(++l == blk) {
					t += blk;
					l = 0;
					lb++;
				}
			} else {
				Writes.write(aux, r, array[i], 1, true, true);
				if(++r == blk) {
					Writes.arraycopy(array, t, array, t+blk, l, 1, true, false);
					Writes.arraycopy(aux, 0, array, t, blk, 1, true, false);
					t += blk;
					r = 0;
					rb++;
				}
			}
		}
		int min = lb > rb ? rb : lb, m = a+lb*blk;
		if(min > 0) {
			int mlog = 32 - Integer.numberOfLeadingZeros(min-1);
			int j, k;
			for(i = 0, k = j = a - blk; i < min; i++) {
				do j += blk; while(!pivCmp(array[j+mlog], piv, bias));
				do k += blk; while(pivCmp(array[k+mlog], piv, bias));
				pivBufXor(array, j, k, i, mlog);
			}
			if(lb > rb) {
				for(i = j = a; i < t; i += blk) {
					if(pivCmp(array[i+mlog], piv, bias)) {
						multiSwap(array, i, j, blk);
						j += blk;
					}
				}
				blockCycle(array, m, t, a, piv, blk, mlog, bias, 1);
			} else {
				for(i = t - blk, j = t; i >= a; i -= blk) {
					if(!pivCmp(array[i+mlog], piv, bias)) {
						multiSwap(array, i, j -= blk, blk);
					}
				}
				blockCycle(array, a, m, m, piv, blk, mlog, bias, 0);
			}
		}
		Writes.arraycopy(aux, 0, array, b-r, r, 1, true, false);
		Writes.arraycopy(array, b-r-l, aux, 0, l, 1, true, false);
		if(l > 0)
			Writes.arraycopy(array, m, array, b-r-rb*blk, rb*blk, 1, true, false);
		Writes.arraycopy(aux, 0, array, a+lb*blk, l, 1, true, false);
		return new int[] {a + l + lb * blk, (chkeq ? 0 : 1) | (chkop ? 2 : 0)};
	}
	
	private int partEasy(int[] array, int[] aux, int[] partition, int a, int b, int piv, int bias) {
		int m = aux == partition ? 0 : a, len = b - a, l = 0;
		boolean chkeq = false;
		for(int i = 0; i < len; i++) {
			int cmp = Reads.compareValues(partition[m+i], piv);
			chkeq = chkeq || cmp != 0;
			if(cmp < bias) {
				Writes.write(array, a + l++, partition[m+i], 1, true, false);
			} else {
				Writes.write(aux, i - l, partition[m+i], 1, true, true);
			}
		}
		return chkeq ? l : -1;
	}
	
	private void easyStable(int[] array, int[] aux, int[] part, int a, int b, int depth) {
		Writes.recordDepth(depth++);
		int p, m;
		boolean bad = false;
		while(b - a > 16) {
			if(bad) {
				p = medianOfMedians(part, part==aux?0:a, b-a-(~(b-a)&1));
				bad = false;
			} else
				p = medianOf9(part, part==aux?0:a, part==aux?b-a:b);
			m = partEasy(array, aux, part, a, b, part[p], 0);
			if(m < 0) {
				Writes.arraycopy(aux, 0, array, a, b - a, 1, true, false);
				return;
			}
			if(m == 0) {
				m = partEasy(array, aux, part, a, b, part[p], 1);
			}
			int left = m, right = b - m - a;
			bad = left >= 8 * right || right >= 8 * left;
			
			Writes.recursion();
			easyStable(array, aux, aux, a + m, b, depth);
			b = a + m;
			part = array;
		}
		if(part == aux) {
			Writes.arraycopy(aux, 0, array, a, b - a, 1, true, false);
		}
		binaryInsert.customBinaryInsert(array, a, b, 1.25);
	}
	
	public void logsort(int[] array, int[] aux, int a, int b, int depth) {
		Writes.recordDepth(depth++);
		int p, pr[], m;
		boolean bad = false;
		while(b - a > Math.max(aux.length, 16)) {
			if(bad) {
				p = medianOfMedians(array, a, b-a-(~(b-a)&1));
				bad = false;
			} else
				p = medianOf9(array, a, b);
			pr = partition(array, aux, a, b, array[p], 1);
			m = pr[0];
			if((pr[1] & 1) > 0) // distinct check failed, break out of loop and skip past easy stable partition
				return;
			if((pr[1] & 2) > 0) { // left half only has one unique value, iterate on new sublist immediately
				a = m;
				continue;
			}
			if(m == b) { // pivot is highest rank
				// repartition with different bias
				pr = partition(array, aux, a, b, array[p], 0);

				b = pr[0]; // due to pivot, the right half only has one unique, so iterate on new sublist immediately
				continue;
			}
			int left = m - a, right = b - m;
			bad = left >= 8 * right || right >= 8 * left;
			if(left > right) {
				Writes.recursion();
				logsort(array, aux, m, b, depth);
				b = m;
			} else {
				Writes.recursion();
				logsort(array, aux, a, a = m, depth);
			}
		}
		easyStable(array, aux, array, a, b, depth);
	}
	
	public void logsort(int[] array, int a, int b, int depth) {
		int bLen = this.productLog(b-a);
		int[] aux = Writes.createExternalArray(bLen);
		binaryInsert = new BinaryInsertionSort(arrayVisualizer);
		logsort(array, aux, a, b, depth);
	}
	
	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		logsort(array, 0, length, 0);
	}
}