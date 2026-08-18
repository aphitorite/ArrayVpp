package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BlockInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class MixMedianSort extends Sort {
   public MixMedianSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Mix Median");
      this.setRunAllSortsName("Mix Median Sort");
      this.setRunSortName("Mixed Mediansort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private void quickSort(int[] a, int lo, int hi, int level) {
      int mid = (int)Math.floor((lo + hi) / 2);
      int mid2 = (int)Math.floor(Math.sqrt(lo * hi));

      for (int i = 0; i <= 2; i++) {
         if (this.Reads.compareValues(a[mid], a[hi]) == 1) {
            this.Writes.swap(a, mid, hi, 5.0, true, false);
         }

         if (this.Reads.compareValues(a[mid2], a[mid]) == 1) {
            this.Writes.swap(a, mid2, mid, 5.0, true, false);
         }

         if (this.Reads.compareValues(a[lo], a[mid2]) == 1) {
            this.Writes.swap(a, lo, mid2, 5.0, true, false);
         }

         if (this.Reads.compareValues(a[lo], a[mid]) == 1) {
            this.Writes.swap(a, lo, mid, 5.0, true, false);
         }

         if (this.Reads.compareValues(a[mid2], a[hi]) == 1) {
            this.Writes.swap(a, mid2, hi, 5.0, true, false);
         }

         if (this.Reads.compareValues(a[lo], a[hi]) == 1) {
            this.Writes.swap(a, lo, hi, 5.0, true, false);
         }
      }

      if (hi - lo > level) {
         this.quickSort(a, lo, mid2, level);
         this.quickSort(a, mid2 + 1, mid, level);
         this.quickSort(a, mid + 1, hi, level);
      } else {
         this.Highlights.markArray(1, lo);
         this.Highlights.markArray(2, mid2);
         this.Highlights.markArray(3, mid);
         this.Highlights.markArray(4, hi);
         BlockInsertionSort sort = new BlockInsertionSort(this.arrayVisualizer);
         sort.insertionSort(a, lo, hi);
      }
   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
      for (int k = 1; k < currentLength; k *= 2) {
         this.quickSort(array, 0, currentLength - 1, k);
      }

      BlockInsertionSort sort2 = new BlockInsertionSort(this.arrayVisualizer);
      sort2.insertionSort(array, 0, currentLength);
      if (this.Reads.compareValues(array[currentLength - 2], array[currentLength - 1]) == 1) {
         this.Writes.swap(array, currentLength - 2, currentLength - 1, 0.1, true, false);
      }
   }
}
