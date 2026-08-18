package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class CashewSort extends Sort {
   public CashewSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Cashew");
      this.setRunAllSortsName("Cashew Sort");
      this.setRunSortName("Cashew Sort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private void smartCashew(int[] array, int start, int end, double sleep) {
      for (int i = start; i < end / 2 + start; i++) {
         boolean sorted = true;

         for (int j = i; j < end + start - i - 1; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, sleep, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(sleep / 2.0);
         }

         for (int j = i; j < end + start - i - 1; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, sleep, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(sleep / 2.0);
         }

         for (int j = end + start - i - 1; j > i; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == -1) {
               this.Writes.swap(array, j, j - 1, sleep, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(sleep / 2.0);
         }

         for (int j = end + start - i - 1; j > i; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == -1) {
               this.Writes.swap(array, j, j - 1, sleep, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(sleep / 2.0);
         }

         if (sorted) {
            break;
         }

         i++;
      }
   }

   public void customSort(int[] array, int start, int end) {
      this.smartCashew(array, start, end, 1.0);
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      this.smartCashew(array, 0, length, 0.1);
   }
}
