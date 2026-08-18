package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.PDBinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

public final class SingularityQuickSort extends Sort {
   int depthlimit;
   int insertlimit;
   int replimit;

   public SingularityQuickSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("Singularity Quick");
      this.setRunAllSortsName("Singularity Quick Sort");
      this.setRunSortName("Singularity Quicksort");
      this.setCategory("Quick Sorts");
      this.setAuthors("PCBoy");
      this.setConstant("n log n");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
        this.setAuthors("PCBoy");
   }

   protected int log2(int x) {
      int n = 1;

      while(1 << n < x) {
         ++n;
      }

      if (1 << n > x) {
         --n;
      }

      return n;
   }

   protected void stableSegmentReversal(int[] array, int start, int end) {
      if (end - start < 3) {
         this.Writes.swap(array, start, end, 0.075, true, false);
      } else {
         this.Writes.reversal(array, start, end, 0.075, true, false);
      }

      for(int i = start; i < end; ++i) {
         int left = i;

         while(this.Reads.compareIndices(array, i, i + 1, 0.25, true) == 0 && i < end) {
            ++i;
         }

         if (left != i) {
            if (i - left < 3) {
               this.Writes.swap(array, left, i, 0.75, true, false);
            } else {
               this.Writes.reversal(array, left, i, 0.75, true, false);
            }
         }
      }

   }

   protected int pd(int[] array, int start, int end) {
      int reverse = start;
      boolean lessunique = false;
      boolean different = false;

      for(int cmp = this.Reads.compareIndices(array, start, start + 1, 0.5, true);
         cmp >= 0 && reverse + 1 < end;
         cmp = this.Reads.compareIndices(array, ++reverse, reverse + 1, 0.5, true)
      ) {
         if (cmp == 0) {
            lessunique = true;
         } else {
            different = true;
         }
      }

      if (reverse > start && different) {
         if (lessunique) {
            this.stableSegmentReversal(array, start, reverse);
         } else if (reverse < start + 3) {
            this.Writes.swap(array, start, reverse, 0.75, true, false);
         } else {
            this.Writes.reversal(array, start, reverse, 0.75, true, false);
         }
      }

      return reverse;
   }

   protected void binsert(int[] array, int start, int end) {
      PDBinaryInsertionSort bin = new PDBinaryInsertionSort(this.arrayVisualizer);
      bin.pdbinsert(array, start - 1, end, 0.1, false);
   }

   protected void singularityQuick(int[] array, int start, int offset, int end, int depth, int rep) {
      this.Writes.recordDepth((long)depth);
      this.Highlights.clearAllMarks();
      if (end - start > this.insertlimit && depth < this.depthlimit && rep < 4) {
         int left = offset;

         while(this.Reads.compareIndices(array, left - 1, left, 0.05, true) <= 0 && left < end) {
            ++left;
         }

         if (left < end) {
            int right = left + 1;
            int pull = 1;
            int pivot = array[left - 1];
            int originalpos = left - 1;
            boolean brokeloop = false;

            for(boolean brokencond = false; right <= end; ++right) {
               if (this.Reads.compareValues(pivot, array[right - 1]) > 0) {
                  this.Highlights.clearMark(2);
                  if (right - left == 1) {
                     this.Writes.write(array, left - 1, array[left], 0.1, true, false);
                  } else {
                     brokeloop = true;
                  }

                  if (brokeloop && !brokencond) {
                     this.Writes.write(array, left - 1, pivot, 0.1, true, false);
                     brokencond = true;
                  }

                  if (right - left > 1) {
                     pull = right - 1;
                     int item = array[pull];
                     this.Highlights.clearMark(2);

                     while(pull >= left) {
                        this.Writes.write(array, pull, array[pull - 1], 0.1, true, false);
                        --pull;
                     }

                     this.Writes.write(array, pull, item, 0.1, true, false);
                  }

                  ++left;
               }
            }

            if (right > end && !brokeloop) {
               this.Writes.write(array, left - 1, pivot, 0.1, true, false);
            }

            boolean lsmall = left - start < end - (left + 1);
            if (lsmall && left - 1 - start > 0) {
               this.Writes.recursion();
               if (end - this.replimit > left && left > start + this.replimit) {
                  this.singularityQuick(array, start, originalpos - 1 > start ? originalpos - 1 : start, left - 1, depth + 1, 0);
               } else {
                  this.singularityQuick(array, start, originalpos - 1 > start ? originalpos - 1 : start, left - 1, depth + 1, rep + 1);
               }
            }

            if (end - (left + 1) > 0) {
               this.Writes.recursion();
               if (end - this.replimit > left && left > start + this.replimit) {
                  this.singularityQuick(array, left + 1, left + 1, end, depth + 1, 0);
               } else {
                  this.singularityQuick(array, left + 1, left + 1, end, depth + 1, rep + 1);
               }
            }

            if (!lsmall && left - 1 - start > 0) {
               this.Writes.recursion();
               if (end - this.replimit > left && left > start + this.replimit) {
                  this.singularityQuick(array, start, originalpos - 1 > start ? originalpos - 1 : start, left - 1, depth + 1, 0);
               } else {
                  this.singularityQuick(array, start, originalpos - 1 > start ? originalpos - 1 : start, left - 1, depth + 1, rep + 1);
               }
            }
         }
      } else {
         this.binsert(array, start, end);
      }

   }

   @Override
   public void runSort(int[] array, int currentLength, int bucketCount) {
      this.depthlimit = (int)Math.min(Math.sqrt((double)currentLength), (double)(2 * this.log2(currentLength)));
      this.insertlimit = Math.max(this.depthlimit / 2 - 1, 15);
      this.replimit = Math.max(this.depthlimit / 4, 2);
      int realstart = this.pd(array, 0, currentLength);
      if (realstart + 1 < currentLength) {
         this.singularityQuick(array, 1, realstart + 1, currentLength, 0, 0);
      }

   }
}
