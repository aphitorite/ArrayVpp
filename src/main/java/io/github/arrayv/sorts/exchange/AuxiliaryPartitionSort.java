package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

public final class AuxiliaryPartitionSort extends Sort {
   public AuxiliaryPartitionSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Auxiliary Partition");
      this.setRunAllSortsName("Auxiliary Partition Sort");
      this.setRunSortName("Auxpartsort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Potassium");
      this.setConstant("n log n");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   public void auxPartSort(int[] array, int start, int[] aux, int length, int bucketCount) {
      int swapTemp = 0;

      for (int j = start; j < start + length - 1; j++) {
         this.Writes.write(aux, j, array[j], 0.1, true, true);
      }

      this.Writes.reversal(aux, start, start + length - 1, 0.1, true, true);

      for (int k = length + start - 1; k > start; k--) {
         if (this.Reads.compareValues(array[k], aux[k]) == 1) {
            swapTemp = aux[k];
            this.Writes.write(aux, k, array[k], 0.1, true, true);
            this.Writes.write(array, k, swapTemp, 0.1, true, true);
         }
      }

      if (length > 2) {
         int[] auxArray1 = this.Writes.createExternalArray(length / 2);
         this.auxPartSort(array, start, auxArray1, length / 2, bucketCount);
         int[] auxArray2 = this.Writes.createExternalArray(length / 2);
         this.auxPartSort(array, start + length / 2, auxArray2, length / 2, bucketCount);
      }
   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      int[] auxArray = this.Writes.createExternalArray(length);
      this.auxPartSort(array, 0, auxArray, length, bucketCount);
   }
}
