package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class DeadGnomeSort extends Sort {
   public DeadGnomeSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Dead Gnome");
      this.setRunAllSortsName("Dead Gnome (sort)");
      this.setRunSortName("Dead Gnome (sort)");
      this.setCategory("Exchange Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private void smartGnomeSort(int[] array, int lowerBound, int upperBound, double sleep) {
      int pos = upperBound;

      while (pos > lowerBound && this.Reads.compareValues(array[pos - 1], array[pos]) == 1) {
         this.Writes.swap(array, pos - 1, pos, sleep, true, false);
         this.smartGnomeSort(array, lowerBound, --pos, sleep);
      }

      while (pos > lowerBound) {
         this.smartGnomeSort(array, lowerBound, --pos, sleep);
      }
   }

   public void customSort(int[] array, int low, int high, double sleep) {
      for (int i = low + 1; i < high; i++) {
         this.smartGnomeSort(array, low, i, sleep);
      }
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      for (int i = 1; i < length; i++) {
         this.smartGnomeSort(array, 0, i, 0.05);
      }
   }
}
