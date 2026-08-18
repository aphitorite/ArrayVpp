package io.github.arrayv.sorts.exchange;

import java.util.ArrayList;
import java.util.Stack;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class FalseShellSort extends BogoSorting {
   public FalseShellSort(ArrayVisualizer arrayVisualizer) {
      super(arrayVisualizer);
      this.setSortListName("False Shell");
      this.setRunAllSortsName("False Shell Sort");
      this.setRunSortName("False Shellsort");
      this.setCategory("Exchange Sorts");
      this.setAuthors("Distray");
      this.setBucketSort(false);
      this.setRadixSort(false);
      this.setUnreasonablySlow(false);
      this.setUnreasonableLimit(0);
      this.setBogoSort(false);
   }

   @Override
   public void runSort(int[] array, int currentLength, int buckets) {
      int g = currentLength / 32;
      int s = g + 1;

      int e;
      do {
         Stack<Integer> m = new Stack<>();
         int f = 0;
         int i = 0;

         for(int j = 0; i < currentLength; ++i) {
            if (m.empty() || this.Reads.compareIndices(array, i, m.peek(), 1.0, true) >= 0) {
               if (!m.empty() && j + g >= i) {
                  ++f;
               } else {
                  j = i;
                  m.push(i);
               }
            }
         }

         ArrayList<Integer> gaps = new ArrayList<>();
         if (s <= g) {
             gaps.add(s | 1);
         } else 
             gaps.add(s);

         if (f > g || s <= g) {
            g /= 2;
         }

         e = f + m.size();
         int t = m.pop();

         while(!m.empty()) {
            gaps.add(t - (t = m.pop()));
         }

         gaps.add(currentLength / (2 * e) + 1);
         s = 0;

         for(int ix = 0; ix < currentLength; ++ix) {
            int k = ix;

            for(int j : gaps) {
               int l = k;
               k -= j;
               if (k < 0 || this.Reads.compareIndices(array, k, l, 0.1, true) <= 0) {
                  break;
               }

               this.Writes.swap(array, k, l, 0.0, true, false);
               ++s;
            }
         }
      } while(e < currentLength);

   }
}
