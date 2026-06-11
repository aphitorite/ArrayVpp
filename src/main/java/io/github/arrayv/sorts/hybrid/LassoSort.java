package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.LassoSorting;


final public class LassoSort extends LassoSorting {
    public LassoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Lasso");
        this.setRunAllSortsName("Lasso Sort");
        this.setRunSortName("Lassosort");
        this.setCategory("Hybrid Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	this.common(array, 0, length);
    }
}