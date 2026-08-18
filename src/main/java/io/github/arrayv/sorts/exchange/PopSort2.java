package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class PopSort2 extends Sort {
   public PopSort2(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Pop 2");
      this.setRunAllSortsName("Pop Sort 2");
      this.setRunSortName("Popsort 2");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setConstant("n^2");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      for (int i = 0; i < length / 4 - 1; i++) {
         boolean sorted = true;

         for (int j = length / 4 - 1; j > 0; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == 1) {
               this.Writes.swap(array, j, j - 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(0.025);
         }

         for (int j = length / 4; j < length / 2 - 1; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(0.025);
         }

         for (int j = length / 4 * 3 - 1; j > length / 2; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == 1) {
               this.Writes.swap(array, j, j - 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(0.025);
         }

         for (int j = length / 4 * 3; j < length - 1; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(0.025);
         }

         if (sorted) {
            break;
         }
      }

      for (int i = 0; i < length / 2 - 1; i++) {
         boolean sorted = true;

         for (int j = length / 2 - 1; j > 0; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == 1) {
               this.Writes.swap(array, j, j - 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(0.025);
         }

         for (int j = length / 2; j < length - 1; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(0.025);
         }

         if (sorted) {
            break;
         }
      }

      for (int i = length - 1; i > 0; i--) {
         boolean sorted = true;

         for (int j = 0; j < i; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(0.025);
         }

         if (sorted) {
            break;
         }
      }
   }
}
