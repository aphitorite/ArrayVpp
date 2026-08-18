package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class BufferedBlockSelectionMergeSort extends Sort {
   public BufferedBlockSelectionMergeSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Buffered Block Selection Merge");
      this.setRunAllSortsName("Buffered Block Selection Merge Sort");
      this.setRunSortName("Buffered Block Selection Merge Sort");
      this.setCategory("Hybrid Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private int sqrt(int n) {
      return (int)Math.sqrt(n - 1) + 1;
   }

   private void multiSwap(int[] array, int a, int b, int len) {
      for (int i = 0; i < len; i++) {
         this.Writes.swap(array, a + i, b + i, 1.0, true, false);
      }
   }

   private boolean blockLesser(int[] array, int a, int b, int bLen) {
      int cmp = this.Reads.compareValues(array[a], array[b]);
      return cmp < 0 || cmp == 0 && this.Reads.compareValues(array[a + bLen - 1], array[b + bLen - 1]) < 0;
   }

   private void mergeAboveBW(int[] array, int a1, int b1, int a, int b) {
      int p = b1-- + b-- - a;

      while (b >= a && b1 >= a1) {
         if (this.Reads.compareValues(array[b], array[b1]) >= 0) {
            this.Writes.swap(array, --p, b--, 1.0, true, false);
         } else {
            this.Writes.swap(array, --p, b1--, 1.0, true, false);
         }
      }

      while (b >= a) {
         this.Writes.swap(array, --p, b--, 1.0, true, false);
      }
   }

   private void mergeBW(int[] array, int a, int m, int b, int p) {
      int pLen = b - m;
      this.multiSwap(array, m, p, pLen);
      int i = pLen - 1;
      int j = m - 1;
      int k = b - 1;

      while (i >= 0 && j >= a) {
         if (this.Reads.compareValues(array[p + i], array[j]) >= 0) {
            this.Writes.swap(array, k--, p + i--, 1.0, true, false);
         } else {
            this.Writes.swap(array, k--, j--, 1.0, true, false);
         }
      }

      while (i >= 0) {
         this.Writes.swap(array, k--, p + i--, 1.0, true, false);
      }
   }

   private void inPlaceMergeUnstable(int[] array, int a, int m, int b) {
      int bLen = this.sqrt(b - a);
      int a1 = a + (m - a - 1) % bLen + 1;
      int b1 = b - (b - m) % bLen;
      this.multiSwap(array, a1, this.blockLesser(array, m - bLen, b1 - bLen, bLen) ? b1 - bLen : m - bLen, bLen);

      for (a1 += bLen; a1 < b1; a1 += bLen) {
         int min = a1;

         for (int i = a1 + bLen; i < b1; i += bLen) {
            if (this.blockLesser(array, i, min, bLen)) {
               min = i;
            }
         }

         if (min > a1) {
            this.multiSwap(array, min, a1, bLen);
         }

         this.mergeAboveBW(array, a, a1 - bLen, a1, a1 + bLen);
      }

      this.mergeAboveBW(array, a, a1 - bLen, a1, b);
      BinaryInsertionSort smallSort = new BinaryInsertionSort(this.arrayVisualizer);
      smallSort.customBinaryInsert(array, b - bLen, b, 0.25);
      this.mergeBW(array, a + bLen, b - bLen, b, a);
      smallSort.customBinaryInsert(array, a, a + bLen, 0.25);
   }

   private void mergeSort(int[] array, int a, int b) {
      if (b - a < 32) {
         BinaryInsertionSort smallSort = new BinaryInsertionSort(this.arrayVisualizer);
         smallSort.customBinaryInsert(array, a, b, 0.25);
      } else {
         int m = (a + b) / 2;
         this.mergeSort(array, a, m);
         this.mergeSort(array, m, b);
         this.inPlaceMergeUnstable(array, a, m, b);
      }
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      this.mergeSort(array, 0, length);
   }
}
