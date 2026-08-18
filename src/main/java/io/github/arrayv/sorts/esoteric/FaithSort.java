package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class FaithSort extends Sort {
   public FaithSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Faith");
      this.setRunAllSortsName("Faith Sort");
      this.setRunSortName("Faithsort");
      this.setCategory("Esoteric Sorts"); // formerly Holy Sorts
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
   }
}
