package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.esoteric.DeadGnomeSort;
import io.github.arrayv.sorts.esoteric.GradualStoogeSort;
import io.github.arrayv.sorts.esoteric.HorrorSort;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class NevermoreSort extends BogoSorting {
   DeadGnomeSort sort = new DeadGnomeSort(this.arrayVisualizer);
   GradualStoogeSort sort2 = new GradualStoogeSort(this.arrayVisualizer);
   HorrorSort sort3 = new HorrorSort(this.arrayVisualizer);

   public NevermoreSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Nevermore");
      this.setRunAllSortsName("Nevermore Sort");
      this.setRunSortName("Nevermoresort");
      this.setCategory("Grossly Impractical Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private void horrorSort(int[] array, int start, int length) {
      for (int k = length; k > start; k--) {
         this.sort3.bitonicSort(array, start, length - start, randBoolean());
      }

      this.Writes.reversal(array, start, length - 1, 1.0, true, false);
   }

   public void faithSort(int[] array, int length) {
   }

   public void bubble(int[] array, int length) {
      for (int i = length - 1; i > 0; i--) {
         boolean sorted = true;

         for (int j = 0; j < i; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 7.5, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(2.5);
         }

         if (sorted) {
            break;
         }
      }
   }

   public void singleRoutine(int[] array, int start, int end, int length) {
      int mid = (end - start) / 2 + start;
      if (end - start > Math.sqrt(length)) {
         this.singleRoutine(array, start, mid, length);
         this.singleRoutine(array, mid, end, length);
      } else {
         this.sort2.stoogeSort(array, start, end);
      }

      while (!this.isRangePartitioned(array, start, mid, end)) {
         int j = start;

         for (int i = end; i > j; j++) {
            if (this.Reads.compareIndices(array, j, i, 0.5, true) > 0) {
               this.Writes.swap(array, i, j, 0.5, true, false);
            }

            i--;
         }

         this.bogoSwap(array, start, mid, false);
         this.bogoSwap(array, mid, end, false);
      }

      if (end - start > Math.sqrt(length)) {
         this.singleRoutine(array, mid, end, length);
         this.singleRoutine(array, start, mid, length);
      } else {
         this.sort2.stoogeSort(array, start, end);
      }
   }

   @Override
   public void runSort(int[] array, int sortLength, int bucketCount) {
      this.singleRoutine(array, 0, sortLength - 1, sortLength);
      this.sort.customSort(array, 0, sortLength, 0.5);
      this.faithSort(array, sortLength);
   }
}
