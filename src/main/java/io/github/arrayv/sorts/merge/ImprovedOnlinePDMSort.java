package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class ImprovedOnlinePDMSort extends Sort {
    public ImprovedOnlinePDMSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Improved Online PDM");
        this.setRunAllSortsName("Improved Online Pattern-Defeating Merge Sort");
        this.setRunSortName("Improved Online PDMsort");
        this.setCategory("Merge Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private void segRev(int[] array, int start, int end, boolean maux) {
        int i = start;
        int left;
        int right;
        while (i < end) {
            left = i;
            while (i < end && Reads.compareIndices(array, i, i + 1, 0.25, true) == 0) i++;
            right = i;
            if (left != right) {
                if (right - left < 3) Writes.swap(array, left, right, 0.75, true, maux);
                else Writes.reversal(array, left, right, 0.75, true, maux);
            }
            i++;
        }
    }
    private int findRun(int[] array, int a, int b, boolean aux) {
    	int A = a;
    	if(++a >= b) {
    		if(a > b)
    			return 0;
    		return 1;
    	}
    	int c = Reads.compareIndices(array, a-1, a, 1, true), d = 0, uq = c == 0 ? 1 : 0;
    	while(++a < b) {
    		d = Reads.compareIndices(array, a-1, a, 1, true);
    		uq |= d == 0 ? 1 : 0;
    		if(c == 0) c = d;
    		else if(d == -c) break;
    	}
    	if(c == 0) c--;
    	if(uq == c) segRev(array, A, a-1, aux);
    	return (a - A) * -c;
    }
    private void mergehead(int[] array, int[] tmp, int a, int m, int b, int offstmp, boolean downl, boolean downr, boolean aux) {
    	Writes.arraycopy(array, a, tmp, offstmp, m - a, 1, true, !aux);
    	boolean downL = downl ^ downr;
    	int l = offstmp + (downL ? m - a - 1 : 0), le = offstmp + (downL ? 0 : m - a), d = downL ? -1 : 1, r = m;
    	while((l < le ^ downL) && r < b) {
    		if(Reads.compareValueIndex(array, tmp[l], r, 0.5, true) > 0 ^ downr) {
    			Writes.write(array, a++, array[r++], 1, true, aux);
    		} else {
    			Writes.write(array, a++, tmp[l], 1, true, aux);
    			l += d;
    		}
    	}
    	while(l < le ^ downL) {
			Writes.write(array, a++, tmp[l], 1, true, aux);
			l += d;
    	}
    }
    private void mergetail(int[] array, int[] tmp, int a, int m, int b, int offstmp, boolean downl, boolean downr, boolean aux) {
    	Writes.arraycopy(array, m, tmp, offstmp, b - m, 1, true, !aux);
    	boolean downR = downl ^ downr;
    	int l = m - 1, r = offstmp + (downR ? 0 : b - m - 1), re = offstmp + (downR ? b - m : 0), d = downR ? 1 : -1;
    	while(l >= a && (r >= re ^ downR)) {
    		if(Reads.compareIndexValue(array, l, tmp[r], 0.5, true) > 0 == downl) {
    			Writes.write(array, --b, tmp[r], 1, true, aux);
    			r += d;
    		} else {
    			Writes.write(array, --b, array[l--], 1, true, aux);
    		}
    	}
    	while(r >= re ^ downR) {
			Writes.write(array, --b, tmp[r], 1, true, aux);
			r += d;
    	}
    }
    public int merge(int[] array, int[] tmp, int offstmp, int a, int b, int run, int depth, boolean aux) {
    	if(run < 0) run = b - a;
    	Writes.recordDepth(depth++);
    	if(run < 3) {
    		return findRun(array, a, b, aux);
    	}
    	Writes.recursion();
    	int l, la = Math.abs(l = merge(array, tmp, offstmp, a, b, run>>1, depth, aux)), r, ra;
    	if(l != 0) { // recursion limiter
        	Writes.recursion();
    		ra = Math.abs(r = merge(array, tmp, offstmp, a+la, b, run>>1, depth, aux));
    	} else ra = r = 0;
    	int pa = ra, p = r;
		while(la / 4 > ra && pa > 0) { // for halves
    		pa = Math.abs(p = merge(array, tmp, offstmp, a+la+ra, b, la, depth, aux));
    		if(pa == 0) break;
			if(ra > pa) {
				mergetail(array, tmp, a + la, a + la + ra, a + la + ra + pa, offstmp, r < 0, p < 0, aux);
				r = r < 0 ? r - pa : r + pa;
			} else {
				mergehead(array, tmp, a + la, a + la + ra, a + la + ra + pa, offstmp, r < 0, p < 0, aux);
				r = p < 0 ? p - ra : p + ra;
			}
			ra += pa;
		}
		if(r != 0)
			if(la > ra) {
				mergetail(array, tmp, a, a += la, a += ra, offstmp, l < 0, r < 0, aux);
				return l < 0 ? l - ra : l + ra;
			} else {
				mergehead(array, tmp, a, a += la, a += ra, offstmp, l < 0, r < 0, aux);
				return r < 0 ? r - la : r + la;
			}
    	return l;
    }
    public void sort(int[] array, int start, int end) {
    	int[] tmp = Writes.createExternalArray((end-start+1)/2);
    	if(merge(array, tmp, 0, start, end, end-start, 0, false) < 0) {
    		Writes.reversal(array, start, end-1, 1, true, false);
    	}
    	Writes.deleteExternalArray(tmp);
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	sort(array, 0, length);
    }
}