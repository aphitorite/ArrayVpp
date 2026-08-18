package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BlockInsertionSort;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class BubbledBufferedMedianOfSevenQuickSort extends BogoSorting {
   InsertionSort sort = new InsertionSort(this.arrayVisualizer);
   BlockInsertionSort sort2 = new BlockInsertionSort(this.arrayVisualizer);
   ImprovedBlockSelectionSort sort3 = new ImprovedBlockSelectionSort(this.arrayVisualizer);

   public BubbledBufferedMedianOfSevenQuickSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Bubbled Buffered Median-of-seven Left/Right Quick");
      this.setRunAllSortsName("Bubbled Buffered Median-of-seven Quick Sort, Left/Right Pointers");
      this.setRunSortName("Bubbled Buffered Median-of-seven Left/Right Quicksort");
      this.setCategory("Exchange Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   public int avg(int a, int b) {
      return Math.abs(b - a) / 2 + a;
   }

   public int avg31(int a, int b) {
      return Math.abs(b - a) / 3 + a;
   }

   public int avg32(int a, int b) {
      return Math.abs(b - a) / 3 * 2 + a;
   }

   private void quickSort(int[] a, int p, int r) {
      if (r - p > 6) {
         int pivot = this.avg(p, r);
         int pLeftL = this.avg31(p, pivot);
         int pLeftR = this.avg32(p, pivot);
         int pRightL = this.avg31(pivot, r);
         int pRightR = this.avg32(pivot, r);
         this.Writes.swap(a, 0, p, 0.25, true, false);
         this.Writes.swap(a, 1, pLeftL, 0.25, true, false);
         this.Writes.swap(a, 2, pLeftR, 0.25, true, false);
         this.Writes.swap(a, 3, pivot, 0.25, true, false);
         this.Writes.swap(a, 4, pRightL, 0.25, true, false);
         this.Writes.swap(a, 5, pRightR, 0.25, true, false);
         this.Writes.swap(a, 6, r, 0.25, true, false);
         this.sort.customInsertSort(a, 0, 7, 1.0, false);
         this.Writes.swap(a, p, 0, 0.25, true, false);
         this.Writes.swap(a, pLeftL, 1, 0.25, true, false);
         this.Writes.swap(a, pLeftR, 2, 0.25, true, false);
         this.Writes.swap(a, pivot, 3, 0.25, true, false);
         this.Writes.swap(a, pRightL, 4, 0.25, true, false);
         this.Writes.swap(a, pRightR, 5, 0.25, true, false);
         this.Writes.swap(a, r, 6, 0.25, true, false);
         int x = a[pivot];
         int i = p;
         int j = r;
         this.Highlights.markArray(3, pivot);

         while (i <= j) {
            while (this.Reads.compareValues(a[i], x) == -1) {
               this.Highlights.markArray(1, ++i);
               this.Delays.sleep(0.5);
            }

            while (this.Reads.compareValues(a[j], x) == 1) {
               this.Highlights.markArray(2, --j);
               this.Delays.sleep(0.5);
            }

            if (i <= j) {
               if (i == pivot) {
                  this.Highlights.markArray(3, j);
               }

               if (j == pivot) {
                  this.Highlights.markArray(3, i);
               }

               this.Writes.swap(a, i, j, 1.0, true, false);
               i++;
               j--;
            }
         }

         if (j - p + (r - i) > 6) {
            if (j - p >= 7) {
               this.quickSort(a, p, j);
            } else {
               this.sort.customInsertSort(a, p, j + 1, 1.0, false);
            }

            if (r - i >= 7) {
               this.quickSort(a, i, r);
            } else {
               this.sort.customInsertSort(a, i, r + 1, 1.0, false);
            }
         } else {
            this.sort.customInsertSort(a, p, r + 1, 1.0, false);
         }
      } else {
         this.sort.customInsertSort(a, p, r + 1, 1.0, false);
      }
   }

   public void pull(int[] array, int a, int b, double sleep, boolean mark, boolean aux) {
      for (int j = a; j < b; j++) {
         this.Writes.swap(array, j, j + 1, sleep, mark, aux);
      }
   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
      this.sort.customInsertSort(array, 0, 7, 1.0, false);
      this.quickSort(array, 7, currentLength - 1);

      for (int i = 7; i >= 0; i--) {
         for (int k = i; this.Reads.compareValues(array[k], array[k + 1]) > 0 && k < currentLength - 1; k++) {
            this.Writes.swap(array, k, k + 1, 0.25, true, false);
         }
      }
   }
}
