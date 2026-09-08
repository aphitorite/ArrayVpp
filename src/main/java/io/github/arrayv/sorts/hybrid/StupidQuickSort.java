package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class StupidQuickSort extends Sort {
   public StupidQuickSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Stupid Quick");
      this.setRunAllSortsName("Stupid Quick Sort");
      this.setRunSortName("Stupid Quicksort");
      this.setCategory("Hybrid Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      int times = (int)Math.sqrt(length);

      for (int count = 0; count < times; count++) {
         int i = 0;
         int j = length - 1;
         int pivotPos = (int)(Math.random() * length);
         int pivot = array[pivotPos];

         while (i < j) {
            while (this.Reads.compareValues(array[i], pivot) == -1) {
               this.Highlights.markArray(1, ++i);
               this.Delays.sleep(1.0);
            }

            while (this.Reads.compareValues(array[j], pivot) == 1) {
               this.Highlights.markArray(2, --j);
               this.Delays.sleep(1.0);
            }

            if (i < j) {
               if (i == pivotPos) {
                  this.Highlights.markArray(3, j);
               }

               if (j == pivotPos) {
                  this.Highlights.markArray(3, i);
               }

               this.Writes.swap(array, i, j, 1.0, true, false);
               i++;
               j--;
            }
         }
      }

      this.Highlights.clearMark(2);
      this.Highlights.clearMark(3);
      InsertionSort insertSorter = new InsertionSort(this.arrayVisualizer);
      insertSorter.customInsertSort(array, 0, length, 0.4, false);
   }
}
