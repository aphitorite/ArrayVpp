package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.ScrollSorting;

public final class ScrollSortInPlace extends ScrollSorting {
    public ScrollSortInPlace(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Scroll (In-Place)");
        this.setRunAllSortsName("Scroll Sort (In-Place)");
        this.setRunSortName("Scroll Sort (In-Place)");
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
    public void runSort(int[] array, int length, int bucketCount) {
        scrollSort(array, length);
    }
}
