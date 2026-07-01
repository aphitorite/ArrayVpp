package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class LogMergeSort extends Sort {
   public LogMergeSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Log Merge");
      this.setRunAllSortsName("Log Merge Sort");
      this.setRunSortName("Log Mergesort");
      this.setCategory("Hybrid Sorts");
      this.setConstant("n log n");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
        this.setAuthors("aphitorite");
      this.setQuestion("Set block size (default: calculates minimum block length for current length)", 1);
   }

   private int log2(int n) {
      return 31 - Integer.numberOfLeadingZeros(n);
   }

   private int minLog(int n) {
      int a = 1;
      int b = 32;

      while(a < b) {
         int m = (a + b) / 2;
         if (this.log2(n / m - 1) + 1 > m) {
            a = m + 1;
         } else {
            b = m;
         }
      }

      return a;
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

   private int medianOf9(int[] array, int start, int end) {
      int length = end - start;
      int half = length / 2;
      int quarter = half / 2;
      int eighth = quarter / 2;
      int[] elements0 = new int[]{start, start + eighth, start + quarter};
      int med0 = this.medianOf3(array, elements0);
      int[] elements1 = new int[]{start + quarter + eighth, start + half, start + half + eighth};
      int med1 = this.medianOf3(array, elements1);
      int[] elements2 = new int[]{start + half + quarter, start + half + quarter + eighth, end - 1};
      int med2 = this.medianOf3(array, elements2);
      return this.medianOf3(array, new int[]{med0, med1, med2});
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

   private void blockSwap(int[] array, int a, int b, int s) {
      while(s-- > 0) {
         this.Writes.swap(array, a++, b++, 1.0, true, false);
      }

   }

   private boolean pivCmp(int v, int piv, int pCmp) {
      return this.Reads.compareValues(v, piv) < pCmp;
   }

   private void pivBufSet(int[] array, int pa, int pb, int v, int wLen) {
      for(; wLen-- > 0; v >>= 1) {
         if ((v & 1) == 1) {
            this.Writes.swap(array, pa + wLen, pb + wLen, 1.0, true, false);
         }
      }

   }

   private int pivBufGet(int[] array, int pa, int piv, int pCmp, int wLen, int bit) {
      int r;
      for(r = 0; wLen-- > 0; r |= this.pivCmp(array[pa++], piv, pCmp) ? bit : bit ^ 1) {
         r <<= 1;
      }

      return r;
   }

   private int partitionEasy(int[] array, int[] aux, int a, int b, int piv, int pCmp) {
      int j = 0;

      for(int i = a; i < b; ++i) {
         this.Highlights.markArray(1, i);
         this.Delays.sleep(0.25);
         if (this.pivCmp(array[i], piv, pCmp)) {
            this.Writes.write(array, a++, array[i], 0.25, true, false);
         } else {
            this.Writes.write(aux, j++, array[i], 0.25, false, true);
         }
      }

      this.Writes.arraycopy(aux, 0, array, a, j, 0.5, true, false);
      return a;
   }

   private void blockCycle(int[] array, int p, int n, int p1, int bLen, int wLen, int piv, int pCmp, int bit) {
      for(int i = 0; i < n; ++i) {
         for(int dest = this.pivBufGet(array, p + i * bLen, piv, pCmp, wLen, bit); dest != i; dest = this.pivBufGet(array, p + i * bLen, piv, pCmp, wLen, bit)) {
            this.blockSwap(array, p + i * bLen, p + dest * bLen, bLen);
         }

         this.pivBufSet(array, p + i * bLen, p1 + i * bLen, i, wLen);
      }

   }

   private int blockPartition(int[] array, int[] swap, int a, int b, int bLen, int piv, int pCmp) {
      while(a < b && this.pivCmp(array[a], piv, pCmp)) {
         ++a;
      }

      while(b > a && !this.pivCmp(array[b - 1], piv, pCmp)) {
         --b;
      }

      if (b - a <= 2 * bLen) {
         return this.partitionEasy(array, swap, a, b, piv, pCmp);
      } else {
         this.Highlights.clearMark(2);
         int p = a;
         int l = 0;
         int r = 0;
         int lb = 0;
         int rb = 0;

         for(int i = a; i < b; ++i) {
            this.Highlights.markArray(1, i);
            this.Delays.sleep(0.25);
            if (this.pivCmp(array[i], piv, pCmp)) {
               this.Writes.write(swap, l++, array[i], 0.25, false, true);
               if (l == bLen) {
                  this.Writes.arraycopy(swap, 0, array, p, bLen, 0.5, true, false);
                  l = 0;
                  ++lb;
                  p += bLen;
               }
            } else {
               this.Writes.write(swap, bLen + r++, array[i], 0.25, false, true);
               if (r == bLen) {
                  this.Writes.arraycopy(swap, bLen, array, p, bLen, 0.5, true, false);
                  r = 0;
                  ++rb;
                  p += bLen;
               }
            }
         }

         this.Highlights.clearMark(3);
         int min = Math.min(lb, rb);
         int m = a + lb * bLen;
         if (min > 0) {
            int bCnt = lb + rb;
            int wLen = this.log2(min - 1) + 1;
            int i = 0;
            int j = 0;

            for(int k = 0; i < min; ++i) {
               while(!this.pivCmp(array[a + j * bLen + wLen], piv, pCmp)) {
                  ++j;
               }

               while(this.pivCmp(array[a + k * bLen + wLen], piv, pCmp)) {
                  ++k;
               }

               this.pivBufSet(array, a + j++ * bLen, a + k++ * bLen, i, wLen);
            }

            if (lb < rb) {
               i = bCnt - 1;

               for(int jx = 0; jx < rb; --i) {
                  if (!this.pivCmp(array[a + i * bLen + wLen], piv, pCmp)) {
                     int var26 = a + i * bLen;
                     ++jx;
                     this.blockSwap(array, var26, a + (bCnt - jx) * bLen, bLen);
                  }
               }

               this.blockCycle(array, a, lb, m, bLen, wLen, piv, pCmp, 0);
            } else {
               i = 0;

               for(int jx = 0; jx < lb; ++i) {
                  if (this.pivCmp(array[a + i * bLen + wLen], piv, pCmp)) {
                     this.blockSwap(array, a + i * bLen, a + jx++ * bLen, bLen);
                  }
               }

               this.blockCycle(array, m, rb, a, bLen, wLen, piv, pCmp, 1);
            }
         }

         this.Writes.arraycopy(swap, bLen, array, b - r, r, 1.0, true, false);
         if (l > 0) {
            this.Highlights.clearMark(2);
            this.Writes.reversearraycopy(array, a + lb * bLen, array, a + lb * bLen + l, rb * bLen, 1.0, true, false);
            this.Writes.arraycopy(swap, 0, array, a + lb * bLen, l, 1.0, true, false);
         }

         return a + lb * bLen + l;
      }
   }

   private void selectMedian(int[] array, int[] aux, int a, int b, int bLen) {
      int med = (a + b) / 2;
      boolean badPartition = false;

      while(b - a > 32) {
         int p;
         if (badPartition) {
            int n = b - a;
            n -= ~n & 1;
            p = this.medianOfMedians(array, a, n);
            badPartition = false;
         } else {
            p = this.medianOf9(array, a, b);
         }

         this.Highlights.markArray(3, p);
         int m = this.blockPartition(array, aux, a, b, bLen, array[p], 0);
         if (m == a) {
            this.Highlights.markArray(3, p);
            m = this.blockPartition(array, aux, a, b, bLen, array[p], 1);
            if (med >= a && med < m) {
               return;
            }
         }

         int left = m - a;
         int right = b - m;
         badPartition = 8 * left < right || 8 * right < left;
         if (m <= med) {
            a = m;
         } else {
            b = m;
         }
      }

      BinaryInsertionSort smallSort = new BinaryInsertionSort(this.arrayVisualizer);
      smallSort.customBinaryInsert(array, a, b, 0.25);
   }

   private void mergeTo(int[] from, int[] to, int a, int m, int b, int p, boolean auxwrite) {
      int i = a;
      int j = m;

      while(i < m && j < b) {
         this.Highlights.markArray(2, i);
         this.Highlights.markArray(3, j);
         if (this.Reads.compareValues(from[i], from[j]) <= 0) {
            this.Writes.write(to, p++, from[i++], 1.0, true, auxwrite);
         } else {
            this.Writes.write(to, p++, from[j++], 1.0, true, auxwrite);
         }
      }

      this.Highlights.clearMark(3);

      while(i < m) {
         this.Highlights.markArray(2, i);
         this.Writes.write(to, p++, from[i++], 1.0, true, auxwrite);
      }

      while(j < b) {
         this.Highlights.markArray(2, j);
         this.Writes.write(to, p++, from[j++], 1.0, true, auxwrite);
      }

      this.Highlights.clearMark(2);
   }

   private void pingPongMerge(int[] array, int[] buf, int a, int m1, int m2, int m3, int b) {
      int p = 0;
      int p1 = p + m2 - a;
      int pEnd = p + b - a;
      this.mergeTo(array, buf, a, m1, m2, p, true);
      this.mergeTo(array, buf, m2, m3, b, p1, true);
      this.mergeTo(buf, array, p, p1, pEnd, a, false);
   }

   private void mergeBWExt(int[] array, int[] tmp, int a, int m, int b) {
      int s = b - m;
      this.Writes.arraycopy(array, m, tmp, 0, s, 1.0, true, true);
      int i = s - 1;
      int j = m - 1;

      while(i >= 0 && j >= a) {
         this.Highlights.markArray(2, j);
         if (this.Reads.compareValues(tmp[i], array[j]) >= 0) {
            this.Writes.write(array, --b, tmp[i--], 1.0, true, false);
         } else {
            this.Writes.write(array, --b, array[j--], 1.0, true, false);
         }
      }

      this.Highlights.clearAllMarks();

      while(i >= 0) {
         this.Writes.write(array, --b, tmp[i--], 1.0, true, false);
      }

   }

   private void blockMerge(int[] array, int[] swap, int a, int m, int b, int p, int bLen, int piv, int pCmp, int bit) {
      if (b - m <= 2 * bLen) {
         this.mergeBWExt(array, swap, a, m, b);
      } else {
         int bCnt = (b - a) / bLen - 2;
         int wLen = this.log2(bCnt - 1) + 1;
         int i = a;
         int j = m;
         int k = 0;
         int l = 0;
         int r = 0;
         int c = 0;

         while(c++ < 2 * bLen) {
            if (this.Reads.compareValues(array[i], array[j]) <= 0) {
               this.Writes.write(swap, k++, array[i++], 1.0, true, true);
               ++l;
            } else {
               this.Writes.write(swap, k++, array[j++], 1.0, true, true);
               ++r;
            }
         }

         int t = 0;
         int pc = p;
         boolean left = l >= r;
         k = left ? i - l : j - r;
         c = 0;

         do {
            if (i >= m || j != b && this.Reads.compareValues(array[i], array[j]) > 0) {
               this.Writes.write(array, k++, array[j++], 1.0, true, false);
               ++r;
            } else {
               this.Writes.write(array, k++, array[i++], 1.0, true, false);
               ++l;
            }

            if (++c == bLen) {
               this.pivBufSet(array, k - bLen, pc, t++, wLen);
               pc += bLen;
               if (left) {
                  l -= bLen;
               } else {
                  r -= bLen;
               }

               left = l >= r;
               k = left ? i - l : j - r;
               c = 0;
            }
         } while(i < m || j < b);

         int b1 = b - c;
         this.Writes.arraycopy(array, k - c, array, b1, c, 1.0, true, false);
         r -= c;
         this.Writes.arraycopy(array, a, array, m - l, l, 1.0, true, false);
         this.Writes.reversearraycopy(array, a + l, array, b1 - r, r, 1.0, true, false);
         this.Writes.reversearraycopy(swap, 0, array, a, 2 * bLen, 1.0, true, false);
         this.blockCycle(array, a + 2 * bLen, bCnt, p, bLen, wLen, piv, pCmp, bit);
      }
   }

   private void logMerge(int[] array, int[] aux, int a, int b, int p, int bLen, int piv, int pCmp, int bit) {
      int j = 16;
      BinaryInsertionSort smallSort = new BinaryInsertionSort(this.arrayVisualizer);

      for(int i = a; i < b; i += j) {
         smallSort.customBinaryInsert(array, i, Math.min(i + j, b), 0.25);
      }

      for(; 2 * j <= bLen; j *= 4) {
         int i;
         for(i = a; i + 2 * j < b; i += 4 * j) {
            this.pingPongMerge(array, aux, i, i + j, i + 2 * j, Math.min(i + 3 * j, b), Math.min(i + 4 * j, b));
         }

         if (i + j < b) {
            this.mergeBWExt(array, aux, i, i + j, b);
         }
      }

      while(j <= 2 * bLen) {
         for(int i = a; i + j < b; i += 2 * j) {
            this.mergeBWExt(array, aux, i, i + j, Math.min(i + 2 * j, b));
         }

         j *= 2;
      }

      while(j < b - a) {
         for(int i = a; i + j < b; i += 2 * j) {
            this.blockMerge(array, aux, i, i + j, Math.min(i + 2 * j, b), p, bLen, piv, pCmp, bit);
         }

         j *= 2;
      }

   }

   @Override
   public void runSort(int[] array, int length, int bucketCount) {
      int bLen = Math.max(this.minLog(length), Math.min(bucketCount, (length + 3) / 4));
      bLen = 1 << this.log2(bLen - 1) + 1;
      int[] aux = this.Writes.createExternalArray(2 * bLen);
      int a = 0;
      int m1 = a + length / 2;
      int m2 = m1 + (length & 1);
      this.selectMedian(array, aux, a, length, bLen);

      int piv;
      for(piv = array[m1]; this.Reads.compareIndices(array, m1 - 1, m2, 1.0, true) == 0; ++m2) {
         --m1;
      }

      int pCmp = this.Reads.compareIndexValue(array, m1 - 1, piv, 1.0, true) == 0 ? 1 : 0;
      this.logMerge(array, aux, a, m1, m2, bLen, piv, pCmp, 0);
      this.logMerge(array, aux, m2, length, a, bLen, piv, pCmp, 1);
   }
}
