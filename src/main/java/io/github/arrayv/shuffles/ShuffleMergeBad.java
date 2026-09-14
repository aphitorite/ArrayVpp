package io.github.arrayv.shuffles;

import java.util.*;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.shuffles.templates.Shuffle;
import io.github.arrayv.sorts.select.MaxHeapSort;
import io.github.arrayv.sorts.select.PoplarHeapSort;
import io.github.arrayv.sorts.select.SmoothSort;
import io.github.arrayv.sorts.select.TriangularHeapSort;
import io.github.arrayv.sorts.templates.PDQSorting;
import io.github.arrayv.utils.*;

public class ShuffleMergeBad extends Shuffle {

        public String getName() {
            return "Shuffle Merge Adversary";
        }
        @Override
        public void shuffleArray(int[] array) {
            int n = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            int[] tmp = new int[n];
            int d = 2, end = 1 << (int)(Math.log(n-1)/Math.log(2) + 1);

            while (d <= end) {
                int i = 0, dec = 0;
                double sleep = 1d/d;

                while (i < n) {
                    int j = i;
                    dec += n;
                    while (dec >= d) {
                        dec -= d;
                        j++;
                    }
                    int k = j;
                    dec += n;
                    while (dec >= d) {
                        dec -= d;
                        k++;
                    }
                    shuffleMergeBad(array, tmp, i, j, k, delay ? sleep : 0, Writes);
                    i = k;
                }
                d *= 2;
            }
        }

        public void shuffleMergeBad(int[] array, int[] tmp, int a, int m, int b, double sleep, Writes Writes) {
            if ((b-a)%2 == 1) {
                if (m-a > b-m) a++;
                else           b--;
            }
            shuffleBad(array, tmp, a, b, sleep, Writes);
        }

        //length is always even
        public void shuffleBad(int[] array, int[] tmp, int a, int b, double sleep, Writes Writes) {
            if (b-a < 2) return;

            int m = (a+b)/2;
            int s = (b-a-1)/4+1;

            a = m-s;
            b = m+s;
            int j = a;

            for (int i = a+1; i < b; i += 2)
                Writes.write(tmp, j++, array[i], sleep, true, true);
            for (int i = a; i < b; i += 2)
                Writes.write(tmp, j++, array[i], sleep, true, true);

            Writes.arraycopy(tmp, a, array, a, b-a, sleep, true, false);
        }
    
}
