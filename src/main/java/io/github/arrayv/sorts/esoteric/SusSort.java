package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class SusSort extends BogoSorting {
   public SusSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Sus");
      this.setRunAllSortsName("Suspicious Sort");
      this.setRunSortName("Sussort");
      this.setCategory("Esoteric Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   public void pull(int[] array, int start, int end) {
      if (start > end) {
         for (int i = start; i > end; i--) {
            this.Writes.swap(array, i, i - 1, 0.25, true, false);
         }
      }

      if (end > start) {
         for (int i = start; i < end; i++) {
            this.Writes.swap(array, i, i + 1, 0.25, true, false);
         }
      }
   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
      while (!this.isArraySorted(array, currentLength)) {
         int i = randInt(0, currentLength);
         this.pull(array, i, currentLength);
         this.arrayVisualizer.setCurrentLength(--currentLength);
         if (!this.isArraySorted(array, currentLength)) {
            continue;
         }
         break;
      }
   }
}
