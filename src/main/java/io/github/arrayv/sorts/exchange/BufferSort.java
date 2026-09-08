package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class BufferSort extends Sort {
   public BufferSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Buffer");
      this.setRunAllSortsName("Buffer Sort");
      this.setRunSortName("Buffersort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setConstant("n^2");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   public void bufferSort(int[] array, int length, int bucketCount) {
      int i = 0;

      for (int k = length; k > 0; k--) {
         for (int j = 0; j < i + 1; j++) {
            if (this.Reads.compareValues(array[j], array[i]) == 1) {
               this.Writes.swap(array, j, i, 0.075, true, false);
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, i);
            this.Delays.sleep(0.025);
         }

         i++;
      }
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      this.bufferSort(array, length, bucketCount);
   }
}
