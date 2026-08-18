package io.github.arrayv.sorts.merge;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.IndexedRotations;
import io.github.arrayv.sorts.templates.Sort;

final public class TrueStoogeMergeSort extends Sort {
    public TrueStoogeMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("True Stooge Merge");
        this.setRunAllSortsName("True Stooge Merge Sort");
        this.setRunSortName("True Stooge Mergesort");
        this.setCategory("Merge Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private void head(int[] array, int[] tmp, int start, int mid, int end) {
    	if(start >= mid || mid >= end)
    		return;
    	Writes.arraycopy(array, start, tmp, 0, mid-start, 0.5, true, true);
    	int[][] table = new int[][] {tmp, array};
    	int[] ptrs = new int[] {0, mid},
    		  vals = new int[] {mid-start, end-mid};
    	int cmp, to = start;
    	while(vals[0] > 0 && vals[1] > 0) {
    		cmp = -(Reads.compareValues(array[ptrs[1]], tmp[ptrs[0]]) >> 31);
    		Highlights.markArray(table[cmp], 2, ptrs[cmp]);
    		Highlights.colorCode(to, "headmerge");
    		Writes.write(array, to++, table[cmp][ptrs[cmp]++], 1, true, false);
    		--vals[cmp];
    	}
    	while(vals[0] > 0) {
    		Highlights.colorCode(to, "headmerge");
    		Writes.write(array, to++, tmp[ptrs[0]++], 1, true, false);
    		--vals[0];
    	}
    }
    private void tail(int[] array, int[] tmp, int start, int mid, int end) {
    	if(start >= mid || mid >= end)
    		return;
    	Writes.arraycopy(array, mid, tmp, 0, end-mid, 0.5, true, true);
    	int[][] table = new int[][] {array, tmp};
    	int[] ptrs = new int[] {mid-1, (end-mid)-1},
    		  vals = new int[] {mid-start, end-mid};
    	int cmp, to = end;
    	while(vals[0] > 0 && vals[1] > 0) {
    		cmp = -((Reads.compareValues(array[ptrs[0]], tmp[ptrs[1]])-1) >> 31);
    		Highlights.markArray(table[cmp], 2, ptrs[cmp]);
    		Writes.write(array, --to, table[cmp][ptrs[cmp]--], 1, true, false);
    		Highlights.colorCode(to, "tailmerge");
    		--vals[cmp];
    	}
    	while(vals[1] > 0) {
    		Writes.write(array, --to, tmp[ptrs[1]--], 1, true, false);
    		Highlights.colorCode(to, "tailmerge");
    		--vals[1];
    	}
    }
    int sig(int a, int b, int d) {
    	return ((a + b) + d * Math.abs(a - b)) / 2;
    }
    private void segRev(int[] array, int start, int end) {
        int i = start;
        int left;
        int right;
        while (i < end) {
            left = i;
            while (Reads.compareIndices(array, i, i + 1, 0.25, true) == 0 && i < end) i++;
            right = i;
            if (left != right) {
                if (right - left < 3) Writes.swap(array, left, right, 0.75, true, false);
                else Writes.reversal(array, left, right, 0.75, true, false);
            }
            i++;
        }
    }
    private int findRun(int[] array, int start, int end) { // May figure out a way to incorporate run finding into this
    	if(start >= end - 1)
    		return start + 1;
    	int cmp = -Reads.compareIndices(array, start++, start, 1, true),
    		k = start - 1, d;
    	boolean lUniq = false;
    	if(cmp==0) {cmp++; lUniq=true;}
    	do {
    		d = Reads.compareIndices(array, start++, start, 1, true);
    		lUniq = lUniq || d == 0;
    	} while(start < end && d != cmp);
    	int m = (start - k) / 2,
    		q = sig(k, start-1, -cmp);
    	for(int i=0; i<m; i++) {
    		Writes.swap(array, k+i, q+cmp*i, 1, true, false);
    	}
    	if(lUniq&&cmp==-1)
    		segRev(array, k, start-1);
    	return start;
    }
    private int bin(int[] array, int start, int end, int key) {
    	while(start < end) {
    		int mid = start + (end - start) / 2;
    		Highlights.colorCode(mid, "binsearch");
    		Highlights.markArray(3, mid);
    		Delays.sleep(0.75);
    		if(Reads.compareValues(array[mid], key) > 0) {
    			end = mid;
    		} else {
    			start = mid + 1;
    		}
    	}
    	Highlights.clearMark(3);
    	return start;
    }
    private void insert(int[] array, int start, int end) {
    	for(int i=start+1; i<end; i++) {
    		if(Reads.compareIndices(array, i-1, i, 1, true) <= 0)
    			continue;
    		Highlights.colorCode(i, "insert");
    		int j = i-1, temp = array[i];
    		do {
    			Writes.write(array, j+1, array[j], 1, true, false);
    			j--;
    		} while(j >= start && Reads.compareValues(array[j], temp) > 0);
    		Writes.write(array, j+1, temp, 1, true, false);
    	}
    }
    private boolean stoogeMerge(int[] array, int[] tmp, int start, int end) {
    	if(end-start < 1) {
    		return false;
    	}
    	if(end-start < 4) {
    		insert(array, start, end);
    		return true;
    	}
    	int mid1 = (2*start+end)/3,
    		mid2 = (2*(end+1)+start)/3;
    	boolean l = stoogeMerge(array, tmp, start, mid2),
    			r = stoogeMerge(array, tmp, mid1, end);
    	if(!r || Reads.compareValues(array[mid1-1], array[mid1]) <= 0) {
    		return l;
    	}
    	if(Reads.compareValues(array[start], array[mid2-1]) > 0) {
    		IndexedRotations.cycleReverse(array, start, mid1, mid2, 1, true, false);
    		return l || r;
    	}
    	int right = bin(array, mid1, mid2, array[mid1-1]),
    		left = bin(array, start, mid1, array[mid1]);
    	if(mid1-left <= right-mid1) {
    		head(array, tmp, left, mid1, right);
    	} else {
    		tail(array, tmp, left, mid1, right);
    	}
    	return l || r;
    }
    public void sort(int[] array, int start, int end) {
    	int[] tmp = Writes.createExternalArray((end-start+2)/3);
    	Highlights.retainColorMarks(true);
    	Highlights.defineColor("headmerge", Color.ORANGE);
    	Highlights.defineColor("tailmerge", new Color(0, 180, 255));
    	Highlights.defineColor("binsearch", new Color(130, 0, 255));
    	Highlights.defineColor("insert", Color.YELLOW);
    	stoogeMerge(array, tmp, start, end);
    	Writes.deleteExternalArray(tmp);
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	sort(array, 0, length);
    }
}