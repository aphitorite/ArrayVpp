package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class HybridBubblePlusBogoQuestionableLikePartitionMergeCircleSort extends BogoSorting {
   public HybridBubblePlusBogoQuestionableLikePartitionMergeCircleSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Hybrid Bubble + Bogo Questionable-Like Partition-Merge Circle");
      this.setRunAllSortsName("Hybrid Bubble + Bogo Questionable-Like Partition-Merge Circle Sort");
      this.setRunSortName("Hybrid Bubble + Bogo Questionable-Like Partition-Merge Circlesort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   public void bubble(int[] array, int length) {
      for (int i = length - 1; i > 0; i--) {
         boolean sorted = true;

         for (int j = 0; j < i; j++) {
            if (this.Reads.compareValues(array[j], array[j + 1]) == 1) {
               this.Writes.swap(array, j, j + 1, 7.5, true, false);
               sorted = false;
            }

            this.Highlights.markArray(1, j);
            this.Highlights.markArray(2, j + 1);
            this.Delays.sleep(2.5);
         }

         if (sorted) {
            break;
         }
      }
   }

   public void singleRoutine(int[] array, int start, int end) {
      int mid = (end - start) / 2 + start;
      if (end - start > 7) {
         this.singleRoutine(array, start, mid);
         this.singleRoutine(array, mid, end);
      }

      while (!this.isRangePartitioned(array, start, mid, end)) {
         int j = start;

         for (int i = end; i > j; j++) {
            if (this.Reads.compareIndices(array, j, i, 0.5, true) > 0) {
               this.Writes.swap(array, i, j, 0.5, true, false);
            }

            i--;
         }

         this.bogoSwap(array, start, mid, false);
         this.bogoSwap(array, mid, end, false);
      }

      if (end - start > 7) {
         this.singleRoutine(array, mid, end);
         this.singleRoutine(array, start, mid);
      }
   }

   @Override
   public void runSort(int[] array, int sortLength, int bucketCount) {
      this.singleRoutine(array, 0, sortLength - 1);
      this.bubble(array, sortLength);
   }
}
