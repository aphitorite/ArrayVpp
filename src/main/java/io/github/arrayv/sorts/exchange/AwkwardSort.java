package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class AwkwardSort extends Sort {
   public AwkwardSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Awkward");
      this.setRunAllSortsName("Awkward Sort");
      this.setRunSortName("Awkward Sort");
      this.setCategory("Impractical Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(true);
      this.setUnreasonableLimit(4096);
      this.setBogoSort(false);
        this.setAuthors("aphitorite");
   }

   private void awkward(int[] arr, int l, int pos) {
      if (l != 1) {
         this.awkward(arr, l / 2, pos);
         this.awkward(arr, l / 2 + l % 2, pos + l / 2);

         for (int i = 0; i < l / 2; i++) {
            int a = pos + i;
            int b = pos + l / 2 + l % 2 + i;
            if (this.Reads.compareIndices(arr, a, b, 0.02, true) == 1) {
               this.Writes.swap(arr, a, b, 0.02, true, false);
            }
         }

         this.awkward(arr, l / 2 + l % 2, pos + l / 4);
         this.awkward(arr, l / 2, pos);
         this.awkward(arr, l / 2 + l % 2, pos + l / 2);
      }
   }

   @Override
   public void runSort(int[] array, int sortLength, int bucketCount) {
      this.awkward(array, sortLength, 0);
   }
}
