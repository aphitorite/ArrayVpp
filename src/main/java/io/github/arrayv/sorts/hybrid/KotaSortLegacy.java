package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.KotaSortingLegacy;

public final class KotaSortLegacy extends KotaSortingLegacy {
    public KotaSortLegacy(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Kota (Legacy)");
        //this.setRunAllID("Grail Sort (Block Merge Sort)"); // ?
        //this.setRunAllSortsName("Grail Sort [Block Merge Sort]");
        this.setRunAllSortsName("Kotasort (Legacy)");
        this.setRunSortName("Kotasort (Legacy)");
        this.setCategory("Block Merge Sorts");
	    this.setAuthors("aphitorite");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        if(Delays.getSleepRatio() == 55.1) {
            Delays.setSleepRatio(1);
            this.kotaSortDynamicBuf(array, 0, length);
        }
        else {
            this.kotaSort(array, 0, length);
        }
    }
}
