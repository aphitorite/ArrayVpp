package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.ScrollSorting;

public final class ScrollSortOutOfPlace extends ScrollSorting {
    public ScrollSortOutOfPlace(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Scroll (Out-of-Place)");
        this.setRunAllSortsName("Scroll Sort (Out-of-Place)");
        this.setRunSortName("Scroll Sort (Out-of-Place)");
        this.setCategory("Hybrid Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("no-op");
        this.setConstant("n log n");
    }

    @Override
    protected boolean auxWrite() {
        return true;
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        int maxPos = 0;
        for (int i = 1; i < length; i++) {
            if (Reads.compareIndices(array, i, maxPos, DELAY, true) > 0) {
                maxPos = i;
            }
        }

        int maxVal = array[maxPos];

        int m = 0;
        for (int i = 0; i < length; i++) {
            if (Reads.compareIndexValue(array, i, maxVal, DELAY, true) < 0) {
                Writes.swap(array, m, i, SWAP_DELAY, true, false);
                m++;
            }
        }
        Highlights.clearMark(2);

        if (m <= 1) return;

        int n = m + scrollBufferReq(m) + 1;
        int[] aux = Writes.createExternalArray(n);

        for (int i = 0; i < m; i++) {
            Writes.write(aux, i, array[i], DELAY, true, true);
        }
        for (int i = m; i < n; i++) {
            Writes.write(aux, i, maxVal, DELAY, true, true);
        }

        int bvCapacity = scrollBitVectorReq(m);
        ScrollBitVector bv = new BitVectorEasy(bvCapacity, ceilLog2(m));
        KWayMergeSort smallSort = new KWayMergeSort(gValue(m));

        new Scroll(aux, 0, m, m + 1, n, bv, smallSort);

        for (int i = 0; i < m; i++) {
            Writes.write(array, i, aux[i], DELAY, true, false);
        }

        Writes.deleteExternalArray(aux);
    }
}
