package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;

final public class DualPivotTernaryRotateQuickSort extends Sort {
    public DualPivotTernaryRotateQuickSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Dual-Pivot Ternary Rotate Quick");
        this.setRunAllSortsName("Dual-Pivot Ternary Rotate Quick Sort");
        this.setRunSortName("Dual-Pivot Ternary Rotate Quicksort");
        this.setCategory("Quick Sorts");
		this.setAuthors("Distray");
		this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private int[] DPRP(int[] array, int start, int end, int pivotlow, int pivothigh, int depth) {
        // track depth
        Writes.recursion();
        Writes.recordDepth(depth++);
        
        // partition case
        if(end <= start) {
            int[] court = new int[4];
            int c = Reads.compareValues(array[start], pivotlow);
            if(c >= 0) {
                if(c > 0) {
                    int c2 = Reads.compareValues(array[start], pivothigh);
                    court[2*c+c2]++;
                } else {
                    court[c]++;
                }
            }
            return court;
        }
        
        // in-run recursion case
        int mid = start + (end - start) / 2;
        int[] l = DPRP(array, start, mid, pivotlow, pivothigh, depth),
              r = DPRP(array, mid+1, end, pivotlow, pivothigh, depth);
        int lhg  = mid-l[3],           // left side: higher than pivothigh
            lhe  = lhg-l[2],           // left side: equal to pivothigh
            llg  = lhe-l[1],           // left side: higher than pivotlow, less than pivothigh
            lle  = llg-l[0],           // left side: equal to pivotlow
            hhg  = end-r[3],           // right side: higher than pivothigh
            hhe  = hhg-r[2],           // right side: equal to pivothigh
            hlg  = hhe-r[1],           // right side: higher than pivotlow, less than pivothigh
            hle  = hlg-r[0],           // right side: equal to pivotlow
            hlre = hle-l[3]-l[2]-l[1], // rotatecase: rotated index of llh~lgh
            hlrg = hlg-l[3]-l[2],      // rotatecase: rotated index of lge~lgh
            hhre = hhe-l[3];           // rotatecase: rotated index of lgh

        // stably weave the ten ternary partitions together
        IndexedRotations.tripleShift(array, lle+1,  mid+1, hle+1, 0.25, true, false);
        IndexedRotations.tripleShift(array, hlre+1, hle+1, hlg+1, 0.25, true, false);
        IndexedRotations.tripleShift(array, hlrg+1, hlg+1, hhe+1, 0.25, true, false);
        IndexedRotations.tripleShift(array, hhre+1, hhe+1, hhg+1, 0.25, true, false);
        
        // add the two lists together
        return new int[] {l[0]+r[0], l[1]+r[1], l[2]+r[2], l[3]+r[3]};
    }
    private int[] SPRP(int[] array, int start, int end, int pivot, int depth) {
        // track depth
        Writes.recursion();
        Writes.recordDepth(depth++);
        
        // partition case
        if(end <= start) {
            int[] court = new int[2];
            int c = Reads.compareValues(array[start], pivot);
            if(c >= 0) {
                court[c]++;
            }
            return court;
        }
        
        // in-run recursion case
        int mid = start + (end - start) / 2;
        int[] l = SPRP(array, start, mid, pivot, depth),
              r = SPRP(array, mid+1, end, pivot, depth);
        int lh  = mid-l[1], // left side: higher than pivot
            ll  = lh-l[0],  // left side: equal to pivot
            hh  = end-r[1], // right side: higher than pivot
            hl  = hh-r[0],  // right side: equal to pivot
            hrl = hl-l[1];  // rotatecase: rotated index of lh

        // stably weave the six ternary partitions together
        IndexedRotations.tripleShift(array, ll+1, mid+1, hl+1, 0.25, true, false);
        IndexedRotations.tripleShift(array, hrl+1, hl+1, hh+1, 0.25, true, false);

        // add the two lists together
        return new int[] {l[0]+r[0], l[1]+r[1]};
    }
    public void quicksort(int[] array, int start, int end, int depth) {
        // no elements to sort
        if(start >= end) {
            return;
        }
        
        Writes.recursion();
        Writes.recordDepth(depth++);
        
        // get pivots
        int third = (end-start)/3;
        int pl = array[start+third], pr = array[end-third-1];
        int[] partition;
        switch(Reads.compareValues(pl, pr)) {
            case 1:
                int t = pl;
                pl = pr;
                pr = t;
                break;
            case 0:
                // equal pivots single partition case (avoid comparison wasting)
                if(end-start > 2) {
                    partition = SPRP(array, start, end-1, pl, depth);
                    quicksort(array, start, end-partition[1]-partition[0], depth);
                    quicksort(array, end-partition[1], end, depth);
                }
                return;
        }
        
        // very small case
        if(end-start == 2) {
            Writes.write(array, start, pl, 1, true, false);
            Writes.write(array, end-1, pr, 1, true, false);
            return;
        }
        
        // do a dual-pivot partition
        partition = DPRP(array, start, end-1, pl, pr, depth);
        
        // a few macros for ease of reading
        int eqlo = partition[0],
            gtlo = partition[1],
            eqhi = partition[2],
            gthi = partition[3];
        
        // recurse
        quicksort(array, start, end-eqlo-gtlo-eqhi-gthi, depth);
        quicksort(array, end-gtlo-eqhi-gthi, end-eqhi-gthi, depth);
        quicksort(array, end-gthi, end, depth);
    }
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
        quicksort(array, 0, currentLength, 0);
    }
}