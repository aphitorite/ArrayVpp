package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;

public final class WeaveQuickSort extends Sort {
   public WeaveQuickSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Weave Quick");
      this.setRunAllSortsName("Weave Quick Sort");
      this.setRunSortName("Weave Quicksort");
      this.setCategory("Hybrid Sorts");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
        this.setAuthors("aphitorite");
   }

   private int medianOf3(int[] array, int[] indices) {
      if (indices.length == 0) {
         return -1;
      } else if (indices.length < 3) {
         return indices[0];
      } else if (this.Reads.compareIndices(array, indices[0], indices[1], 0.5, true) <= 0) {
         if (this.Reads.compareIndices(array, indices[1], indices[2], 0.5, true) <= 0) {
            return indices[1];
         } else {
            return this.Reads.compareIndices(array, indices[0], indices[2], 0.5, true) < 0 ? indices[2] : indices[0];
         }
      } else if (this.Reads.compareIndices(array, indices[1], indices[2], 0.5, true) >= 0) {
         return indices[1];
      } else {
         return this.Reads.compareIndices(array, indices[0], indices[2], 0.5, true) <= 0 ? indices[0] : indices[2];
      }
   }

   private int mOMHelper(int[] array, int start, int length) {
      if (length == 1) {
         return start;
      } else {
         int[] meds = new int[3];
         int third = length / 3;
         meds[0] = this.mOMHelper(array, start, third);
         meds[1] = this.mOMHelper(array, start + third, third);
         meds[2] = this.mOMHelper(array, start + 2 * third, third);
         return this.medianOf3(array, meds);
      }
   }

   private int medianOfMedians(int[] array, int start, int length) {
      if (length == 1) {
         return start;
      } else {
         int[] meds = new int[3];
         int nearPower = (int)Math.pow(3.0, (double)Math.round(Math.log((double)length) / Math.log(3.0)));
         if (nearPower == length) {
            return this.mOMHelper(array, start, length);
         } else {
            nearPower /= 3;
            if (2 * nearPower >= length) {
               nearPower /= 3;
            }

            meds[0] = this.mOMHelper(array, start, nearPower);
            meds[2] = this.mOMHelper(array, start + length - nearPower, nearPower);
            meds[1] = this.medianOfMedians(array, start + nearPower, length - 2 * nearPower);
            return this.medianOf3(array, meds);
         }
      }
   }

   private void insertTo(int[] array, int a, int b) {
      int temp = array[a];

      while(a > b) {
         this.Writes.write(array, a--, array[a], 0.25, true, false);
      }

      this.Writes.write(array, a, temp, 0.25, true, false);
   }

   private void rotate(int[] array, int a, int m, int b) {
      IndexedRotations.cycleReverse(array, a, m, b, 1.0, true, false);
   }

   private void bitReversal(int[] array, int a, int b) {
      int len = b - a;
      int m = 0;
      int d1 = len >> 1;
      int d2 = d1 + (d1 >> 1);

      for(int i = 1; i < len - 1; ++i) {
         int j = d1;
         int k = i;

         for(int n = d2; (k & 1) == 0; n >>= 1) {
            j -= n;
            k >>= 1;
         }

         m += j;
         if (m > i) {
            this.Writes.swap(array, a + i, a + m, 1.0, true, false);
         }
      }

   }

   private void shuffle(int[] array, int a, int b) {
      int n = b - a;
      int j = a;
      int m = 0;

      for(int k = 2; n / k > 0; k *= 2) {
         if ((n / k & 1) == 1) {
            this.bitReversal(array, j, j + k);
            this.bitReversal(array, j, j + k / 2);
            this.bitReversal(array, j + k / 2, j + k);
            this.Highlights.clearMark(2);
            this.rotate(array, j - m, j, j + k / 2);
            m += k / 2;
            j += k;
         }
      }

   }

   private int weavePartition(int[] array, int a, int b, int piv, int cmp) {
      int c = 0;

      for(int i = a; i < b; ++i) {
         this.Highlights.markArray(2, i);
         if (this.Reads.compareValues(array[i], piv) < cmp) {
            this.insertTo(array, i, i - c--);
         } else {
            this.insertTo(array, i, i + ++c);
         }
      }

      int b1 = b - Math.abs(c);
      this.shuffle(array, a, b1);
      int p = (a + b1) / 2;
      if (c < 0) {
         this.rotate(array, p, b1, b);
         p -= c;
      }

      return p;
   }

   private void weaveQuick(int[] array, int a, int b) {
      while(b - a > 32) {
         int n = b - a;
         n -= ~n & 1;
         int p = this.medianOfMedians(array, a, n);
         int m = this.weavePartition(array, a, b, array[p], 0);
         int left = m - a;
         int right = b - m;
         if (m == a) {
            m = this.weavePartition(array, a, b, array[p], 1);
            a = m;
         } else if (right < left) {
            this.weaveQuick(array, m, b);
            b = m;
         } else {
            this.weaveQuick(array, a, m);
            a = m;
         }
      }

      BinaryInsertionSort smallSort = new BinaryInsertionSort(this.arrayVisualizer);
      smallSort.customBinaryInsert(array, a, b, 0.25);
   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
      this.weaveQuick(array, 0, currentLength);
   }
}
