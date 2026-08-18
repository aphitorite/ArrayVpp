package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BlockInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class ClusterSelectionSort extends Sort {
   BlockInsertionSort sort = new BlockInsertionSort(this.arrayVisualizer);

   public ClusterSelectionSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Cluster Selection");
      this.setRunAllSortsName("Cluster Selection Sort");
      this.setRunSortName("Cluster Selection Sort");
      this.setCategory("Hybrid Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   public void selectionSort(int[] array, int length, int[] cluster) {
      int i = 0;

      while (i < length - 1) {
         boolean group = false;
         int c = 0;
         int lowestindex = i;

         for (int j = i + 1; j < length; j++) {
            this.Highlights.markArray(2, j);
            this.Delays.sleep(0.125);
            if (this.Reads.compareValues(array[j], array[lowestindex]) == -1) {
               lowestindex = j;
               this.Highlights.markArray(1, j);
               this.Delays.sleep(0.125);
            }

            if (this.Reads.compareValues(array[j], array[lowestindex]) == 0) {
               this.Writes.write(cluster, c, j, 0.125, true, true);
               c++;
               this.Highlights.markArray(1, lowestindex);
               this.Delays.sleep(0.01);
               group = true;
            }
         }

         this.Writes.swap(array, i, lowestindex, 0.25, true, false);
         if (group) {
            int l = 0;

            int w;
            for (w = i + 1; cluster[l] >= 0; w++) {
               this.Writes.swap(array, cluster[l], w, 0.125, true, false);
               l++;
            }

            for (int k = 0; k < length; k++) {
               this.Writes.write(cluster, k, -1, 0.125, false, false);
            }

            i += w - i;
         } else {
            i++;
         }
      }
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      int[] cluster = this.Writes.createExternalArray(length);

      for (int k = 0; k < length; k++) {
         this.Writes.write(cluster, k, -1, 0.125, false, false);
      }

      this.selectionSort(array, length, cluster);
      this.Writes.deleteExternalArray(cluster);
      this.sort.insertionSort(array, 0, length);
   }
}
