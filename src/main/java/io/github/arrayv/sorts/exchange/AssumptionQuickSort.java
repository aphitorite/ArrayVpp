package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class AssumptionQuickSort extends BogoSorting {
   public AssumptionQuickSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Assumption Quick");
      this.setRunAllSortsName("Assumption Quick Sort");
      this.setRunSortName("Assumption Quicksort");
      this.setCategory("Quick Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private int partition(int[] array, int lo, int hi) {
      if (this.isRangeSorted(array, lo, hi + 1)) {
         return -1;
      } else {
         int i = lo;
         int lowestindex = hi;
         int highestindex = lo;

         for (int j = lo; j <= hi; j++) {
            this.Highlights.markArray(2, j);
            this.Delays.sleep(0.1);
            if (this.Reads.compareValues(array[j], array[lowestindex]) == -1) {
               lowestindex = j;
               this.Highlights.markArray(1, j);
               this.Delays.sleep(0.1);
            }
         }

         for (int jx = hi; jx >= lo; jx--) {
            this.Highlights.markArray(2, jx);
            this.Delays.sleep(0.1);
            if (this.Reads.compareValues(array[jx], array[highestindex]) == 1) {
               highestindex = jx;
               this.Highlights.markArray(1, jx);
               this.Delays.sleep(0.1);
            }
         }

         int mid = (array[highestindex] - array[lowestindex]) / 2 + array[lowestindex];
         int midindex = (highestindex - lowestindex) / 2 + lowestindex;
         boolean checkForErrorMargin = true;

         for (int jxx = lo; jxx <= hi; jxx++) {
            this.Highlights.markArray(2, jxx);
            this.Delays.sleep(0.1);
            if (this.Reads.compareValues(array[jxx], mid) == 0) {
               midindex = jxx;
               checkForErrorMargin = false;
               this.Highlights.markArray(1, jxx);
               this.Delays.sleep(0.1);
            }
         }

         if (checkForErrorMargin && hi - lo > 4) {
            for (int margin = 1; margin < Math.cbrt(hi - lo); margin++) {
               for (int negativeorpositivelol = -1; negativeorpositivelol <= 1; negativeorpositivelol += 2) {
                  int closeEnough = margin * negativeorpositivelol;

                  for (int jxxx = lo; jxxx <= hi; jxxx++) {
                     this.Highlights.markArray(2, jxxx);
                     this.Delays.sleep(0.1);
                     if (this.Reads.compareValues(array[jxxx], mid + closeEnough) == 0) {
                        midindex = jxxx;
                        this.Highlights.markArray(1, jxxx);
                        this.Delays.sleep(0.1);
                     }
                  }
               }
            }
         }

         this.Writes.swap(array, midindex, hi, 1.0, true, false);
         int pivot = array[hi];

         for (int jxxxx = lo; jxxxx < hi; jxxxx++) {
            this.Highlights.markArray(1, jxxxx);
            if (this.Reads.compareValues(array[jxxxx], pivot) < 0) {
               this.Writes.swap(array, i, jxxxx, 1.0, true, false);
               i++;
            }

            this.Delays.sleep(1.0);
         }

         this.Writes.swap(array, i, hi, 1.0, true, false);
         return i;
      }
   }

   private void quickSort(int[] array, int lo, int hi) {
      if (lo < hi) {
         int p = this.partition(array, lo, hi);
         if (p != -1) {
            this.quickSort(array, lo, p - 1);
            this.quickSort(array, p + 1, hi);
         }
      }
   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
      this.quickSort(array, 0, currentLength - 1);
   }
}
