package io.github.arrayv.sorts.root;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class SingleSort extends Sort {
   private InsertionSort insertSorter;

   public SingleSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Single");
      this.setRunAllSortsName("Singlesort");
      this.setRunSortName("Singlesort");
      this.setCategory("Block Merge Sorts");
	  this.setAuthors("Potassium");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   private void sqrtSwap(int[] arr, int a, int b, boolean auxwrite) {
      this.Writes.swap(arr, a, b, 1.0, true, auxwrite);
      this.Highlights.clearMark(2);
   }

   private void sqrtMultiSwap(int[] arr, int a, int b, int swapsLeft, boolean auxwrite) {
      while (swapsLeft != 0) {
         this.sqrtSwap(arr, a++, b++, auxwrite);
         swapsLeft--;
      }
   }

   private void sqrtInsertSort(int[] arr, int pos, int len, boolean auxwrite) {
      this.insertSorter.customInsertSort(arr, pos, len, 0.25, auxwrite);
   }

   private void sqrtMergeRight(int[] arr, int pos, int leftLen, int rightLen, int dist, boolean auxwrite) {
      int mergedPos = leftLen + rightLen + dist - 1;
      int right = leftLen + rightLen - 1;
      int left = leftLen - 1;

      while (left >= 0) {
         this.Highlights.markArray(2, pos + left);
         this.Highlights.markArray(3, pos + right);
         if (right >= leftLen && this.Reads.compareValues(arr[pos + left], arr[pos + right]) <= 0) {
            this.Writes.write(arr, pos + mergedPos--, arr[pos + right--], 1.0, true, auxwrite);
         } else {
            this.Writes.write(arr, pos + mergedPos--, arr[pos + left--], 1.0, true, auxwrite);
         }
      }

      this.Highlights.clearMark(3);
      if (right != mergedPos) {
         while (right >= leftLen) {
            this.Writes.write(arr, pos + mergedPos--, arr[pos + right--], 1.0, true, auxwrite);
            this.Highlights.markArray(2, pos + right);
         }
      }

      this.Highlights.clearMark(2);
   }

   private void sqrtMergeLeftWithXBuf(int[] arr, int pos, int leftEnd, int rightEnd, int dist, boolean auxwrite) {
      int left = 0;
      int right = leftEnd;
      rightEnd += leftEnd;

      while (right < rightEnd) {
         if (left != leftEnd && this.Reads.compareValues(arr[pos + left], arr[pos + right]) <= 0) {
            this.Writes.write(arr, pos + dist++, arr[pos + left++], 1.0, true, auxwrite);
         } else {
            this.Writes.write(arr, pos + dist++, arr[pos + right++], 1.0, true, auxwrite);
         }

         this.Highlights.markArray(2, pos + left);
         this.Highlights.markArray(3, pos + right);
      }

      this.Highlights.clearMark(3);
      if (dist != left) {
         while (left < leftEnd) {
            this.Writes.write(arr, pos + dist++, arr[pos + left++], 1.0, true, auxwrite);
            this.Highlights.markArray(2, pos + left);
         }
      }

      this.Highlights.clearMark(2);
   }

   private void sqrtMergeDown(int[] arr, int arrPos, int[] buffer, int bufPos, int leftLen, int rightLen, boolean auxwrite) {
      int arrMerge = 0;
      int bufMerge = 0;
      int dist = 0 - rightLen;

      while (bufMerge < rightLen) {
         if (arrMerge != leftLen && this.Reads.compareValues(arr[arrPos + arrMerge], buffer[bufPos + bufMerge]) < 0) {
            this.Writes.write(arr, arrPos + dist++, arr[arrPos + arrMerge++], 1.0, true, auxwrite);
         } else {
            this.Writes.write(arr, arrPos + dist++, buffer[bufPos + bufMerge++], 1.0, true, auxwrite);
         }

         this.Highlights.markArray(2, arrPos + arrMerge);
         this.Highlights.markArray(3, bufPos + bufMerge);
      }

      this.Highlights.clearMark(3);
      if (dist != arrMerge) {
         while (arrMerge < leftLen) {
            this.Writes.write(arr, arrPos + dist++, arr[arrPos + arrMerge++], 1.0, true, auxwrite);
            this.Highlights.markArray(2, arrPos + arrMerge);
         }
      }

      this.Highlights.clearMark(2);
   }

   private SqrtState sqrtSmartMergeWithXBuf(int[] arr, int pos, int leftOverLen, int leftOverFrag, int blockLen, boolean auxwrite) {
      int dist = 0 - blockLen;
      int left = 0;
      int right = leftOverLen;
      int leftEnd = leftOverLen;
      int rightEnd = leftOverLen + blockLen;
      int typeFrag = 1 - leftOverFrag;

      while (left < leftEnd && right < rightEnd) {
         if (this.Reads.compareValues(arr[pos + left], arr[pos + right]) - typeFrag < 0) {
            this.Writes.write(arr, pos + dist++, arr[pos + left++], 1.0, true, auxwrite);
         } else {
            this.Writes.write(arr, pos + dist++, arr[pos + right++], 1.0, true, auxwrite);
         }

         this.Highlights.markArray(2, pos + left);
         this.Highlights.markArray(3, pos + right);
      }

      this.Highlights.clearMark(3);
      int fragment = leftOverFrag;
      int length;
      if (left < leftEnd) {
         length = leftEnd - left;

         while (left < leftEnd) {
            this.Writes.write(arr, pos + --rightEnd, arr[pos + --leftEnd], 1.0, true, auxwrite);
            this.Highlights.markArray(2, pos + leftEnd);
         }
      } else {
         length = rightEnd - right;
         fragment = typeFrag;
      }

      this.Highlights.clearMark(2);
      return new SqrtState(length, fragment);
   }

   private void sqrtMergeBuffersLeftWithXBuf(
      int[] keys, int midkey, int[] arr, int pos, int blockCount, int regBlockLen, int aBlockCount, int lastLen, boolean auxwrite
   ) {
      if (blockCount == 0) {
         int aBlocksLen = aBlockCount * regBlockLen;
         this.sqrtMergeLeftWithXBuf(arr, pos, aBlocksLen, lastLen, 0 - regBlockLen, auxwrite);
      } else {
         int leftOverLen = regBlockLen;
         int leftOverFrag = this.Reads.compareOriginalValues(keys[0], midkey) < 0 ? 0 : 1;
         int processIndex = regBlockLen;

         for (int keyIndex = 1; keyIndex < blockCount; processIndex += regBlockLen) {
            int restToProcess = processIndex - leftOverLen;
            int nextFrag = this.Reads.compareOriginalValues(keys[keyIndex], midkey) < 0 ? 0 : 1;
            if (nextFrag == leftOverFrag) {
               this.Writes.arraycopy(arr, pos + restToProcess, arr, pos + restToProcess - regBlockLen, leftOverLen, 1.0, true, auxwrite);
               leftOverLen = regBlockLen;
            } else {
               SqrtState results = this.sqrtSmartMergeWithXBuf(arr, pos + restToProcess, leftOverLen, leftOverFrag, regBlockLen, auxwrite);
               leftOverLen = results.getLeftOverLen();
               leftOverFrag = results.getLeftOverFrag();
            }

            keyIndex++;
         }

         int restToProcess = processIndex - leftOverLen;
         if (lastLen != 0) {
            if (leftOverFrag != 0) {
               this.Writes.arraycopy(arr, pos + restToProcess, arr, pos + restToProcess - regBlockLen, leftOverLen, 1.0, true, auxwrite);
               restToProcess = processIndex;
               leftOverLen = regBlockLen * aBlockCount;
            } else {
               leftOverLen += regBlockLen * aBlockCount;
            }

            this.sqrtMergeLeftWithXBuf(arr, pos + restToProcess, leftOverLen, lastLen, 0 - regBlockLen, auxwrite);
         } else {
            this.Writes.arraycopy(arr, pos + restToProcess, arr, pos + restToProcess - regBlockLen, leftOverLen, 1.0, true, auxwrite);
         }
      }
   }

   private void sqrtBuildBlocks(int[] arr, int pos, int len, int buildLen, boolean auxwrite) {
      for (int dist = 1; dist < len; dist += 2) {
         int extraDist = 0;
         if (this.Reads.compareValues(arr[pos + (dist - 1)], arr[pos + dist]) > 0) {
            extraDist = 1;
         }

         this.Writes.write(arr, pos + dist - 3, arr[pos + dist - 1 + extraDist], 1.0, true, auxwrite);
         this.Writes.write(arr, pos + dist - 2, arr[pos + dist - extraDist], 1.0, true, auxwrite);
      }

      if (len % 2 != 0) {
         this.Writes.write(arr, pos + len - 3, arr[pos + len - 1], 1.0, true, auxwrite);
      }

      pos -= 2;

      for (int part = 2; part < buildLen; part *= 2) {
         int left = 0;

         for (int right = len - 2 * part; left <= right; left += 2 * part) {
            this.sqrtMergeLeftWithXBuf(arr, pos + left, part, part, 0 - part, auxwrite);
         }

         int rest = len - left;
         if (rest > part) {
            this.sqrtMergeLeftWithXBuf(arr, pos + left, part, rest - part, 0 - part, auxwrite);
         } else {
            while (left < len) {
               this.Writes.write(arr, pos + left - part, arr[pos + left++], 1.0, true, auxwrite);
            }
         }

         pos -= part;
      }

      int restToBuild = len % (2 * buildLen);
      int leftOverPos = len - restToBuild;
      if (restToBuild <= buildLen) {
         this.Writes.arraycopy(arr, pos + leftOverPos, arr, pos + leftOverPos + buildLen, restToBuild, 1.0, true, auxwrite);
      } else {
         this.sqrtMergeRight(arr, pos + leftOverPos, buildLen, restToBuild - buildLen, buildLen, auxwrite);
      }

      while (leftOverPos > 0) {
         leftOverPos -= 2 * buildLen;
         this.sqrtMergeRight(arr, pos + leftOverPos, buildLen, buildLen, buildLen, auxwrite);
      }
   }

   private void sqrtCombineBlocks(int[] arr, int pos, int len, int buildLen, int regBlockLen, int[] tags, boolean auxwrite) {
      int combineLen = len / (2 * buildLen);
      int leftOver = len % (2 * buildLen);
      if (leftOver <= buildLen) {
         len -= leftOver;
         leftOver = 0;
      }

      int leftIndex = 0;

      for (int i = 0; i <= combineLen && (i != combineLen || leftOver != 0); i++) {
         int blockPos = pos + i * 2 * buildLen;
         int blockCount = (i == combineLen ? leftOver : 2 * buildLen) / regBlockLen;
         int tagIndex = blockCount + (i == combineLen ? 1 : 0);

         for (int j = 0; j <= tagIndex; j++) {
            this.Writes.write(tags, j, j, 1.0, true, true);
         }

         int midkey = buildLen / regBlockLen;

         for (int var20 = 1; var20 < blockCount; var20++) {
            leftIndex = var20 - 1;

            for (int rightIndex = var20; rightIndex < blockCount; rightIndex++) {
               int rightComp = this.Reads.compareValues(arr[blockPos + leftIndex * regBlockLen], arr[blockPos + rightIndex * regBlockLen]);
               if (rightComp > 0 || rightComp == 0 && tags[leftIndex] > tags[rightIndex]) {
                  leftIndex = rightIndex;
               }
            }

            if (leftIndex != var20 - 1) {
               this.sqrtMultiSwap(arr, blockPos + (var20 - 1) * regBlockLen, blockPos + leftIndex * regBlockLen, regBlockLen, auxwrite);
               this.sqrtSwap(tags, var20 - 1, leftIndex, true);
            }
         }

         int aBlockCount = 0;
         int lastLen = 0;
         if (i == combineLen) {
            lastLen = leftOver % regBlockLen;
         }

         if (lastLen != 0) {
            while (
               aBlockCount < blockCount
                  && this.Reads.compareValues(arr[blockPos + blockCount * regBlockLen], arr[blockPos + (blockCount - aBlockCount - 1) * regBlockLen]) < 0
            ) {
               aBlockCount++;
            }
         }

         this.sqrtMergeBuffersLeftWithXBuf(tags, midkey, arr, blockPos, blockCount - aBlockCount, regBlockLen, aBlockCount, lastLen, auxwrite);
      }

      for (int var19 = len - 1; var19 >= 0; var19--) {
         this.Writes.write(arr, pos + var19, arr[pos + var19 - regBlockLen], 1.0, true, auxwrite);
      }
   }

   public void sqrtCommonSort(int[] arr, int pos, int len, int[] extBuf, int extBufPos, int[] tags, boolean auxwrite) {
      this.insertSorter = new InsertionSort(this.arrayVisualizer);
      if (len <= 16) {
         this.sqrtInsertSort(arr, pos, len, auxwrite);
         this.Highlights.clearAllMarks();
      } else {
         int blockLen = 1;

         while (blockLen * blockLen * blockLen * blockLen * blockLen * blockLen * blockLen * blockLen * blockLen * blockLen < len) {
            blockLen *= 2;
         }

         this.Writes.arraycopy(arr, pos, extBuf, extBufPos, blockLen, 1.0, true, auxwrite);
         this.sqrtCommonSort(extBuf, extBufPos, blockLen, arr, pos, tags, !auxwrite);
         this.sqrtBuildBlocks(arr, pos + blockLen, len - blockLen, blockLen, auxwrite);
         int buildLen = blockLen;

         while (len > (buildLen *= 2)) {
            this.sqrtCombineBlocks(arr, pos + blockLen, len - blockLen, buildLen, blockLen, tags, auxwrite);
         }

         this.sqrtMergeDown(arr, pos + blockLen, extBuf, extBufPos, len - blockLen, blockLen, auxwrite);
         this.Highlights.clearAllMarks();
      }
   }

   @Override
   public void runSort(int[] array, int len, int bucketCount) {
      int bufferLen = 1;

      while (bufferLen * bufferLen * bufferLen * bufferLen * bufferLen * bufferLen * bufferLen * bufferLen * bufferLen < len) {
         bufferLen *= 2;
      }

      int numKeys = (len - 1) / bufferLen + 2;
      int[] extBuf = this.Writes.createExternalArray(bufferLen);
      int[] tags = this.Writes.createExternalArray(numKeys);
      this.sqrtCommonSort(array, 0, len, extBuf, 0, tags, false);
      this.Writes.deleteExternalArray(extBuf);
      this.Writes.deleteExternalArray(tags);
   }
}
