package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class PopSort extends Sort {
   public PopSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Pop");
      this.setRunAllSortsName("Pop Sort");
      this.setRunSortName("Popsort");
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

         for (int j = length / 4 - 1; j > i; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == 1) {
               this.Writes.swap(array, j, j - 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(0.025);
         }

         if (sorted) {
            break;
         }
      }

      for (int i = length / 2 - 1; i > length / 4 - 1; i--) {
         boolean sorted = true;

         for (int j = length / 4; j < i; j++) {
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

      for (int i = length / 2; i < length / 4 * 3 - 1; i++) {
         boolean sorted = true;

         for (int j = length / 4 * 3 - 1; j > i; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == 1) {
               this.Writes.swap(array, j, j - 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(0.025);
         }

         if (sorted) {
            break;
         }
      }

      for (int i = length - 1; i > length / 4 * 3 - 1; i--) {
         boolean sorted = true;

         for (int j = length / 4 * 3; j < i; j++) {
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

         for (int j = length / 2 - 1; j > i; j--) {
            if (this.Reads.compareValues(array[j], array[j - 1]) == 1) {
               this.Writes.swap(array, j, j - 1, 0.075, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j - 1);
            this.Delays.sleep(0.025);
         }

         if (sorted) {
            break;
         }
      }

      for (int i = length - 1; i > 0; i--) {
         boolean sorted = true;

         for (int j = length / 2; j < i; j++) {
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
