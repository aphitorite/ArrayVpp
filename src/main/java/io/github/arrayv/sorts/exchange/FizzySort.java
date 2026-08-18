package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class FizzySort extends Sort {
   public FizzySort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Fizzy");
      this.setRunAllSortsName("Fizzy Sort");
      this.setRunSortName("Fizzysort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      int n = 0;
      int lastSwap = 0;

      for (int i = length; i > 0; i--) {
         for (int j = 0; j < i; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 0.75, true, false);
            } else {
               for (int k = j; k > 0; k--) {
                  if (this.Reads.compareValues(array[k], array[k - 1]) == -1) {
                     this.Writes.swap(array, k, k - 1, 0.75, true, false);
                  }

                  this.Highlights.markArray(1, k);
                  this.Highlights.markArray(2, k - 1);
                  this.Delays.sleep(0.025);
               }
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(0.025);
         }
      }
   }
}
