package io.github.arrayv.sorts.concurrent;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class MergeExchangeSortRecursive extends Sort {
   private int bnd;

   public MergeExchangeSortRecursive(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Merge-Exchange (Recursive)");
      this.setRunAllSortsName("Recursive Merge-Exchange Sort");
      this.setRunSortName("Recursive Merge-Exchange Sort");
      this.setCategory("Concurrent Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
        this.setAuthors("Ken Batcher");
   }

   private void compSwap(int[] array, int a, int b) {
      if (b < this.bnd && this.Reads.compareIndices(array, a, b, 1.0, true) > 0) {
         this.Writes.swap(array, a, b, 1.0, true, false);
      }

   }

   private void mergeweave(int[] array, int p, int n, int Gm, int Gw) {
      if (n == 2) {
         this.compSwap(array, p, p + Gm);
      }

      if (n >= 3) {
         this.mergeweave(array, p, n / 2, Gm, 2 * Gw);
         this.mergeweave(array, p + Gw * Gm, n / 2, Gm, 2 * Gw);
         int c = 1;

         for(int i = 1; i + 1 < n; c += Gw) {
            this.compSwap(array, p + c * Gm, p + (c + Gw - 1) * Gm);
            ++i;
         }

      }
   }

   private void merge_exch(int[] array, int p, int n, int g) {
      if (n > 2) {
         this.merge_exch(array, p, n / 2, g * 2);
         this.merge_exch(array, p + g, n / 2, g * 2);
      }

      this.mergeweave(array, p, n, g, 2);
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      this.bnd = length;
      this.merge_exch(array, 0, 2 << 31 - Integer.numberOfLeadingZeros(length - 1), 1);
   }
}
