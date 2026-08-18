package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.AdaptiveSquareInsertionSort;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Rotations;

public final class PlasmaSort extends Sort {
   int[] keys;
   private AdaptiveSquareInsertionSort squareInsert;
   private InsertionSort insertSorter;
   private LazierSort finalMerger;

   public PlasmaSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Plasma");
      this.setRunAllSortsName("Plasma Sort");
      this.setRunSortName("Plasmasort");
      this.setCategory("Hybrid Sorts");
      this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private void rotate(int[] array, int pos, int lenA, int lenB) {
      Rotations.holyGriesMills(array, pos, lenA, lenB, 1.0, true, false);
   }

   private int collectKeys(int[] array, int start, int keyCount, int end) {
      int minbound = start;
      int compindex = start + 1;
      int lastGood = compindex;
      int badCount = 0;

      int count;
      for (count = 1; count < keyCount - start; count++) {
         this.Highlights.markArray(3, compindex);
         int num = array[compindex];
         int l = minbound;
         int h = lastGood;
         int lastBad = badCount;

         while (l < h) {
            int m = l + (h - l) / 2;
            this.Highlights.markArray(2, m);
            this.Delays.sleep(0.01);
            int comp = this.Reads.compareValues(num, array[m]);
            if (comp < 0) {
               h = m;
            } else {
               if (comp == 0) {
                  badCount++;
                  break;
               }
               l = m + 1;
            }
         }

         if (badCount > 0) {
            if (badCount > lastBad) {
               this.Delays.sleep(0.2);
               count--;
               if (++compindex >= end) {
                  break;
               }
               continue;
            }

            this.rotate(array, minbound, lastGood - minbound, badCount);
            minbound += badCount;
            lastGood = compindex;
            l += badCount;
            badCount = 0;
         }

         for (int j = compindex - 1; j >= l; j--) {
            this.Writes.write(array, j + 1, array[j], 1.0, true, false);
         }

         this.Writes.write(array, l, num, 1.0, true, false);
         this.Highlights.clearAllMarks();
         compindex++;
         lastGood++;
         if (compindex >= end) {
            break;
         }
      }

      this.Highlights.clearMark(4);
      if (minbound != start) {
         this.rotate(array, start, minbound - start, count);
      }
      return count;
   }

   private static int getBufferSize(int length) {
      int size = 1;

      while (size * size * size * size * size < length) {
         size *= 2;
      }
      return size;
   }

   private static int getKeySize(int bufferSize, int length) {
      return length / bufferSize;
   }

   private void mergeUnderBuffer(int[] array, int bufferSize, int start, int mid, int end, boolean rightPriority) {
      if (rightPriority) {
         this.rotate(array, start, mid - start, end - mid);
      }

      for (int i = 0; i < mid - start; i++) {
         this.Writes.swap(array, i, start + i, 0.5, true, false);
      }

      int bufferPointer = 0;
      int left = start;
      int right = mid;

      while (left < right && right < end) {
         if (this.Reads.compareIndices(array, bufferPointer, right, 0.1, true) <= 0) {
            this.Writes.swap(array, bufferPointer++, left++, 0.25, true, false);
         } else {
            this.Writes.swap(array, left++, right++, 0.25, true, false);
         }
      }

      while (left < right) {
         this.Writes.swap(array, bufferPointer++, left++, 0.25, true, false);
      }
   }

   public void blockSwap(int[] array, int a, int b, int len) {
      for (int i = 0; i < len; i++) {
         this.Writes.swap(array, a + i, b + i, 1.0, true, false);
      }
   }

   private void mergeOverBuffer(int[] array, int bufferSize, int start, int mid, int end, int keySize) {
      this.resetKeys(keySize);
      int midKey = this.keys[keySize / 2];
      int blockSize = bufferSize;

      int i;
      for (i = start; i < end - blockSize; i += blockSize) {
         int ikey = (i - start) / blockSize;
         int lowestindex = i;
         int lowestkey = ikey;

         for (int j = i + blockSize; j < end; j += blockSize) {
            int jkey = (j - start) / blockSize;
            this.Highlights.markArray(2, j);
            this.Delays.sleep(0.5);
            int comp = this.Reads.compareValues(array[j], array[lowestindex]);
            if (comp == -1 || comp == 0 && this.Reads.compareOriginalIndices(this.keys, jkey, lowestkey, 0.25, true) == -1) {
               lowestindex = j;
               lowestkey = jkey;
               this.Highlights.markArray(1, j);
               this.Delays.sleep(0.5);
            }
         }

         if (lowestindex > i) {
            this.blockSwap(array, i, lowestindex, blockSize);
            this.Writes.swap(this.keys, (i - start) / blockSize, lowestkey, 1.0, true, true);
         }

         if (ikey > 0 && this.Reads.compareIndices(array, i - 1, i, 0.5, true) > 0) {
            int keyIndex = (i - start) / blockSize;
            this.mergeUnderBuffer(
               array, bufferSize, i - blockSize, i, i + blockSize, this.keys[keyIndex - 1] > midKey && this.keys[keyIndex - 1] > this.keys[keyIndex]
            );
         }
      }

      int keyIndex = keySize - 1;
      this.mergeUnderBuffer(
         array, bufferSize, i - blockSize, i, i + blockSize, this.keys[keyIndex - 1] > midKey && this.keys[keyIndex - 1] > this.keys[keyIndex]
      );
   }

   private void resetKeys(int count) {
      for (int i = 0; i < count; i++) {
         this.Writes.write(this.keys, i, i, 0.5, true, true);
      }
   }

   @Override
   public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
      this.squareInsert = new AdaptiveSquareInsertionSort(this.arrayVisualizer);
      this.insertSorter = new InsertionSort(this.arrayVisualizer);
      this.finalMerger = new LazierSort(this.arrayVisualizer);
      if (sortLength < 24) {
         this.insertSorter.customInsertSort(array, 0, sortLength, 0.333, false);
      } else {
         int bufferSize = getBufferSize(sortLength);
         this.collectKeys(array, 0, bufferSize, sortLength);

         for (int i = bufferSize + 1; i < sortLength; i += 2) {
            if (this.Reads.compareIndices(array, i - 1, i, 0.5, true) == 1) {
               this.Writes.swap(array, i - 1, i, 0.5, true, false);
            }
         }

         int gap;
         for (gap = 2; gap < bufferSize * 2; gap *= 2) {
            int var9;
            for (var9 = bufferSize; var9 + 2 * gap <= sortLength; var9 += 2 * gap) {
               this.mergeUnderBuffer(array, bufferSize, var9, var9 + gap, var9 + 2 * gap, false);
            }

            if (var9 + gap < sortLength) {
               this.mergeUnderBuffer(array, bufferSize, var9, var9 + gap, sortLength, false);
            }
         }

         int keyCount = getKeySize(bufferSize, sortLength);

         for (this.keys = this.Writes.createExternalArray(keyCount); gap <= sortLength - bufferSize; gap *= 2) {
            int keySize = 2 * gap / bufferSize;

            int var10;
            for (var10 = bufferSize; var10 + 2 * gap <= sortLength; var10 += 2 * gap) {
               this.mergeOverBuffer(array, bufferSize, var10, var10 + gap, var10 + 2 * gap, keySize);
            }

            if (var10 + gap < sortLength) {
               this.mergeOverBuffer(array, bufferSize, var10, var10 + gap, sortLength, keySize);
            }
         }

         this.Writes.deleteExternalArray(this.keys);
         this.runSort(array, bufferSize, bucketCount);
         this.finalMerger.inPlaceMerge(array, bufferSize / 2, bufferSize, sortLength);
         this.finalMerger.inPlaceMerge(array, 0, bufferSize / 2, sortLength);
      }
   }
}
